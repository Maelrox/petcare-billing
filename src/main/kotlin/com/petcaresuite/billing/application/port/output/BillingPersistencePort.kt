package com.petcaresuite.billing.application.port.output

import com.petcaresuite.billing.domain.model.Billing
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface BillingPersistencePort {

     fun save(billing: Billing): Billing

     fun findAllByFilterPaginated(filter: Billing, pageable: Pageable): Page<Billing>

     fun findById(inventoryId: Long): Billing

     fun update(billing: Billing): Billing

}