package com.petcaresuite.billing.interfaces.web

import com.petcaresuite.appointment.application.service.modules.ModuleActions
import com.petcaresuite.billing.application.dto.*
import com.petcaresuite.billing.application.port.input.BillingUseCase
import com.petcaresuite.billing.application.service.modules.Modules
import com.petcaresuite.billing.infrastructure.security.Permissions
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class BillingController(private val billingUseCase: BillingUseCase) {

    @PostMapping()
    @Permissions(Modules.INVENTORY, ModuleActions.CREATE)
    fun saveBilling(@RequestBody billingDTO: BillingDTO, request: HttpServletRequest): ResponseEntity<ResponseDTO> {
        val companyId  = request.getAttribute("companyId").toString().toLong()
        billingDTO.authorization = request.getHeader("Authorization")
        billingDTO.companyId = companyId
        return ResponseEntity.ok(billingUseCase.save(billingDTO))
    }

    @PutMapping()
    @Permissions(Modules.BILLING, ModuleActions.UPDATE)
    fun updateBilling(@RequestBody billingDTO: BillingDTO, request: HttpServletRequest): ResponseEntity<ResponseDTO> {
        val companyId  = request.getAttribute("companyId").toString().toLong()
        billingDTO.companyId = companyId
        return ResponseEntity.ok(billingUseCase.update(billingDTO))
    }

    @GetMapping("/search")
    @Permissions(Modules.BILLING, ModuleActions.VIEW)
    fun getAllBillingsByFilter(@ModelAttribute filterDTO: BillingFilterDTO, @RequestParam(defaultValue = "0") page: Int, @RequestParam(defaultValue = "30") size: Int, request: HttpServletRequest): ResponseEntity<PaginatedResponseDTO<BillingDTO>> {
        val pageable = PageRequest.of(page, size)
        val companyId  = request.getAttribute("companyId").toString().toLong()
        filterDTO.companyId = companyId
        val result = billingUseCase.getAllByFilterPaginated(filterDTO, pageable)

        val pageDTO = PageDTO(
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages
        )

        val paginatedResponse = PaginatedResponseDTO(
            data = result.content,
            pagination = pageDTO
        )

        return ResponseEntity.ok(paginatedResponse)
    }

}