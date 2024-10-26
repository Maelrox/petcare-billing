package com.petcaresuite.billing

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
class AppointmentApplication

fun main(args: Array<String>) {
	runApplication<AppointmentApplication>(*args)
}
