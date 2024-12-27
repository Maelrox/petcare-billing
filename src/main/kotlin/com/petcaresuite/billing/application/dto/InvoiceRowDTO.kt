package com.petcaresuite.billing.application.dto

import java.math.BigDecimal

data class InvoiceRowDTO(
    val itemName: String,
    val itemQuantity: String,
    val itemValue: BigDecimal,
    val itemTotal: BigDecimal
)