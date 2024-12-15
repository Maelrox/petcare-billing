package com.petcaresuite.billing.application.service

import com.petcaresuite.billing.application.dto.*
import com.petcaresuite.billing.application.mapper.BillingMapper
import com.petcaresuite.billing.application.port.input.BillingUseCase
import com.petcaresuite.billing.application.port.output.BillingPersistencePort
import com.petcaresuite.billing.application.port.output.BillingMessageProducerPort
import com.petcaresuite.billing.application.service.messages.Responses
import com.petcaresuite.billing.domain.service.BillingDomainService
import com.petcaresuite.billing.infrastructure.exception.InsufficientInventoryException
import com.petcaresuite.billing.infrastructure.exception.LockAcquisitionException
import com.petcaresuite.billing.infrastructure.rest.InventoryClient
import feign.FeignException
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.retry.RetryPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Service
class BillingService(
    private val billingPersistencePort: BillingPersistencePort,
    private val billingMapper: BillingMapper,
    private val billingDomainService: BillingDomainService,
    private val billingMessageProducerPort: BillingMessageProducerPort,
    private val inventoryClient: InventoryClient
) : BillingUseCase {

    val retryTemplate = RetryTemplate().apply {
        setRetryPolicy(customRetryPolicy())
    }

    override fun save(billingDTO: BillingDTO): ResponseDTO {
        billingDTO.ownerId = billingDTO.owner?.ownerId
        billingDTO.transactionDate = LocalDateTime.now().toString()
        billingDTO.transactionType = "BILL"
        billingDTO.trxId = UUID.randomUUID().toString()
        billingMessageProducerPort.sendBillingMessage(billingDTO)
        return ResponseDTO(success = true, Responses.BILLING_CREATED, trx = billingDTO.trxId)
    }

    override fun update(billingDTO: BillingDTO): ResponseDTO? {
        val billing = billingMapper.toDomain(billingDTO)
        billingPersistencePort.findById(billingDTO.billingId!!)
        billingPersistencePort.update(billing)
        billingMessageProducerPort.sendBillingMessage(billingDTO)
        return ResponseDTO(success = true, message = Responses.BILLING_UPDATED, trx = billingDTO.trxId)
    }

    override fun getAllByFilterPaginated(filterDTO: BillingFilterDTO, pageable: Pageable): Page<BillingDTO> {
        val filter = billingMapper.toDomain(filterDTO)
        return billingPersistencePort.findAllByFilterPaginated(filter, pageable)
            .map { billingMapper.toDTO(it) }
    }

    @Transactional
    override fun processBilling(billingDTO: BillingDTO): ResponseDTO {
        val inventory = generateInventories(billingDTO, false)
        updateInventory(inventory, billingDTO)
        try {
            val billing = billingMapper.toDomain(billingDTO)
            billing.transactionDate = LocalDateTime.now()
            billing.paymentStatus = "PAID"
            billingPersistencePort.save(billing)
            return ResponseDTO(success = true, message = Responses.BILLING_CREATED, trx = billingDTO.trxId)
        } catch (e: Exception) {
            val inventoryRollback = generateInventories(billingDTO, true)
            updateInventory(inventoryRollback, billingDTO)
            throw e
        }
    }

    override fun cancel(billingId: Long, companyId: Long, authorization: String): ResponseDTO {
        val billing = billingPersistencePort.findByIdAndCompanyId(billingId, companyId)
        billingDomainService.validateCancellation(billing)
        val billingDTO = billingMapper.toDTO(billing)
        billingDTO.authorization = authorization
        billingDTO.transactionDate = LocalDateTime.now().toString()
        billingDTO.transactionType = "CANCELLATION"
        billingDTO.trxId = UUID.randomUUID().toString()
        billingMessageProducerPort.sendBillingMessage(billingDTO)
        return ResponseDTO(success = true, message = Responses.BILLING_CANCELLED, trx = billingDTO.trxId)
    }

    override fun processCancellation(billingDTO: BillingDTO): ResponseDTO {
        val billing = billingMapper.toDomain(billingDTO)
        billing.billingDetails
            ?.filter { billingDetail -> billingDetail.consultationId != null }
            ?.forEach { it.consultationId = null }

        val inventory = generateInventories(billingDTO, true)
        updateInventory(inventory, billingDTO)

        billing.transactionDate = LocalDateTime.now()
        billing.paymentStatus = "REVERTED"
        billingPersistencePort.save(billing)
        return ResponseDTO(success = true, message = Responses.BILLING_CANCELLED, trx = billingDTO.trxId)
    }

    fun updateInventory(inventories: List<InventoryDTO>, billingDTO: BillingDTO) {
        inventories.isNotEmpty().let {
            val inventoryResponse = retryTemplate.execute<ResponseDTO, Exception> {
                try {
                    inventoryClient.update(inventories, billingDTO.authorization!!)
                } catch (e: FeignException.Conflict) {
                    when {
                        e.message?.contains("Resource locked") == true -> {
                            throw LockAcquisitionException("Resource is currently locked")
                        }
                        else -> {
                            throw InsufficientInventoryException("Inventory not available")
                        }
                    }
                }
            }
            if (!inventoryResponse.success!!) {
                throw RuntimeException("Failed to update inventory: ${inventoryResponse.message}")
            }
        }
    }

    fun generateInventories(billingDTO: BillingDTO, minus: Boolean): List<InventoryDTO> {
        return billingDTO.billingDetails
            ?.filter { billingDetail -> billingDetail.inventoryId != null }
            ?.map { billingDetail ->
                InventoryDTO(
                    inventoryId = billingDetail.inventoryId,
                    name = billingDetail.name ?: "Unnamed Item",
                    description = billingDetail.description ?: "No description",
                    quantity = if (minus) {
                        ((billingDetail.quantity) * -1)
                    } else {
                        billingDetail.quantity
                    },
                    price = billingDetail.amount,
                    companyId = billingDTO.companyId
                )
            } ?: emptyList()
    }

    fun customRetryPolicy(): RetryPolicy {
        val policyMap = mapOf<Class<out Throwable>, Boolean>(
            FeignException.Conflict::class.java to true,
            EntityNotFoundException::class.java to false,
            InsufficientInventoryException::class.java to false,
        )
        return SimpleRetryPolicy(3, policyMap)
    }
}
