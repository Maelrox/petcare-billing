package com.petcaresuite.billing.infrastructure.persistence.repository

import com.petcaresuite.billing.domain.model.Billing
import com.petcaresuite.billing.infrastructure.persistence.entity.BillingEntity
import com.petcaresuite.billing.infrastructure.persistence.entity.BillingProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface JpaBillingRepository : JpaRepository<BillingEntity, Long> {

    @Query(
        value = """
            SELECT b.billing_id as billingId, b.total_amount as totalAmount, b.payment_status as paymentStatus,
                   b.transaction_date as transactionDate, o.identification as identification
            FROM billing b 
            JOIN owners o ON o.owner_id = b.owner_id
            WHERE b.company_id = :#{#filter.companyId}
            AND (COALESCE(:#{#filter.identification}, '') = '' OR o.identification = :#{#filter.identification})
            AND (COALESCE(:#{#filter.paymentStatus}, '') = '' OR b.payment_status = :#{#filter.paymentStatus})
            AND (COALESCE(:#{#filter.billingId}, 0) = 0 OR b.billing_id = :#{#filter.billingId})
            AND (b.transaction_date >= :#{#filter.initialDate})
            AND (b.transaction_date <= :#{#filter.finalDate}) 
            ORDER BY b.billing_id desc
        """,
        nativeQuery = true
    )
    fun findAllByFilter(filter: Billing, pageable: Pageable): Page<BillingProjection>

    fun findByBillingIdAndCompanyId(billingId: Long, companyId: Long): BillingEntity

}
