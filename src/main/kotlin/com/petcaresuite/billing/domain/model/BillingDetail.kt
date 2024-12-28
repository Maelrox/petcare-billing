package com.petcaresuite.billing.domain.model

import com.petcaresuite.inventory.domain.model.Inventory
import java.math.BigDecimal

data class BillingDetail(
    val billingDetailId: Long?,
    val inventory: Inventory?,
    var consultation: Consultation?,
    val quantity: Int?,
    val amount: BigDecimal?
)
