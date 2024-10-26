package com.petcaresuite.billing.application.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class InventoryDTO(
    val inventoryId: Long?,
)