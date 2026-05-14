package com.adarsh.mahilashaktiunnati.utils

import android.content.Context
import android.content.Intent
import com.adarsh.mahilashaktiunnati.data.entities.Member
import com.adarsh.mahilashaktiunnati.data.entities.Savings
import com.adarsh.mahilashaktiunnati.data.entities.Loan
import com.adarsh.mahilashaktiunnati.data.relations.MemberWithSavingsAndLoans
import java.text.SimpleDateFormat
import java.util.*

object ExportUtils {
    
    /**
     * Formats a member's financial summary into a clean text string for sharing
     */
    fun formatMemberSummary(
        member: Member,
        savings: List<Savings>,
        loans: List<Loan>
    ): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val currencyFormat = "₹%,d"
        
        val totalSavings = savings
            .filter { it.status == "PAID" }
            .sumOf { it.amount }
        
        val pendingSavings = savings
            .filter { it.status == "PENDING" }
            .sumOf { it.amount }
        
        val activeLoans = loans.filter { !it.isPaid }
        val totalLoanAmount = activeLoans.sumOf { it.principalAmount }
        
        val summary = buildString {
            // Header
            appendLine("💪 ಮಹಿಳ ಶಕ್ತಿ ಸಂಘಟನ - Member Summary")
            appendLine("=" .repeat(40))
            appendLine()
            
            // Member Information
            appendLine("👤 Member Details:")
            appendLine("Name: ${member.name}")
            appendLine("Phone: ${member.phone}")
            appendLine("Member ID: #${member.id}")
            appendLine("Join Date: ${dateFormat.format(Date(member.joinDate))}")
            appendLine("Status: ${if (member.isActive) "Active" else "Inactive"}")
            appendLine()
            
            // Financial Summary
            appendLine("💰 Financial Summary:")
            appendLine("Total Paid Savings: ${currencyFormat.format(totalSavings)}")
            appendLine("Pending Savings: ${currencyFormat.format(pendingSavings)}")
            appendLine("Total Savings Entries: ${savings.size}")
            appendLine("Paid Entries: ${savings.count { it.status == "PAID" }}")
            appendLine()
            
            // Loan Information
            if (activeLoans.isNotEmpty()) {
                appendLine("🏦 Active Loans:")
                appendLine("Number of Active Loans: ${activeLoans.size}")
                appendLine("Total Loan Amount: ${currencyFormat.format(totalLoanAmount)}")
                activeLoans.forEachIndexed { index, loan ->
                    appendLine()
                    appendLine("Loan ${index + 1}:")
                    appendLine("  Amount: ${currencyFormat.format(loan.principalAmount)}")
                    appendLine("  Interest Rate: ${loan.interestRate}%")
                    appendLine("  Disbursement Date: ${dateFormat.format(Date(loan.disbursementDate))}")
                    appendLine("  Due Date: ${dateFormat.format(Date(loan.dueDate))}")
                    appendLine("  Paid Amount: ${currencyFormat.format(loan.paidAmount)}")
                    loan.purpose?.let { appendLine("  Purpose: $it") }
                }
            } else {
                appendLine("✅ Loan Status: No active loans")
            }
            appendLine()
            
            // Recent Activity
            if (savings.isNotEmpty()) {
                appendLine("📊 Recent Savings Activity:")
                val recentEntries = savings.takeLast(5).reversed()
                recentEntries.forEach { entry ->
                    val status = if (entry.status == "PAID") "✅" else "⏳"
                    appendLine("$status ${dateFormat.format(Date(entry.date))}: ${currencyFormat.format(entry.amount)}")
                }
            }
            appendLine()
            
            // Footer
            appendLine("📅 Generated on: ${SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())}")
            appendLine("📱 Mahila Shakti Unnati App")
        }
        
