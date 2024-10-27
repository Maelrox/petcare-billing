package com.petcaresuite.billing.infrastructure.persistence.adapter

import com.petcaresuite.billing.application.port.output.BillingPersistencePort
import com.petcaresuite.billing.domain.model.Billing
import com.petcaresuite.billing.infrastructure.persistence.mapper.BillingEntityMapper
import com.petcaresuite.billing.infrastructure.persistence.repository.JpaBillingRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class BillingRepositoryAdapter(
    private val jpaBillingRepository: JpaBillingRepository,
    private val billingMapper: BillingEntityMapper
) : BillingPersistencePort {

    override fun findById(inventoryId: Long): Billing {
        val ownerEntity = jpaBillingRepository.findById(inventoryId)
            .orElseThrow { EntityNotFoundException("Inventory with id $inventoryId not found") }
        return billingMapper.toDomain(ownerEntity)
    }

    override fun update(billing: Billing): Billing {
        val billingEntity = billingMapper.toEntity(billing)
        jpaBillingRepository.save(billingEntity)
        return billingMapper.toDomain(billingEntity)
    }

    override fun save(billing: Billing): Billing {
        val billingEntity = billingMapper.toEntity(billing)
        billingEntity.billingDetails.forEach { detail ->
            detail.billing = billingEntity
        }
        jpaBillingRepository.save(billingEntity)
        return billingMapper.toDomain(billingEntity)
    }

    override fun findAllByFilterPaginated(filter: Billing, pageable: Pageable): Page<Billing> {
        val pagedRolesEntity = jpaBillingRepository.findAllByFilter(filter, pageable)
        return pagedRolesEntity.map { billingMapper.toDomain(it) }
    }

}