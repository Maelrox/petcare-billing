package com.petcaresuite.inventory.domain.model

import java.math.BigDecimal

data class Service(
    val id: Long?,
    val name: String?,
    val description: String? = null,
    val price: BigDecimal?,
    val companyId: Long,
)