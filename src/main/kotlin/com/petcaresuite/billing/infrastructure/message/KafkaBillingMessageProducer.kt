package com.petcaresuite.billing.infrastructure.message

import com.fasterxml.jackson.databind.ObjectMapper
import com.petcaresuite.billing.application.dto.BillingDTO
import com.petcaresuite.billing.application.port.output.BillingMessageProducerPort
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaBillingMessageProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>
) : BillingMessageProducerPort {

    private val objectMapper = ObjectMapper()

    override fun sendBillingMessage(billingDTO: BillingDTO) {
        try {
            val jsonString = objectMapper.writeValueAsString(billingDTO)
            kafkaTemplate.send("billing-topic", jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
