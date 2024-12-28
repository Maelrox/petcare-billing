package com.petcaresuite.billing.infrastructure.persistence.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "billing_details")
data class BillingDetailEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "billing_detail_id")
    val billingDetailId: Long?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_id", referencedColumnName = "billing_id")
    var billing: BillingEntity?,

    @Column(name = "quantity")
    val quantity: Int?,

    @Column(name = "amount")
    val amount: BigDecimal?,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    val inventory: InventoryEntity?,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id")
    val consultation: ConsultationEntity?

)
