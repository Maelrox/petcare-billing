package com.petcaresuite.billing.infrastructure.persistence.repository

import com.petcaresuite.billing.infrastructure.persistence.entity.BillingTransactionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface JpaTransactionRepository : JpaRepository<BillingTransactionEntity, Long> {

    fun findByTrxIdAndCompanyId(trxId: String, companyId: Long): BillingTransactionEntity?

}
