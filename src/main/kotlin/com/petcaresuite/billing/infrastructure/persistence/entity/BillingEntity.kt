package com.petcaresuite.billing.infrastructure.persistence.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "billing")
data class BillingEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "billing_id")
    val billingId: Long?,

    @Column(name = "total_amount", precision = 16, scale = 2)
    val totalAmount: BigDecimal? = null,

    @Column(name = "payment_status", nullable = false, length = 50)
    val paymentStatus: String,

    @Column(name = "transaction_type", nullable = false, length = 50)
    val transactionType: String,

    @Column(name = "transaction_date")
    val transactionDate: LocalDateTime? = LocalDateTime.now(),

    @OneToMany(mappedBy = "billing", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val billingDetails: MutableList<BillingDetailEntity> = mutableListOf(),

    @Column(name = "company_id")
    val companyId: Long,

    @Column(name = "owner_id")
    val ownerId: Long,
)
