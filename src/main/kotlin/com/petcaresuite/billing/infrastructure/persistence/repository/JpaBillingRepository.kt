package com.petcaresuite.billing.infrastructure.persistence.repository

import com.petcaresuite.billing.domain.model.Billing
import com.petcaresuite.billing.infrastructure.persistence.entity.BillingEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface JpaBillingRepository : JpaRepository<BillingEntity, Long> {

    @Query(
        """
            SELECT i FROM BillingEntity i
            WHERE i.companyId = :#{#filter.companyId}
            ORDER BY i.billingId desc 
            """
    )
    fun findAllByFilter(filter: Billing, pageable: Pageable): Page<BillingEntity>

}
