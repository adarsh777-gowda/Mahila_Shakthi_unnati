package com.adarsh.mahilashaktiunnati.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import com.adarsh.mahilashaktiunnati.data.entities.Member
import com.adarsh.mahilashaktiunnati.data.entities.Savings
import com.adarsh.mahilashaktiunnati.data.entities.Loan

object DataExportImport {
    
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timestampFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    
    suspend fun exportMembersToCsv(context: Context, members: List<Member>): Result<Uri> {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = "members_export_${timestampFormat.format(Date())}.csv"
                val file = File(context.cacheDir, fileName)
                
                FileWriter(file).use { writer ->
                    // Write header
                    writer.appendLine("ID,Name,Phone,Photo URI,Created At,Updated At")
                    
                    // Write data
                    members.forEach { member ->
                        writer.appendLine("${member.id},${escapeCsv(member.name)},${escapeCsv(member.phone)},${escapeCsv(member.photoUri ?: "")},${dateFormat.format(Date(member.createdAt))},${dateFormat.format(Date(member.joinDate))}")
                    }
                }
                
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                Result.success(uri)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun exportSavingsToCsv(context: Context, savings: List<Savings>): Result<Uri> {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = "savings_export_${timestampFormat.format(Date())}.csv"
                val file = File(context.cacheDir, fileName)
                
                FileWriter(file).use { writer ->
                    // Write header
                    writer.appendLine("ID,Member ID,Amount,Week,Status,Created At,Updated At")
                    
                    // Write data
                    savings.forEach { saving ->
                        writer.appendLine("${saving.id},${saving.memberId},${saving.amount},${escapeCsv(saving.date.toString())},${escapeCsv(saving.status)},${dateFormat.format(Date(saving.date))},${dateFormat.format(Date(saving.date))}")
                    }
                }
                
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                Result.success(uri)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun exportLoansToCsv(context: Context, loans: List<Loan>): Result<Uri> {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = "loans_export_${timestampFormat.format(Date())}.csv"
                val file = File(context.cacheDir, fileName)
                
                FileWriter(file).use { writer ->
                    // Write header
                    writer.appendLine("ID,Member ID,Amount,Date,Status,Created At,Updated At")
                    
                    // Write data
                    loans.forEach { loan ->
                        writer.appendLine("${loan.id},${loan.memberId},${loan.principalAmount},${escapeCsv(loan.disbursementDate.toString())},${if (loan.isPaid) "Paid" else "Pending"},${dateFormat.format(Date(loan.disbursementDate))},${dateFormat.format(Date(loan.dueDate))}")
                    }
                }
                
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                Result.success(uri)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun importMembersFromCsv(context: Context, uri: Uri): Result<List<MemberImportData>> {
        return withContext(Dispatchers.IO) {
            try {
                val members = mutableListOf<MemberImportData>()
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        var line = reader.readLine()
                        if (line != null) {
                            // Skip header
                            line = reader.readLine()
                        }
                        
                        while (line != null) {
                            val parts = line.split(",").map { it.trim() }
                            if (parts.size >= 3) {
                                try {
                                    val member = MemberImportData(
                                        name = unescapeCsv(parts[1]),
                                        phone = unescapeCsv(parts[2]),
                                        photoUri = if (parts.size > 3) unescapeCsv(parts[3]) else null
                                    )
                                    members.add(member)
                                } catch (e: Exception) {
                                    // Skip invalid lines
                                }
                            }
                            line = reader.readLine()
                        }
                    }
                }
                Result.success(members)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun importSavingsFromCsv(context: Context, uri: Uri): Result<List<SavingsImportData>> {
        return withContext(Dispatchers.IO) {
            try {
                val savings = mutableListOf<SavingsImportData>()
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        var line = reader.readLine()
                        if (line != null) {
                            // Skip header
                            line = reader.readLine()
                        }
                        
                        while (line != null) {
                            val parts = line.split(",").map { it.trim() }
                            if (parts.size >= 4) {
                                try {
                                    val saving = SavingsImportData(
                                        memberId = parts[1].toInt(),
                                        amount = parts[2].toInt(),
                                        week = unescapeCsv(parts[3]),
                                        status = if (parts.size > 4) unescapeCsv(parts[4]) else "Pending"
                                    )
                                    savings.add(saving)
                                } catch (e: Exception) {
                                    // Skip invalid lines
                                }
                            }
                            line = reader.readLine()
                        }
                    }
                }
                Result.success(savings)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun importLoansFromCsv(context: Context, uri: Uri): Result<List<LoanImportData>> {
        return withContext(Dispatchers.IO) {
            try {
                val loans = mutableListOf<LoanImportData>()
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        var line = reader.readLine()
                        if (line != null) {
                            // Skip header
                            line = reader.readLine()
                        }
                        
                        while (line != null) {
                            val parts = line.split(",").map { it.trim() }
                            if (parts.size >= 4) {
                                try {
                                    val loan = LoanImportData(
                                        memberId = parts[1].toInt(),
                                        amount = parts[2].toInt(),
                                        date = unescapeCsv(parts[3]),
                                        status = if (parts.size > 4) unescapeCsv(parts[4]) else "Pending"
                                    )
                                    loans.add(loan)
                                } catch (e: Exception) {
                                    // Skip invalid lines
                                }
                            }
                            line = reader.readLine()
                        }
                    }
                }
                Result.success(loans)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    fun shareFile(context: Context, uri: Uri, title: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, "Exported data from Mahila Shakti Unnati app")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        val chooser = Intent.createChooser(intent, "Share exported data")
        context.startActivity(chooser)
    }
    
    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }
    
    private fun unescapeCsv(value: String): String {
        return if (value.startsWith("\"") && value.endsWith("\"")) {
            value.substring(1, value.length - 1).replace("\"\"", "\"")
        } else {
            value
        }
    }
}

data class MemberImportData(
    val name: String,
    val phone: String,
    val photoUri: String?
)

data class SavingsImportData(
    val memberId: Int,
    val amount: Int,
    val week: String,
    val status: String
)

data class LoanImportData(
    val memberId: Int,
    val amount: Int,
    val date: String,
    val status: String
)
