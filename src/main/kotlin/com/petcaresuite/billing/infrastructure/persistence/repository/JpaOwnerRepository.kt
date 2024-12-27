package com.petcaresuite.billing.infrastructure.persistence.repository

import com.petcaresuite.billing.infrastructure.persistence.entity.OwnerEntity
import org.springframework.data.jpa.repository.JpaRepository

interface JpaOwnerRepository : JpaRepository<OwnerEntity, Long> {

}
