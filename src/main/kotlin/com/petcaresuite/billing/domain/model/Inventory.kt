package com.petcaresuite.inventory.domain.model

import java.math.BigDecimal

data class Inventory(
    val inventoryId: Long?,
    val name: String?,
    val description: String?,
    val price: BigDecimal?,
    var quantity: Int?,
    val companyId: Long?,
)