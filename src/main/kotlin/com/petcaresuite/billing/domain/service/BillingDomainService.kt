package com.petcaresuite.billing.domain.service

import com.petcaresuite.billing.application.dto.InventoryDTO
import com.petcaresuite.billing.application.dto.ResponseDTO
import com.petcaresuite.billing.application.port.output.BillingPersistencePort
import com.petcaresuite.billing.application.service.messages.Responses
import com.petcaresuite.billing.domain.model.Billing
import com.petcaresuite.billing.infrastructure.exception.InsufficientInventoryException
import com.petcaresuite.billing.infrastructure.exception.LockAcquisitionException
import feign.FeignException
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Service

@Service
class BillingDomainService(private val billingPersistencePort: BillingPersistencePort) {

    fun rollBackInventory(name: String, id: Long) {

    }

    fun validateCancellation(billing: Billing) {
        if (billing.paymentStatus.equals("REVERTED")) {
            throw IllegalArgumentException(Responses.BILLING_ALREADY_CANCELLED);
        }
    }

}