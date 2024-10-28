package com.petcaresuite.billing.infrastructure.persistence.entity

import java.math.BigDecimal
import java.time.LocalDateTime

interface BillingProjection {
    val billingId: Long
    val totalAmount: BigDecimal
    val paymentStatus: String
    val transactionDate: LocalDateTime
    val identification: String?
}