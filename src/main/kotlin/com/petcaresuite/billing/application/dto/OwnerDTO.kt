package com.petcaresuite.billing.application.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class OwnerDTO @JsonCreator constructor(
    @JsonProperty("ownerId")
    val ownerId: Long?
)