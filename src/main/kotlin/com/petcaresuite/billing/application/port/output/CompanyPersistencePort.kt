package com.petcaresuite.billing.application.port.output

import com.petcaresuite.billing.domain.model.Company

interface CompanyPersistencePort {

    fun findById(id: Long): Company?

}