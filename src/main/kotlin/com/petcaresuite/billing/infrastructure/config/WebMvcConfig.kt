package com.petcaresuite.billing.infrastructure.config

import com.petcaresuite.billing.infrastructure.security.PermissionsInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(
    private val permissionsInterceptor: PermissionsInterceptor
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(permissionsInterceptor)
    }
}
