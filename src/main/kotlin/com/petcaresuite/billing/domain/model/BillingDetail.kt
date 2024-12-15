package com.petcaresuite.billing.domain.model

import java.math.BigDecimal

data class BillingDetail(
    val billingDetailId: Long?,
    val inventoryId: Long?,
    var consultationId: Long?,
    val quantity: Int?,
    val amount: BigDecimal?
)
