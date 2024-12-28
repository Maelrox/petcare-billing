package com.petcaresuite.inventory.application.dto

import java.math.BigDecimal

data class ServiceDTO(
    val id: Long = 0,
    val name: String?,
    val description: String? = null,
    val price: BigDecimal,
    var companyId: Long? = null
) {

}