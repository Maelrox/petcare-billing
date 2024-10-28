package com.petcaresuite.billing.application.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BillingFilterDTO(
    val billingId: Long?,
    val paymentStatus: String?,
    val transactionType: String?,
    val transactionDate: String?,
    var companyId: Long?,
    val ownerId: Long?,
    val identification: String?,
    val initialDate: LocalDateTime?,
    val finalDate: LocalDateTime?,
)