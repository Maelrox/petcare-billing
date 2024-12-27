package com.petcaresuite.billing.infrastructure.persistence.entity

import com.petcaresuite.billing.domain.model.Owner
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface OwnerEntityMapper {

    fun toEntity(owner: Owner): OwnerEntity

    fun toDomain(ownerEntity: OwnerEntity): Owner

    fun toDomain(owners: List<OwnerEntity>): List<Owner>

}