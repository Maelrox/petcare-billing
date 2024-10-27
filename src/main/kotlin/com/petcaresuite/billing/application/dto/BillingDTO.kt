package com.petcaresuite.billing.application.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BillingDTO(
    val billingId: Long?,
    val totalAmount: BigDecimal,
    var paymentStatus: String?,
    var transactionType: String?,
    var transactionDate: String?,
    val billingDetails: List<BillingDetailDTO>?,
    var companyId: Long?,
    var authorization: String?,
    var ownerId: Long?,
    var owner: OwnerDTO,
    var trxId: String?
) {
}