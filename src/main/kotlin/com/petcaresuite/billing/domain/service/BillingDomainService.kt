package com.petcaresuite.billing.domain.service

import com.petcaresuite.billing.application.dto.BillingDTO
import com.petcaresuite.billing.application.dto.InventoryDTO
import com.petcaresuite.billing.application.dto.ResponseDTO
import com.petcaresuite.billing.application.port.output.BillingPersistencePort
import com.petcaresuite.billing.application.port.output.CompanyPersistencePort
import com.petcaresuite.billing.application.service.messages.Responses
import com.petcaresuite.billing.domain.model.Billing
import com.petcaresuite.billing.infrastructure.exception.InsufficientInventoryException
import com.petcaresuite.billing.infrastructure.exception.LockAcquisitionException
import feign.FeignException
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperExportManager
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.springframework.core.io.ClassPathResource
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Service

@Service
class BillingDomainService(
    private val billingPersistencePort: BillingPersistencePort,
    private val companyPersistencePort: CompanyPersistencePort
) {

    data class JasperData(val field1: String, val field2: Int)

    fun rollBackInventory(name: String, id: Long) {

    }

    fun validateCancellation(billing: Billing) {
        if (billing.paymentStatus.equals("REVERTED")) {
            throw IllegalArgumentException(Responses.BILLING_ALREADY_CANCELLED);
        }
    }

    fun payConsultations(billingDTO: BillingDTO) {
        val appointmentsToPay = billingDTO.billingDetails
            ?.filter { it.consultationId != null }
            ?.map { it.consultationId }

        appointmentsToPay?.forEach { consultationId ->
            billingPersistencePort.updateConsultation(consultationId!!, "PAID")
            val appointmentIds = billingPersistencePort.getAppointments(consultationId)
            appointmentIds.forEach { appointmentId ->
                billingPersistencePort.updateAppointment(appointmentId, "PAID")
            }
        }
    }

    fun generateInvoice(appointmentId: Long, companyId: Long): ByteArray {
        val appointment = billingPersistencePort.findByIdAndCompanyId(appointmentId, companyId)
        val company = companyPersistencePort.findById(companyId)
        val dataList = listOf(
            JasperData("Item 1", 10),
            JasperData("Item 2", 20),
            JasperData("Item 3", 30)
        )
        val parameters = mapOf("title" to "Sample Report")
        val jasperDataSource = JRBeanCollectionDataSource(dataList)
        val reportResource = ClassPathResource("invoice.jrxml")
        val jasperReport = JasperCompileManager.compileReport(reportResource.inputStream)
        val jasperPrint = try {
            JasperFillManager.fillReport(jasperReport, null, jasperDataSource)
        } catch (e: Exception) {
            throw RuntimeException("Error filling the report", e)
        }

        // Export the report to PDF
        return JasperExportManager.exportReportToPdf(jasperPrint)
    }

}