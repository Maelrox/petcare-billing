package com.petcaresuite.billing.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class Billing(
    val billingId: Long?,
    val totalAmount: BigDecimal?,
    var paymentStatus: String?,
    var transactionType: String?,
    var transactionDate: LocalDateTime?,
    val billingDetails: List<BillingDetail>?,
    val companyId: Long?,
    var ownerId: Long?,
    val identification: String?,
    val initialDate: LocalDateTime?,
    val finalDate: LocalDateTime?,
)
