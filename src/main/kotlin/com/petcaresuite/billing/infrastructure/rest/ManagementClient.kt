package com.petcaresuite.billing.infrastructure.rest

import com.petcaresuite.billing.application.dto.ResponseDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "MANAGEMENT")
interface ManagementClient {

    @GetMapping("/permission/hasPermission")
    fun hasPermission(
        @RequestHeader("Authorization") token: String,
        @RequestParam("module") module: String,
        @RequestParam("action") action: String
    ): ResponseDTO

}