        return summary.toString()
    }
    
    /**
     * Formats group summary for all members
     */
    fun formatGroupSummary(
        membersWithSavingsAndLoans: List<MemberWithSavingsAndLoans>
    ): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val currencyFormat = "₹%,d"
        
        val totalMembers = membersWithSavingsAndLoans.size
        val activeMembers = membersWithSavingsAndLoans.count { it.member.isActive }
        val totalSavings = membersWithSavingsAndLoans.sumOf { member ->
            member.savings.filter { it.status == "PAID" }.sumOf { it.amount }
        }
        val totalActiveLoans = membersWithSavingsAndLoans.sumOf { member ->
            member.loans.filter { !it.isPaid }.sumOf { it.principalAmount }
        }
        
        return buildString {
            // Header
            appendLine("💪 ಮಹಿಳ ಶಕ್ತಿ ಸಂಘಟನ - Group Summary")
            appendLine("=" .repeat(40))
            appendLine()
            
            // Group Statistics
            appendLine("📊 Group Statistics:")
            appendLine("Total Members: $totalMembers")
            appendLine("Active Members: $activeMembers")
            appendLine("Inactive Members: ${totalMembers - activeMembers}")
            appendLine("Total Group Savings: ${currencyFormat.format(totalSavings)}")
            appendLine("Total Active Loans: ${currencyFormat.format(totalActiveLoans)}")
            appendLine()
            
            // Member-wise Summary
            appendLine("👥 Member-wise Summary:")
            appendLine("-".repeat(40))
            
            membersWithSavingsAndLoans.sortedBy { it.member.name }.forEach { memberData ->
                val member = memberData.member
                val memberSavings = memberData.savings
                    .filter { it.status == "PAID" }
                    .sumOf { it.amount }
                val memberLoans = memberData.loans.filter { !it.isPaid }.sumOf { it.principalAmount }
                val hasActiveLoan = memberData.loans.any { !it.isPaid }
                
                appendLine()
                appendLine("${member.name} (${member.phone}):")
                appendLine("  Savings: ${currencyFormat.format(memberSavings)}")
                appendLine("  Active Loans: ${if (hasActiveLoan) currencyFormat.format(memberLoans) else "None"}")
                appendLine("  Status: ${if (member.isActive) "Active" else "Inactive"}")
            }
            
            appendLine()
            appendLine("📅 Generated on: ${SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())}")
            appendLine("📱 Mahila Shakti Unnati App")
        }
    }
    
    /**
     * Creates an Intent to share text to WhatsApp specifically
     */
    fun createWhatsAppShareIntent(context: Context, text: String): Intent {
        return try {
            // Try to open WhatsApp directly
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
                setPackage("com.whatsapp")
            }
            
            // Check if WhatsApp is installed
            if (intent.resolveActivity(context.packageManager) != null) {
                intent
            } else {
                // Fallback to general share sheet if WhatsApp is not installed
                createGeneralShareIntent(text)
            }
        } catch (e: Exception) {
            // Fallback to general share sheet
            createGeneralShareIntent(text)
        }
    }
    
    /**
     * Creates a general share intent for all apps
     */
    fun createGeneralShareIntent(text: String): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, "Mahila Shakti Unnati - Financial Summary")
            addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
        }
    }
    
    /**
     * Formats weekly report for sharing
     */
    fun formatWeeklyReport(
        member: Member,
        weekSavings: List<Savings>,
        weekStartDate: Date,
        weekEndDate: Date
    ): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val currencyFormat = "₹%,d"
        
        val weekTotal = weekSavings.sumOf { it.amount }
        val paidAmount = weekSavings.filter { it.status == "PAID" }.sumOf { it.amount }
        val pendingAmount = weekSavings.filter { it.status == "PENDING" }.sumOf { it.amount }
        
        return buildString {
            appendLine("💪 ಮಹಿಳ ಶಕ್ತಿ ಸಂಘಟನ - Weekly Report")
            appendLine("=" .repeat(40))
            appendLine()
            
            appendLine("📅 Week: ${dateFormat.format(weekStartDate)} - ${dateFormat.format(weekEndDate)}")
            appendLine("👤 Member: ${member.name}")
            appendLine("📱 Phone: ${member.phone}")
            appendLine()
            
            appendLine("💰 Weekly Summary:")
            appendLine("Total Savings: ${currencyFormat.format(weekTotal)}")
            appendLine("Paid Amount: ${currencyFormat.format(paidAmount)}")
            appendLine("Pending Amount: ${currencyFormat.format(pendingAmount)}")
            appendLine("Status: ${if (pendingAmount == 0.0) "✅ Fully Paid" else "⏳ Pending Payment"}")
            appendLine()
            
            if (weekSavings.isNotEmpty()) {
                appendLine("📊 Daily Breakdown:")
                weekSavings.forEach { entry ->
                    val status = if (entry.status == "PAID") "✅" else "⏳"
                    appendLine("$status ${dateFormat.format(Date(entry.date))}: ${currencyFormat.format(entry.amount)}")
                }
            }
            
            appendLine()
            appendLine("📅 Generated on: ${SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())}")
            appendLine("📱 Mahila Shakti Unnati App")
        }
    }
}
