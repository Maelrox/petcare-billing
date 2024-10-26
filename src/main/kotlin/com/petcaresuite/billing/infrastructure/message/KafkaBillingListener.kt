package com.petcaresuite.billing.infrastructure.message

import com.fasterxml.jackson.databind.ObjectMapper
import com.petcaresuite.billing.application.dto.BillingDTO
import com.petcaresuite.billing.application.service.BillingService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class BillingKafkaListener(private val billingService: BillingService, private val objectMapper: ObjectMapper) {


    @KafkaListener(topics = ["billing-topic"], groupId = "billing-group")
    fun listen(billingJson: String) {
        var billingDTO: BillingDTO? = null
        try {
            billingDTO = objectMapper.readValue(billingJson, BillingDTO::class.java)
            billingService.processBilling(billingDTO)
        } catch (e: Exception) {
            //TODO: Create or update as failed
            e.printStackTrace()
        }
    }

}
