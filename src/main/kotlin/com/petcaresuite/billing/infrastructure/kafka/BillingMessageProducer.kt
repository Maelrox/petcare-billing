package com.petcaresuite.billing.infrastructure.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.petcaresuite.billing.application.dto.BillingDTO
import com.petcaresuite.billing.application.port.output.BillingMessageProducerPort
import com.petcaresuite.billing.infrastructure.persistence.entity.BillingTransactionEntity
import com.petcaresuite.billing.infrastructure.persistence.repository.JpaTransactionRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class BillingMessageProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>, private val jpaTransactionRepository: JpaTransactionRepository
) : BillingMessageProducerPort {

    private val logger: Logger = LoggerFactory.getLogger(BillingMessageProducer::class.java)

    private val objectMapper = ObjectMapper()

    override fun sendBillingMessage(billingDTO: BillingDTO) {
        try {
            val transaction = BillingTransactionEntity(
                trxId = billingDTO.trxId!!,
                billing = null,
                status = "IN QUEUE",
                response = null,
                companyId = billingDTO.companyId,
            )
            jpaTransactionRepository.save(transaction)
            val jsonString = objectMapper.writeValueAsString(billingDTO)
            kafkaTemplate.send("billing-topic", jsonString)
        } catch (e: Exception) {
            logger.error("Error sending message to billing-topic" + e.message)
            e.printStackTrace()
        }
    }

}
