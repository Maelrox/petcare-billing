package com.petcaresuite.billing.infrastructure.persistence.mapper

import com.petcaresuite.billing.domain.model.Company
import com.petcaresuite.billing.infrastructure.persistence.entity.CompanyEntity
import org.mapstruct.Mapper
import java.util.*

@Mapper(componentModel = "spring")
interface CompanyEntityMapper {

    fun toEntity(company: Company): CompanyEntity

    fun toDomain(companyEntity: CompanyEntity): Company
}