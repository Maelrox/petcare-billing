package com.petcaresuite.billing.infrastructure.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.petcaresuite.billing.application.dto.BillingDTO
import com.petcaresuite.billing.application.service.BillingService
import com.petcaresuite.billing.infrastructure.persistence.entity.BillingTransactionEntity
import com.petcaresuite.billing.infrastructure.persistence.repository.JpaTransactionRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class BillingKafkaListener(
    private val billingService: BillingService,
    private val objectMapper: ObjectMapper,
    private val jpaTransactionRepository: JpaTransactionRepository
) {

    @KafkaListener(topics = ["billing-topic"], groupId = "billing-group")
    fun listen(billingJson: String) {
        val billingDTO: BillingDTO?
        var transaction: BillingTransactionEntity? = null
        try {

            billingDTO = objectMapper.readValue(billingJson, BillingDTO::class.java)
            transaction = jpaTransactionRepository.findByTrxIdAndCompanyId(billingDTO.trxId!!, billingDTO.companyId!!)
            transaction?.let {
                it.status = "PROCESSING"
                it.response = ""
                jpaTransactionRepository.save(it)
            }
            transaction?.let { jpaTransactionRepository.save(transaction) }
            billingService.processBilling(billingDTO)
            transaction?.status = "DONE"
            if (transaction != null) {
                jpaTransactionRepository.save(transaction)
            }
        } catch (e: Exception) {
            transaction?.status = "FAILED"
            transaction?.response = e.message
            transaction?.let { jpaTransactionRepository.save(it) }
        }
    }

}

