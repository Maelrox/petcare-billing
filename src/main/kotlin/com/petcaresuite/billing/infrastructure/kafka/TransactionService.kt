package com.petcaresuite.billing.infrastructure.kafka

import com.petcaresuite.billing.infrastructure.persistence.entity.BillingTransactionEntity
import com.petcaresuite.billing.infrastructure.persistence.repository.JpaTransactionRepository
import org.springframework.stereotype.Service

@Service
class TransactionService(private val jpaTransactionRepository: JpaTransactionRepository) {

    fun checkStatus(trxId: String, companyId: Long): BillingTransactionEntity? {
        return jpaTransactionRepository.findByTrxIdAndCompanyId(trxId, companyId)
    }

}