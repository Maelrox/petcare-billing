package com.petcaresuite.billing.domain.model

import com.petcaresuite.inventory.domain.model.Service
import java.math.BigDecimal
import java.time.LocalDateTime

data class Consultation(
    var consultationId: Long? = null,
    val patientId: Long? = null,
    val vetId: Long? = null,
    var companyId: Long? = null,
    val appointmentId: Long? = null,
    val consultationDate: LocalDateTime?,
    val reason: String? = null,
    val diagnosis: String? = null,
    val treatment: String? = null,
    val notes: String? = null,
    val followUpDate: LocalDateTime? = null,
    var status: String? = null,
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = LocalDateTime.now(),
    var ownerId: Long?,
    val veterinaryName: String? = null,
    val ownerName: String? = null,
    val patientName: String? = null,
    val service: Service? = null,
    val serviceName: String? = null,
    val price: BigDecimal? = null,
)