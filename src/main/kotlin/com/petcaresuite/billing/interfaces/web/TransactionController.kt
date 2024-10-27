package com.petcaresuite.billing.interfaces.web

import com.petcaresuite.appointment.application.service.modules.ModuleActions
import com.petcaresuite.billing.application.service.modules.Modules
import com.petcaresuite.billing.infrastructure.kafka.TransactionService
import com.petcaresuite.billing.infrastructure.persistence.entity.BillingTransactionEntity
import com.petcaresuite.billing.infrastructure.security.Permissions
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController("/transaction")
class TransactionController(
    private val transactionService: TransactionService
) {

    @GetMapping("/{trxId}")
    @Permissions(Modules.BILLING, ModuleActions.VIEW)
    fun getAllBillingsByFilter(@PathVariable trxId: String, request: HttpServletRequest): ResponseEntity<BillingTransactionEntity> {
        val companyId  = request.getAttribute("companyId").toString().toLong()
        val transaction = transactionService.checkStatus(trxId, companyId)
        return ResponseEntity.ok(transaction)
    }

}