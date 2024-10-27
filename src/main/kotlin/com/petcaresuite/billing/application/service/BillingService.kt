package com.petcaresuite.billing.application.service

import com.petcaresuite.billing.application.dto.*
import com.petcaresuite.billing.application.mapper.BillingMapper
import com.petcaresuite.billing.application.port.input.BillingUseCase
import com.petcaresuite.billing.application.port.output.BillingPersistencePort
import com.petcaresuite.billing.application.port.output.BillingMessageProducerPort
import com.petcaresuite.billing.application.service.messages.Responses
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
import java.time.LocalDateTime
import java.util.*

@Service
class BillingService(
    private val billingPersistencePort: BillingPersistencePort,
    private val billingMapper: BillingMapper,
    private val billingMessageProducerPort: BillingMessageProducerPort,
    private val inventoryClient: InventoryClient
) : BillingUseCase {

    override fun save(billingDTO: BillingDTO): ResponseDTO {
        billingDTO.ownerId = billingDTO.owner.ownerId
        billingDTO.transactionDate = LocalDateTime.now().toString()
        billingDTO.transactionType = "BILL"
        billingDTO.trxId = UUID.randomUUID().toString()
        billingMessageProducerPort.sendBillingMessage(billingDTO)
        return ResponseDTO(message = Responses.BILLING_CREATED, trxId = billingDTO.trxId)
    }

    override fun update(billingDTO: BillingDTO): ResponseDTO? {
        val billing = billingMapper.toDomain(billingDTO)
        billingPersistencePort.findById(billingDTO.billingId!!)
        billingPersistencePort.update(billing)
        billingMessageProducerPort.sendBillingMessage(billingDTO)
        return ResponseDTO(message = Responses.BILLING_UPDATED, trxId = billingDTO.trxId)
    }

    override fun getAllByFilterPaginated(filterDTO: BillingFilterDTO, pageable: Pageable): Page<BillingDTO> {
        val filter = billingMapper.toDomain(filterDTO)
        return billingPersistencePort.findAllByFilterPaginated(filter, pageable)
            .map { billingMapper.toDTO(it) }
    }


    @Transactional
    override fun processBilling(billingDTO: BillingDTO): ResponseDTO {
        val inventories = billingDTO.billingDetails
            ?.filter { billingDetail -> billingDetail.inventoryId != null }
            ?.map { billingDetail ->
                InventoryDTO(
                    inventoryId = billingDetail.inventoryId,
                    name = billingDetail.name,
                    description = billingDetail.description,
                    quantity = billingDetail.quantity,
                    price = billingDetail.amount,
                    companyId = billingDTO.companyId
                )
            } ?: emptyList()

        val retryTemplate = RetryTemplate().apply {
            setRetryPolicy(customRetryPolicy())
        }
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
        try {
            val billing = billingMapper.toDomain(billingDTO)
            billing.transactionDate = LocalDateTime.now()
            billing.transactionType = "BILL"
            billingPersistencePort.save(billing)
            return ResponseDTO(message = Responses.BILLING_CREATED, trxId = billingDTO.trxId)
        } catch (e: Exception) {
            //TODO: Apply SAGA way
            //TODO: revertInventoryChanges(billingDTO)
            throw e
        }
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
