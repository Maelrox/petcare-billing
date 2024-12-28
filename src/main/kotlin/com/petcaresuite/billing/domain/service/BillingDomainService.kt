package com.petcaresuite.billing.domain.service

import com.petcaresuite.billing.application.dto.BillingDTO
import com.petcaresuite.billing.application.dto.InvoiceRowDTO
import com.petcaresuite.billing.application.port.output.BillingPersistencePort
import com.petcaresuite.billing.application.port.output.CompanyPersistencePort
import com.petcaresuite.billing.application.port.output.OwnerPersistencePort
import com.petcaresuite.billing.application.service.messages.Responses
import com.petcaresuite.billing.domain.model.Billing
import com.petcaresuite.billing.domain.model.Consultation
import com.petcaresuite.inventory.domain.model.Inventory
import net.sf.jasperreports.engine.JREmptyDataSource
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperExportManager
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service

@Service
class BillingDomainService(
    private val billingPersistencePort: BillingPersistencePort,
    private val companyPersistencePort: CompanyPersistencePort,
    private val ownerPersistencePort: OwnerPersistencePort
) {

    data class ItemRow(val fieldName1: String, val fieldName2: Int)

    fun rollBackInventory(name: String, id: Long) {

    }

    fun validateCancellation(billing: Billing) {
        if (billing.paymentStatus.equals("REVERTED")) {
            throw IllegalArgumentException(Responses.BILLING_ALREADY_CANCELLED);
        }
    }

    fun payConsultations(billingDTO: BillingDTO) {
        val appointmentsToPay = billingDTO.billingDetails
            ?.filter { it.consultation != null }
            ?.map { it.consultation!!.consultationId }

        appointmentsToPay?.forEach { consultationId ->
            billingPersistencePort.updateConsultation(consultationId!!, "PAID")
            val appointmentIds = billingPersistencePort.getAppointments(consultationId)
            appointmentIds.forEach { appointmentId ->
                billingPersistencePort.updateAppointment(appointmentId, "PAID")
            }
        }
    }

    fun generateInvoice(billingId: Long, companyId: Long): ByteArray {
        val invoice = billingPersistencePort.findByIdAndCompanyId(billingId, companyId)
        val owner = ownerPersistencePort.findById(invoice.ownerId!!)
        val company = companyPersistencePort.findById(companyId)

        val parameters = mutableMapOf<String, Any>()

        // Header parameters
        val invoiceDate = invoice.transactionDate.toString()
        parameters["invoiceDate"] = invoiceDate
        parameters["invoiceNum"] = invoice.billingId.toString()
        //parameters["invoiceTotal"] = invoice.totalAmount ?: BigDecimal.ZERO
        parameters["companyName"] = company!!.name
        parameters["companyAddress"] = company.address ?: ""
        parameters["companyPhone"] = company.phone ?: ""
        parameters["companyEmail"] = company.email ?: ""
        parameters["ownerName"] = owner.name ?: ""
        parameters["ownerIdentification"] = owner.identification ?: ""
        parameters["ownerIdentificationType"] = owner.identificationType?.name ?: ""
        parameters["ownerPhone"] = owner.phone ?: ""

        // Details table
        val invoiceDetails = mutableListOf<InvoiceRowDTO>()
        invoice.billingDetails!!.forEach { billingDetail ->
            val detail = billingDetail.consultation ?: billingDetail.inventory
            val detailName = when (detail) {
                is Consultation -> detail.service!!.name
                is Inventory -> detail.name
                else -> "Unknown Detail"
            }
            invoiceDetails.add(InvoiceRowDTO(
                detailName ?: "Unknown detail",
                billingDetail.quantity.toString(),
                billingDetail.amount!!,
                billingDetail.amount.multiply(billingDetail.quantity!!.toBigDecimal())
                )
            )
        }
        val invoiceDetailsDataSource = JRBeanCollectionDataSource(invoiceDetails)

        // Add the datasource as a parameter
        parameters["tableDetailSet"] = invoiceDetailsDataSource

        val reportResource = ClassPathResource("invoice.jrxml")
        val jasperReport = JasperCompileManager.compileReport(reportResource.inputStream)
        val jasperPrint = try {
            // Pass empty datasource as main datasource since we're using parameter datasource
            JasperFillManager.fillReport(jasperReport, parameters, JREmptyDataSource())
        } catch (e: Exception) {
            throw RuntimeException("Error filling the report", e)
        }

        return JasperExportManager.exportReportToPdf(jasperPrint)
    }

}