package com.petcaresuite.billing.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class Billing(
    val billingId: Long?,
    val totalAmount: BigDecimal?,
    val paymentStatus: String?,
    val transactionType: String?,
    var transactionDate: LocalDateTime?,
    val billingDetails: List<BillingDetail>?,
    val companyId: Long?,
    var ownerId: Long?,
)
