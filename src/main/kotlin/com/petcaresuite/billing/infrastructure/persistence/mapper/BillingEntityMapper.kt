package com.petcaresuite.billing.infrastructure.persistence.mapper

import com.petcaresuite.billing.domain.model.Billing
import com.petcaresuite.billing.infrastructure.persistence.entity.BillingEntity
import com.petcaresuite.billing.infrastructure.persistence.entity.BillingProjection
import org.mapstruct.*

@Mapper(componentModel = "spring", uses = [BillingDetailEntityMapper::class])
interface BillingEntityMapper {

    fun toEntity(billing: Billing): BillingEntity

    @Mapping(target = "billingDetails", source = "billingEntity.billingDetails")
    fun toDomain(billingEntity: BillingEntity): Billing

    fun toDomain(entities: List<BillingEntity>): List<Billing>

    /*@Named("mapBillingDetails")
    fun mapBillingDetails(billingDetailEntities: List<BillingDetailEntity>): List<BillingDetail> {
        return billingDetailEntities.map { detailEntity ->
            BillingDetail(
                billingDetailId = detailEntity.billingDetailId,
                inventory = detailEntity.inventory,
                quantity = detailEntity.quantity,
                amount = detailEntity.amount,
                consultation = detailEntity.consultation,
            )
        }
    }*/

    fun projectionToDomain(billing: BillingProjection?): Billing?
}
