package com.petcaresuite.billing.domain.model

data class Company(
    val id: Long,
    val name: String,
    val country: String?,
    val companyIdentification: String,
    val address: String? = null,
    val phone: String? = null,
    val email: String? = null,
    var logoUrl: String? = null,
)