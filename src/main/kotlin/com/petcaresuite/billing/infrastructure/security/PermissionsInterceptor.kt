package com.petcaresuite.billing.infrastructure.security

import com.petcaresuite.billing.infrastructure.rest.ManagementClient
import feign.FeignException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class PermissionsInterceptor(private val managementClientProvider: ObjectProvider<ManagementClient>) :
    HandlerInterceptor {

    @Throws(Exception::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod) {
            val method = handler.method

            val permissionRequired = method.getAnnotation(Permissions::class.java)
            if (permissionRequired != null) {
                val module = permissionRequired.module
                val action = permissionRequired.action
                val managementClient = managementClientProvider.getIfAvailable()
                val authHeader = request.getHeader("Authorization")
                try {
                    val permissionResponse = managementClient!!.hasPermission(authHeader, module, action)
                    if (!permissionResponse.success!!) {
                        throw IllegalAccessException("You don't have permission for the operation")
                    } else {
                        request.setAttribute("companyId", permissionResponse.message)
                    }
                } catch (e: FeignException) {
                    val feignResponse = feign.Response.builder()
                        .status(e.status())
                        .reason(e.message)
                        .request(e.request())
                        .build()
                    throw FeignException.errorStatus("FeignClientException", feignResponse)
                } catch (e: Exception) {
                    throw IllegalAccessException(e.message)
                }
            }
        }
        return true
    }
}