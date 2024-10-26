package com.petcaresuite.billing.application.mapper

import com.petcaresuite.billing.application.dto.BillingDTO
import com.petcaresuite.billing.application.dto.BillingDetailDTO
import com.petcaresuite.billing.application.dto.BillingFilterDTO
import com.petcaresuite.billing.domain.model.Billing
import com.petcaresuite.billing.domain.model.BillingDetail
import org.mapstruct.*

@Mapper(componentModel = "spring")
interface BillingMapper {

    @Mapping(source = "billingDetails", target = "billingDetails")
    fun toDomain(billingDTO: BillingDTO): Billing

    @Mapping(source = "billingDetails", target = "billingDetails")
    fun toDTO(billing: Billing): BillingDTO

    fun toDTOList(billings: List<Billing>): List<BillingDTO>

    fun toDomain(filterDTO: BillingFilterDTO): Billing

    fun toDomain(billingDetailDTO: BillingDetailDTO): BillingDetail
    fun toDTO(billingDetail: BillingDetail): BillingDetailDTO

    fun toDomainDetails(details: List<BillingDetailDTO>): List<BillingDetail>
    fun toDTOListDetails(details: List<BillingDetail>): List<BillingDetailDTO>
}
