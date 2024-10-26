package com.petcaresuite.billing.infrastructure.persistence.mapper

import com.petcaresuite.billing.domain.model.BillingDetail
import com.petcaresuite.billing.infrastructure.persistence.entity.BillingDetailEntity
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface BillingDetailEntityMapper {

    fun toEntity(billingDetail: BillingDetail): BillingDetailEntity

    fun toDomain(billingDetailEntity: BillingDetailEntity): BillingDetail

    fun toDomain(entities: List<BillingDetailEntity>): List<BillingDetail> {
        return entities.map { toDomain(it) }
    }
}