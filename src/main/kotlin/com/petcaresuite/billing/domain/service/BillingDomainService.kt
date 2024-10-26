package com.petcaresuite.billing.domain.service

import com.petcaresuite.billing.application.port.output.BillingPersistencePort
import org.springframework.stereotype.Service

@Service
class BillingDomainService(private val billingPersistencePort: BillingPersistencePort) {

    fun validateAppointment(name: String, id: Long) {

    }

}