package com.petcaresuite.billing.infrastructure.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "IdentificationTypes")
data class IdentificationTypeEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    val id: Long? = null,

    @Column(name = "type_name", nullable = false, length = 100)
    val name: String
)