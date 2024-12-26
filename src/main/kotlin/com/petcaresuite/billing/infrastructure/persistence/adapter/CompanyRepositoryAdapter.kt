package com.petcaresuite.billing.infrastructure.persistence.adapter

import com.petcaresuite.billing.application.port.output.CompanyPersistencePort
import com.petcaresuite.billing.domain.model.Company
import com.petcaresuite.billing.infrastructure.persistence.mapper.CompanyEntityMapper
import com.petcaresuite.billing.infrastructure.persistence.repository.JpaCompanyRepository
import org.springframework.stereotype.Component

@Component
class CompanyRepositoryAdapter(
    private val jpaCompanyRepository: JpaCompanyRepository,
    private val companyMapper: CompanyEntityMapper
) : CompanyPersistencePort {

    override fun findById(id: Long): Company? {
        val companyEntity = jpaCompanyRepository.findById(id)
        return companyEntity.map { companyMapper.toDomain(it) }.orElse(null)
    }

}