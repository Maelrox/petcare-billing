package com.petcaresuite.billing.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "billing_transactions")
data class BillingTransactionEntity(
    @Id
    @Column(name = "trx_id", length = 256, nullable = false)
    val trxId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_id", referencedColumnName = "billing_id")
    val billing: BillingEntity? = null,

    @Column(name = "date", nullable = false)
    val date: LocalDateTime = LocalDateTime.now(),

    @Column(name = "status", length = 32)
    var status: String? = null,

    @Column(name = "response")
    var response: String? = null,

    @Column(name = "company_id")
    var companyId: Long? = null
)
