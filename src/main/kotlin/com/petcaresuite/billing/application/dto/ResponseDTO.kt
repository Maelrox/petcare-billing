package com.petcaresuite.billing.application.dto

data class ResponseDTO(
    val success: Boolean?,
    val message: String?,
    val trx: String?
) {
    constructor(message: String, trxId: String?) : this(true, message, trxId)

    companion object {
        fun generateSuccessResponse(isSuccess: Boolean, message: String?, trxId: String?): ResponseDTO {
            return ResponseDTO(
                success = isSuccess,
                message = message,
                trx=  trxId
            )
        }
    }
}