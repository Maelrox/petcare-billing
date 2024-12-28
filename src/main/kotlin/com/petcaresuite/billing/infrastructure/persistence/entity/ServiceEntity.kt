package com.petcaresuite.billing.infrastructure.persistence.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "services", schema = "public")
data class ServiceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    val id: Long = 0,

    @Column(name = "name", length = 100, nullable = false)
    val name: String,

    @Column(name = "description", nullable = true)
    val description: String? = null,

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    val price: BigDecimal,

    @Column(name = "company_id", nullable = false)
    val companyId: Long


)