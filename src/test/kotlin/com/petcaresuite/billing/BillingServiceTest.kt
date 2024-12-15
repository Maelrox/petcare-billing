package com.petcaresuite.billing

import com.petcaresuite.billing.application.dto.*
import com.petcaresuite.billing.application.mapper.BillingMapper
import com.petcaresuite.billing.application.port.output.BillingPersistencePort
import com.petcaresuite.billing.application.port.output.BillingMessageProducerPort
import com.petcaresuite.billing.application.service.BillingService
import com.petcaresuite.billing.application.service.messages.Responses
import com.petcaresuite.billing.domain.model.Billing
import com.petcaresuite.billing.domain.model.BillingDetail
import com.petcaresuite.billing.domain.service.BillingDomainService
import com.petcaresuite.billing.infrastructure.exception.InsufficientInventoryException
import com.petcaresuite.billing.infrastructure.rest.InventoryClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.math.BigDecimal
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class BillingServiceTest {

    @Mock
    private lateinit var billingPersistencePort: BillingPersistencePort

    @Mock
    private lateinit var billingMapper: BillingMapper

    @Mock
    private lateinit var billingMessageProducerPort: BillingMessageProducerPort

    @Mock
    private lateinit var inventoryClient: InventoryClient

    private lateinit var billingService: BillingService
    private lateinit var billingDomainService: BillingDomainService

    private lateinit var mockBilling: Billing
    private lateinit var mockBillingDTO: BillingDTO
    private lateinit var mockBillingDetailDTO: BillingDetailDTO
    private lateinit var mockBillingDetail: BillingDetail
    private lateinit var mockOwnerDTO: OwnerDTO

    @BeforeEach
    fun setUp() {
        mockOwnerDTO = OwnerDTO(
            ownerId = 1L,
        )

        mockBillingDetailDTO = BillingDetailDTO(
            billingDetailId = 1L,
            inventoryId = 1L,
            name = "Pet Food",
            description = "Premium Dog Food",
            quantity = 2,
            amount = BigDecimal("50.00"),
            consultationId = 1L,
            companyId = 1
        )

        mockBillingDTO = BillingDTO(
            billingId = 1L,
            totalAmount = BigDecimal("100.00"),
            paymentStatus = "PENDING",
            transactionType = "BILL",
            transactionDate = LocalDateTime.now().toString(),
            billingDetails = listOf(mockBillingDetailDTO),
            companyId = 1L,
            authorization = "auth-token",
            ownerId = 1L,
            owner = mockOwnerDTO,
            trxId = "trx-123",
            identification = "BILL-001"
        )

        mockBillingDetail = BillingDetail(
            billingDetailId = 1L,
            inventoryId = 1L,
            consultationId = null,
            quantity = 1,
            amount = BigDecimal(1000),
        )

        mockBilling = Billing(
            totalAmount = BigDecimal("100.00"),
            paymentStatus = "PENDING",
            transactionType = "BILL",
            transactionDate = LocalDateTime.now(),
            companyId = 1L,
            ownerId = 1L,
            identification = "BILL-001",
            billingId = 0L,
            initialDate = null,
            finalDate = null,
            billingDetails = listOf(mockBillingDetail)
        )

        billingService = BillingService(
            billingPersistencePort,
            billingMapper,
            billingDomainService ,
            billingMessageProducerPort,
            inventoryClient,
        )
    }

    @Test
    fun `save - successful billing creation`() {
        // Given
        Mockito.doNothing().`when`(billingMessageProducerPort).sendBillingMessage(any(BillingDTO::class.java))

        // When
        val result = billingService.save(mockBillingDTO)

        // Then
        Mockito.verify(billingMessageProducerPort).sendBillingMessage(any(BillingDTO::class.java))
        assert(result.success!!)
        assert(result.message == Responses.BILLING_CREATED)
        assert(result.trx != null)
    }

    @Test
    fun `update - successful billing update`() {
        // Given
        Mockito.`when`(billingPersistencePort.findById(mockBillingDTO.billingId!!)).thenReturn(mockBilling)
        Mockito.`when`(billingMapper.toDomain(mockBillingDTO)).thenReturn(mockBilling)
        Mockito.doNothing().`when`(billingMessageProducerPort).sendBillingMessage(any(BillingDTO::class.java))

        // When
        val result = billingService.update(mockBillingDTO)

        // Then
        Mockito.verify(billingPersistencePort).update(any(Billing::class.java))
        Mockito.verify(billingMessageProducerPort).sendBillingMessage(any(BillingDTO::class.java))
        assert(result?.success == true)
        assert(result?.message == Responses.BILLING_UPDATED)
    }

    @Test
    fun `getAllByFilterPaginated - returns paginated results`() {
        // Given
        val pageable = PageRequest.of(0, 10)
        val filterDTO = BillingFilterDTO(
            billingId = 1L,
            paymentStatus = null,
            transactionType = null,
            transactionDate = null,
            companyId = null,
            ownerId = null,
            identification = null,
            initialDate = null,
            finalDate = null
        )
        val mockPage = PageImpl(listOf(mockBilling))

        Mockito.`when`(billingMapper.toDomain(filterDTO)).thenReturn(mockBilling)
        Mockito.`when`(billingPersistencePort.findAllByFilterPaginated(mockBilling, pageable)).thenReturn(mockPage)
        Mockito.`when`(billingMapper.toDTO(mockBilling)).thenReturn(mockBillingDTO)

        // When
        val result = billingService.getAllByFilterPaginated(filterDTO, pageable)

        // Then
        assert(result.content.size == 1)
        assert(result.content[0] == mockBillingDTO)
    }

    @Test
    fun `processBilling - successful billing processing with inventory update`() {
        // Given
        val inventoryResponse = ResponseDTO(success = true, message = "Inventory updated", trx = "trx-123")
        Mockito.`when`(inventoryClient.update(any(), any())).thenReturn(inventoryResponse)
        Mockito.`when`(billingMapper.toDomain(mockBillingDTO)).thenReturn(mockBilling)

        // When
        val result = billingService.processBilling(mockBillingDTO)

        // Then
        Mockito.verify(inventoryClient).update(any(), any())
        Mockito.verify(billingPersistencePort).save(any(Billing::class.java))
        assert(result.success!!)
        assert(result.message == Responses.BILLING_CREATED)
    }

    @Test
    fun `processBilling - throws exception when inventory update fails`() {
        // Given
        val errorResponse = ResponseDTO(success = false, message = "Inventory update failed", trx = "trx-123")
        Mockito.`when`(inventoryClient.update(any(), any())).thenReturn(errorResponse)

        // When/Then
        assertThrows<RuntimeException> {
            billingService.processBilling(mockBillingDTO)
        }
    }

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
    private fun <T> any(): T = Mockito.any<T>()
}