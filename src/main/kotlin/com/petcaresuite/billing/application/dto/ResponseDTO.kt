package com.petcaresuite.billing.application.dto

data class ResponseDTO(
    val success: Boolean?,
    val message: String?,
) {
    constructor(message: String) : this(true, message)

    companion object {
        fun generateSuccessResponse(isSuccess: Boolean, message: String): ResponseDTO {
            return ResponseDTO(
                success = isSuccess,
                message = message
            )
        }
    }
}