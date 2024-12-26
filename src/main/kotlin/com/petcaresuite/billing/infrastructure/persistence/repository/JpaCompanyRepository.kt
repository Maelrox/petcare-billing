package com.petcaresuite.billing.infrastructure.persistence.repository

import com.petcaresuite.billing.infrastructure.persistence.entity.CompanyEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaCompanyRepository : JpaRepository<CompanyEntity, Long> {


}