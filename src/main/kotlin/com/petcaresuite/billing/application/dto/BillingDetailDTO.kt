package com.petcaresuite.billing.application.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BillingDetailDTO(
    val billingDetailId: Long?,
    val inventory: InventoryDTO?,
    var consultation: ConsultationDTO?,
    val quantity: Int,
    val amount: BigDecimal,
    var companyId: Long?,
    val name: String?,
    val description: String?,
)