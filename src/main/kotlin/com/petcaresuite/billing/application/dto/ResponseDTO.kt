package com.petcaresuite.billing.application.dto

data class ResponseDTO(
    val success: Boolean?,
    val message: String?,
    val trx: String?
)