package com.petcaresuite.billing.application.dto

data class PageDTO(
    val page: Int = 0,
    val size: Int = 10,
    val totalElements: Long = 0,
    val totalPages: Int = 0
)

data class PaginatedResponseDTO<T>(
    val data: List<T>,
    val pagination: PageDTO
)