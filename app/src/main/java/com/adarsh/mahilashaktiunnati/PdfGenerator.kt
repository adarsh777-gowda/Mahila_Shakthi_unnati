package com.adarsh.mahilashaktiunnati

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.adarsh.mahilashaktiunnati.data.entities.Member
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.File
import java.io.FileOutputStream
import java.util.Date

private const val TAG = "PdfGenerator"

/**
 * Utility to open a PDF file using an external viewer.
 */
fun openPdf(context: Context, file: File) {
    if (!file.exists()) {
        Toast.makeText(context, "PDF file not found.", Toast.LENGTH_SHORT).show()
        return
    }
    
    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No app found to open PDF files.", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Log.e(TAG, "Error opening PDF", e)
        Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Utility to share a PDF file via the system sharing sheet.
 */
fun sharePdf(context: Context, file: File) {
    if (!file.exists()) {
        Toast.makeText(context, "PDF file not found.", Toast.LENGTH_SHORT).show()
        return
    }

    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(intent, "Share Report"))
    } catch (e: Exception) {
        Log.e(TAG, "Error sharing PDF", e)
        Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Generates a professionally formatted PDF report for the dashboard.
 */
fun generateDashboardPdfReport(
    context: Context,
    members: List<Member>,
    totalSavings: Int,
    totalLoan: Int
): File {
    val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
    if (!dir.exists()) dir.mkdirs()
    
    val file = File(dir, "Dashboard_Report_${System.currentTimeMillis()}.pdf")
    var document: Document? = null

    try {
        val writer = PdfWriter(FileOutputStream(file))
        val pdf = PdfDocument(writer)
        document = Document(pdf)

        document.add(Paragraph("Mahila Shakti Dashboard Report")
            .setTextAlignment(TextAlignment.CENTER)
            .setBold()
            .setFontSize(20f))

        document.add(Paragraph("Generated on: ${Date()}").setFontSize(10f))
        
        document.add(Paragraph("\nFinancial Summary").setBold().setFontSize(14f))
        val summaryTable = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f)))
        summaryTable.width = UnitValue.createPercentValue(100f)
        
        summaryTable.addCell(Cell().add(Paragraph("Total Members")))
        summaryTable.addCell(Cell().add(Paragraph(members.size.toString())))
        
        summaryTable.addCell(Cell().add(Paragraph("Total Savings")))
        summaryTable.addCell(Cell().add(Paragraph("₹$totalSavings")))
        
        summaryTable.addCell(Cell().add(Paragraph("Total Loan")))
        summaryTable.addCell(Cell().add(Paragraph("₹$totalLoan")))
        
        document.add(summaryTable)

        document.add(Paragraph("\nMember List").setBold().setFontSize(14f))
        
        if (members.isNotEmpty()) {
            val memberTable = Table(UnitValue.createPercentArray(floatArrayOf(10f, 55f, 35f)))
            memberTable.width = UnitValue.createPercentValue(100f)
            
            memberTable.addHeaderCell(Cell().add(Paragraph("S.No").setBold()))
            memberTable.addHeaderCell(Cell().add(Paragraph("Name").setBold()))
            memberTable.addHeaderCell(Cell().add(Paragraph("Phone").setBold()))
            
            members.forEachIndexed { index, member ->
                memberTable.addCell(Cell().add(Paragraph((index + 1).toString())))
                memberTable.addCell(Cell().add(Paragraph(member.name)))
                memberTable.addCell(Cell().add(Paragraph(member.phone)))
            }
            document.add(memberTable)
        } else {
            document.add(Paragraph("No members registered yet."))
        }
    } catch (e: Exception) {
        Log.e(TAG, "PDF generation failed", e)
    } finally {
        document?.close()
    }

    return file
}

/**
 * Generates a simple PDF report from a raw string content.
 */
fun generatePdfReport(context: Context, content: String): File {
    val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
    if (!dir.exists()) dir.mkdirs()
    
    val file = File(dir, "Report_${System.currentTimeMillis()}.pdf")
    var document: Document? = null

    try {
        val writer = PdfWriter(FileOutputStream(file))
        val pdf = PdfDocument(writer)
        document = Document(pdf)

        document.add(Paragraph("Mahila Shakti Report")
            .setBold()
            .setFontSize(16f)
            .setTextAlignment(TextAlignment.CENTER))
            
        document.add(Paragraph("\n$content"))
    } catch (e: Exception) {
        Log.e(TAG, "Simple PDF generation failed", e)
    } finally {
        document?.close()
    }

    return file
}
