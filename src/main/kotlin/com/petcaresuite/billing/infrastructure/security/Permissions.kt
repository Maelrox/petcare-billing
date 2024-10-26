package com.petcaresuite.billing.infrastructure.security

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(
    AnnotationRetention.RUNTIME
)
annotation class Permissions(val module: String, val action: String)