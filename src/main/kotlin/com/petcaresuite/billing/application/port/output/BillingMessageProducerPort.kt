package com.petcaresuite.billing.application.port.output

import com.petcaresuite.billing.application.dto.BillingDTO

interface BillingMessageProducerPort {
    fun sendBillingMessage(billingDTO: BillingDTO)
}
