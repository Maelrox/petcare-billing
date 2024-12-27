package com.petcaresuite.billing.domain.model

data class Owner(
    val ownerId: Long?,
    val identification: String?,
    val name: String?,
    val address: String?,
    val phone: String?,
    val identificationType: IdentificationType?,
    val companyId: Long?,
)