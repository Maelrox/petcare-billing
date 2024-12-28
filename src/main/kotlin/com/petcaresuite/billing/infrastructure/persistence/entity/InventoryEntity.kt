package com.petcaresuite.billing.infrastructure.persistence.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "inventory")
data class InventoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    val inventoryId: Long? = null,

    @Column(name = "name", nullable = false, length = 100)
    val name: String,

    @Column(name = "description", nullable = false)
    val description: String,

    @Column(name = "price")
    val price: BigDecimal?,

    @Column(name = "quantity")
    val quantity: Int?,

    @Column(name = "company_id", nullable = false)
    val companyId: Long,

)