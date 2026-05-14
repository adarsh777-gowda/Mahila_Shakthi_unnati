package com.adarsh.mahilashaktiunnati.ui.screens

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adarsh.mahilashaktiunnati.R
import com.adarsh.mahilashaktiunnati.ui.components.LanguageSelector
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    context: android.content.Context,
    onBack: () -> Unit,
    onLanguageChanged: () -> Unit = {}
) {
    var selectedReportType by remember { mutableStateOf("members") }
    var isGeneratingReport by remember { mutableStateOf(false) }
    
    val accentColor = Color(0xFFE91E63)
    val successColor = Color(0xFF4CAF50)
    val infoColor = Color(0xFF2196F3)
    val cardBackground = Color(0xFFF8F9FA)
    
    val localContext = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFCE4EC), // Light Pink
                        Color(0xFFF3E5F5)  // Light Purple
                    )
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Language Selector
        LanguageSelector(
            context = context,
            onLanguageChanged = onLanguageChanged
        )

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.reports),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF880E4F)
            )
            
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color(0xFF880E4F)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Report Type Selection
        Text(
            text = "ವರದಿ ಆಯ್ಕೆಮಾಡಿ",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF424242)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Report Type Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val reportTypes = listOf(
                "members" to R.string.members,
                "savings" to R.string.savings,
                "loans" to R.string.loans,
                "meetings" to R.string.meetings_tab
            )
            
            reportTypes.forEach { (type, labelRes) ->
                FilterChip(
                    onClick = { selectedReportType = type },
                    label = { Text(stringResource(labelRes)) },
                    selected = selectedReportType == type,
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = accentColor,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Generate Report Button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = cardBackground
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when (selectedReportType) {
                        "members" -> stringResource(R.string.generate_members_report)
                        "savings" -> stringResource(R.string.generate_savings_report)
                        "loans" -> stringResource(R.string.generate_loans_report)
                        "meetings" -> stringResource(R.string.generate_meetings_report)
                        else -> stringResource(R.string.generate_general_report)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF424242),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Button(
                    onClick = { 
                        isGeneratingReport = true
                        // Simple simulation
                        isGeneratingReport = false
                    },
                    enabled = !isGeneratingReport,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFE0E0E0),
                        disabledContentColor = Color(0xFF9E9E9E)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isGeneratingReport) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            stringResource(R.string.generating_report),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Text(
                            "ವರದಿ ತಯಾರಿಸಿ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { 
                    shareReportViaWhatsApp(localContext, selectedReportType)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = successColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    stringResource(R.string.share_whatsapp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Button(
                onClick = { 
                    downloadReport(localContext, selectedReportType)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = infoColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    stringResource(R.string.download_report),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun shareReportViaWhatsApp(context: Context, reportType: String) {
    try {
        val reportData = generateMockReportData(reportType)
        val reportText = formatReportForWhatsApp(reportData, reportType)
        
        val whatsappIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, reportText)
            setPackage("com.whatsapp")
        }
        
        if (whatsappIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(whatsappIntent)
        } else {
            // Fallback to regular share if WhatsApp is not installed
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, reportText)
            }
            context.startActivity(Intent.createChooser(shareIntent, "ವರದಿ ಹಂಚಿಕೊಳ್ಳಿ"))
        }
    } catch (e: Exception) {
        // Handle error
    }
}

private fun downloadReport(context: Context, reportType: String) {
    try {
        val reportData = generateMockReportData(reportType)
        val fileName = "${reportType}_report_${SimpleDateFormat("dd_MM_yyyy_HH_mm", Locale.getDefault()).format(Date())}.txt"
        
        val downloadsDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MahilaShaktiReports")
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        
        val reportFile = File(downloadsDir, fileName)
        reportFile.writeText(formatReportForDownload(reportData, reportType))
    } catch (e: Exception) {
        // Handle error
    }
}

private fun generateMockReportData(reportType: String): Map<String, Any> {
    return when (reportType) {
        "members" -> mapOf(
            "totalMembers" to 45,
            "activeMembers" to 38,
            "newMembers" to 7,
            "generatedDate" to SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        )
        "savings" -> mapOf(
            "totalSavings" to 250000,
            "monthlySavings" to 15000,
            "growthRate" to 12.5,
            "generatedDate" to SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        )
        "loans" -> mapOf(
            "totalLoans" to 12,
            "activeLoans" to 8,
            "pendingLoans" to 4,
            "totalAmount" to 480000,
            "generatedDate" to SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        )
        "meetings" -> mapOf(
            "totalMeetings" to 24,
            "attendedMeetings" to 20,
            "upcomingMeetings" to 4,
            "generatedDate" to SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        )
        else -> mapOf(
            "message" to "Invalid report type",
            "generatedDate" to SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        )
    }
}

private fun formatReportForWhatsApp(reportData: Map<String, Any>, reportType: String): String {
    return when (reportType) {
        "members" -> """
            💪 ಮಹಿಳಾ ಶಕ್ತಿ ಸಂಘಟನಾ - ಸದಸ್ಯರ ವರದಿ
            
            📊 ಒಟ್ಟು ಸದಸ್ಯರು: ${reportData["totalMembers"]}
            ✅ ಸಕ್ರಿಯ ಸದಸ್ಯರು: ${reportData["activeMembers"]}
            🆕 ಹೊಸ ಸದಸ್ಯರು: ${reportData["newMembers"]}
            
            📅 ವರದಿ ದಿನಾಂಕ: ${reportData["generatedDate"]}
        """.trimIndent()
        
        "savings" -> """
            💪 ಮಹಿಳಾ ಶಕ್ತಿ ಸಂಘಟನಾ - ಉಳಿತಾಯ ವರದಿ
            
            💰 ಒಟ್ಟು ಉಳಿತಾಯ: ₹${reportData["totalSavings"]}
            📈 ಮಾಸಿಕ ಉಳಿತಾಯ: ₹${reportData["monthlySavings"]}
            📊 ಬೆಳವಣಿಗೆ ದರ: ${reportData["growthRate"]}%
            
            📅 ವರದಿ ದಿನಾಂಕ: ${reportData["generatedDate"]}
        """.trimIndent()
        
        "loans" -> """
            💪 ಮಹಿಳಾ ಶಕ್ತಿ ಸಂಘಟನಾ - ಸಾಲಗಳ ವರದಿ
            
            💳 ಒಟ್ಟು ಸಾಲಗಳು: ${reportData["totalLoans"]}
            ✅ ಸಕ್ರಿಯ ಸಾಲಗಳು: ${reportData["activeLoans"]}
            ⏳ ಬಾಕಿ ಸಾಲಗಳು: ${reportData["pendingLoans"]}
            💰 ಒಟ್ಟು ಮೊತ್ತ: ₹${reportData["totalAmount"]}
            
            📅 ವರದಿ ದಿನಾಂಕ: ${reportData["generatedDate"]}
        """.trimIndent()
        
        "meetings" -> """
            💪 ಮಹಿಳಾ ಶಕ್ತಿ ಸಂಘಟನಾ - ಸಭೆಗಳ ವರದಿ
            
            📊 ಒಟ್ಟು ಸಭೆಗಳು: ${reportData["totalMeetings"]}
            ✅ ನಡೆದ ಸಭೆಗಳು: ${reportData["attendedMeetings"]}
            📅 ಬಾಕಿ ಸಭೆಗಳು: ${reportData["upcomingMeetings"]}
            
            📅 ವರದಿ ದಿನಾಂಕ: ${reportData["generatedDate"]}
        """.trimIndent()
        
        else -> "ವರದಿ ಲಭ್ಯವಿದೆ. 💪 ಮಹಿಳಾ ಶಕ್ತಿ ಸಂಘಟನಾ."
    }
}

private fun formatReportForDownload(reportData: Map<String, Any>, reportType: String): String {
    return formatReportForWhatsApp(reportData, reportType)
}
