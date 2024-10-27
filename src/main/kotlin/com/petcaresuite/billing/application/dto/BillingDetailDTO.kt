package com.petcaresuite.billing.application.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BillingDetailDTO(
    val inventoryId: Long?,
    val consultationId: Long?,
    val name: String,
    val description: String,
    val quantity: Int,
    val amount: BigDecimal,
    var companyId: Long?,
)