package com.petcaresuite.billing.infrastructure.persistence.adapter

import com.petcaresuite.billing.application.port.output.OwnerPersistencePort
import com.petcaresuite.billing.domain.model.Owner
import com.petcaresuite.billing.infrastructure.persistence.entity.OwnerEntityMapper
import com.petcaresuite.billing.infrastructure.persistence.repository.JpaOwnerRepository

import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Component

@Component
class OwnerRepositoryAdapter(
    private val jpaOwnerRepository: JpaOwnerRepository,
    private val ownerMapper: OwnerEntityMapper
) : OwnerPersistencePort {

    override fun findById(ownerId: Long): Owner {
        val ownerEntity = jpaOwnerRepository.findById(ownerId)
            .orElseThrow { EntityNotFoundException("Owner with id $ownerId not found") }
        return ownerMapper.toDomain(ownerEntity)
    }

}