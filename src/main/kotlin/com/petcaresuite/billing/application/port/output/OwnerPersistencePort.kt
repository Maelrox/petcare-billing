package com.petcaresuite.billing.application.port.output

import com.petcaresuite.billing.domain.model.Owner


interface OwnerPersistencePort {

     fun findById(ownerId: Long): Owner

}