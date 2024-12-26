package com.petcaresuite.billing.application.port.input

import com.petcaresuite.billing.application.dto.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface BillingUseCase {
    
    fun save(billingDTO: BillingDTO): ResponseDTO

    fun update(billingDTO: BillingDTO): ResponseDTO?

    fun getAllByFilterPaginated(filterDTO: BillingFilterDTO, pageable: Pageable): Page<BillingDTO>

    fun processBilling(billingDTO: BillingDTO): ResponseDTO

    fun cancel(billingId: Long, companyId: Long, authorization: String): ResponseDTO

    fun processCancellation(billingDTO: BillingDTO): ResponseDTO

    fun generateInvoice(billingId: Long, companyId: Long): ByteArray

}