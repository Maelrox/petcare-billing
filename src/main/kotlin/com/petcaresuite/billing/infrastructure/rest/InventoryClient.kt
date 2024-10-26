package com.petcaresuite.billing.infrastructure.rest

import com.petcaresuite.billing.application.dto.InventoryDTO
import com.petcaresuite.billing.application.dto.ResponseDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(name = "INVENTORY")
interface InventoryClient {

    @PostMapping("/update")
    fun update(inventories: List<InventoryDTO>, @RequestHeader("Authorization") authorization: String): ResponseDTO

}