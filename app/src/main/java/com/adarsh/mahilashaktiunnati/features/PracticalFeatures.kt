package com.adarsh.mahilashaktiunnati.features

import java.util.*
import com.adarsh.mahilashaktiunnati.data.entities.Member
import com.adarsh.mahilashaktiunnati.data.entities.Savings
import com.adarsh.mahilashaktiunnati.data.entities.Loan

/**
 * Practical features specifically designed for women's self-help groups
 * in rural/semi-urban Indian contexts
 */
object PracticalFeatures {
    
    // ===========================================
    // CORE FINANCIAL MANAGEMENT FEATURES
    // ===========================================
    
    /**
     * Calculate group meeting requirements for regulatory compliance
     * Most states require minimum meetings and member participation
     */
    fun calculateGroupCompliance(
        totalMembers: Int,
        meetingsHeld: Int,
        averageAttendance: Double
    ): ComplianceReport {
        val attendanceRate = if (totalMembers > 0) averageAttendance / totalMembers else 0.0
        val meetingsRequired = maxOf(12, totalMembers) // Minimum 12 meetings/year
        
        return ComplianceReport(
            isCompliant = meetingsHeld >= meetingsRequired && attendanceRate >= 0.75,
            attendanceRate = attendanceRate,
            meetingsRequired = meetingsRequired,
            meetingsHeld = meetingsHeld,
            totalMembers = totalMembers,
            recommendation = when {
                meetingsHeld < meetingsRequired -> "Increase meeting frequency to meet regulatory requirements"
                attendanceRate < 0.75 -> "Improve member engagement through regular follow-ups"
                else -> "Good compliance with group standards"
            }
        )
    }
    
    /**
     * Calculate loan eligibility based on group savings patterns
     * Traditional self-help groups have specific lending criteria
     */
    fun calculateLoanEligibility(
        member: Member,
        memberSavings: List<Savings>,
        groupAverageSavings: Double
    ): LoanEligibilityResult {
        val totalMemberSavings = memberSavings.sumOf { it.amount }
        val monthsInGroup = calculateMonthsInGroup(member.createdAt)
        val averageMonthlySavings = if (monthsInGroup > 0) totalMemberSavings.toDouble() / monthsInGroup else 0.0
        
        // Traditional criteria: 3x average monthly savings or minimum 6 months tenure
        val maxLoanAmount = (averageMonthlySavings * 3).toInt()
        val isEligible = monthsInGroup >= 6 && totalMemberSavings >= 1000
        
        return LoanEligibilityResult(
            isEligible = isEligible,
            maxLoanAmount = maxLoanAmount,
            totalSavings = totalMemberSavings,
            averageMonthlySavings = averageMonthlySavings,
            monthsInGroup = monthsInGroup,
            groupAverageSavings = groupAverageSavings,
            recommendation = when {
                !isEligible && monthsInGroup < 6 -> "Continue regular savings for 6 more months to become eligible"
                !isEligible && totalMemberSavings < 1000 -> "Increase savings amount to meet minimum eligibility criteria"
                else -> "Eligible for loan up to ₹$maxLoanAmount"
            }
        )
    }
    
    /**
     * Generate meeting reminders with local notification support
     * Many women have limited smartphone access for reminders
     */
    fun generateMeetingSchedule(
        meetingType: String,
        frequency: MeetingFrequency
    ): List<MeetingReminder> {
        val calendar = Calendar.getInstance()
        val reminders = mutableListOf<MeetingReminder>()
        
        when (frequency) {
            MeetingFrequency.WEEKLY -> {
                for (i in 0..3) {
                    calendar.add(Calendar.DAY_OF_MONTH, 7)
                    reminders.add(
                        MeetingReminder(
                            title = "$meetingType - Week ${i + 1}",
                            date = calendar.time,
                            type = MeetingType.REGULAR,
                            importance = if (i == 0) MeetingImportance.HIGH else MeetingImportance.MEDIUM
                        )
                    )
                }
            }
            MeetingFrequency.MONTHLY -> {
                calendar.add(Calendar.MONTH, 1)
                reminders.add(
                    MeetingReminder(
                        title = "$meetingType - Monthly",
                        date = calendar.time,
                        type = MeetingType.REGULAR,
                        importance = MeetingImportance.HIGH
                    )
                )
            }
            MeetingFrequency.QUARTERLY -> {
                for (i in 0..3) {
                    calendar.add(Calendar.MONTH, 3)
                    reminders.add(
                        MeetingReminder(
                            title = "$meetingType - Quarter ${i + 1}",
                            date = calendar.time,
                            type = MeetingType.REGULAR,
                            importance = MeetingImportance.MEDIUM
                        )
                    )
                }
            }
        }
        
        return reminders
    }
    
    /**
     * Generate SMS reminders for members with basic phones
     * Many members prefer to SMS over app notifications
     */
    fun generateSMSReminder(
        memberName: String,
        message: String,
        phoneNumber: String
    ): SMSReminder {
        return SMSReminder(
            recipientName = memberName,
            phoneNumber = phoneNumber,
            message = message,
            estimatedCost = 1.5, // ₹1.5 per SMS
            deliveryMethod = if (phoneNumber.startsWith("+91")) "Standard SMS" else "International SMS",
            suggestedAlternative = if (phoneNumber.startsWith("+91")) {
                "Consider WhatsApp for free delivery"
            } else {
                "Use app notification for international members"
            }
        )
    }
    
    /**
     * Create emergency contact list for critical situations
     * Important for women's safety in rural areas
     */
    fun createEmergencyContacts(
        members: List<Member>
    ): EmergencyContactList {
        val emergencyContacts = members.map { member ->
            EmergencyContact(
                memberName = member.name,
                phoneNumber = member.phone,
                relationship = "Primary", // Primary member
                priority = if (member.phone.startsWith("+91")) {
                    EmergencyPriority.HIGH
                } else {
                    EmergencyPriority.MEDIUM
                }
            )
        }
        
        return EmergencyContactList(
            contacts = emergencyContacts,
            totalContacts = members.size,
            localContacts = members.count { it.phone.startsWith("+91") },
            internationalContacts = members.count { !it.phone.startsWith("+91") },
            recommendation = "Share this list with all members and local authorities"
        )
    }
    
    /**
     * Calculate and display financial literacy metrics
     * Help members understand their financial progress
     */
    fun calculateFinancialLiteracyMetrics(
        members: List<Member>,
        savings: List<Savings>,
        loans: List<Loan>
    ): FinancialLiteracyReport {
        val totalSavings = savings.sumOf { it.amount }
        val totalLoans = loans.filter { !it.isPaid }.sumOf { it.principalAmount }
        val savingMembers = members.count { member ->
            savings.any { it.memberId == member.id }
        }
        val borrowingMembers = members.count { member ->
            loans.any { it.memberId == member.id }
        }
        
        val savingsRate = if (members.isNotEmpty()) {
            (savingMembers.toDouble() / members.size) * 100
        } else 0.0
        
        val savingsToLoanRatio = if (totalLoans > 0) (totalSavings.toDouble() / totalLoans) else Double.POSITIVE_INFINITY

        return FinancialLiteracyReport(
            totalMembers = members.size,
            savingMembers = savingMembers,
            borrowingMembers = borrowingMembers,
            savingsRate = savingsRate,
            totalSavings = totalSavings,
            totalLoans = totalLoans,
            savingsToLoanRatio = savingsToLoanRatio,
            financialHealthScore = calculateFinancialHealthScore(totalSavings, totalLoans, savingMembers),
            recommendations = generateFinancialRecommendations(savingsRate, savingsToLoanRatio)
        )
    }
    
    /**
     * Calculate optimal meeting times based on agricultural cycles
     * Many women work around farming schedules
     */
    fun calculateOptimalMeetingTimes(
        season: String,
        isAgriculturalArea: Boolean
    ): MeetingTimeRecommendation {
        return if (isAgriculturalArea) {
            when (season.lowercase()) {
                "kharif" -> MeetingTimeRecommendation(
                    times = listOf("6:00 AM", "7:00 PM"), 
                    reason = "Before farming work"
                )
                "rabi" -> MeetingTimeRecommendation(
                    times = listOf("5:00 PM", "6:00 PM"), 
                    reason = "After farming work"
                )
                "zaid" -> MeetingTimeRecommendation(
                    times = listOf("10:00 AM", "2:00 PM"), 
                    reason = "Between harvest seasons"
                )
                else -> MeetingTimeRecommendation(
                    times = listOf("6:00 PM"), 
                    reason = "Standard evening time"
                )
            }
        } else {
            MeetingTimeRecommendation(
                times = listOf("6:00 PM"), 
                reason = "Standard evening time"
            )
        }
    }
    
    /**
     * Generate vernacular language content for better accessibility
     * Support multiple Indian languages
     */
    fun generateVernacularContent(
        contentType: VernacularContentType,
        language: String = "hi" // Default to Hindi
    ): VernacularContent {
        return when (contentType) {
            VernacularContentType.LOAN_INSTRUCTIONS -> when (language) {
                "hi" -> VernacularContent("ऋण सावँने के लिए निर्देशन के लियमों का पालन करें", language)
                "mr" -> VernacularContent("कर्ज घेण्या सावँने के लिए निर्देशन के लियमों का पालन करें", language)
                "gu" -> VernacularContent("સામુક લેવા માટેલા લોનનું લેવા માટેલા", language)
                else -> VernacularContent("Loan instructions for group members", language)
            }
            VernacularContentType.SAVINGS_TIPS -> when (language) {
                "hi" -> VernacularContent("बचत बचत करने के लिए सरल टिप्स", language)
                "mr" -> VernacularContent("बचत बचत करण्यासाठी टिप्स", language)
                "gu" -> VernacularContent("બચત બચત કરવા માટેલા ટિપ્સ", language)
                else -> VernacularContent("Regular savings tips", language)
            }
            VernacularContentType.EMERGENCY_CONTACT -> when (language) {
                "hi" -> VernacularContent("आपातकाली स्थितियों", language)
                "mr" -> VernacularContent("आपातकाली स्थितियां", language)
                "gu" -> VernacularContent("આપાતકાલી સંપર્ક", language)
                else -> VernacularContent("Emergency contacts", language)
            }
        }
    }
    
    // ===========================================
    // UTILITY FUNCTIONS
    // ===========================================
    
    private fun calculateMonthsInGroup(joinDate: Long): Int {
        val now = System.currentTimeMillis()
        val diff = now - joinDate
        return (diff / (1000L * 60L * 60L * 24L * 30L)).toInt()
    }
    
    private fun calculateFinancialHealthScore(
        totalSavings: Double,
        totalLoans: Long,
        savingMembers: Int
    ): Int {
        val savingsWeight = if (totalLoans > 0) (totalSavings / totalLoans) * 100 else 100.0
        val participationWeight = if (savingMembers > 0) (savingMembers.toDouble() / maxOf(1, savingMembers)) * 100 else 0.0
        return ((savingsWeight + participationWeight) / 2).toInt() // Simple scoring system
    }
    
    private fun generateFinancialRecommendations(
        savingsRate: Double,
        savingsToLoanRatio: Double
    ): List<String> {
        val recommendations = mutableListOf<String>()
        
        if (savingsRate < 50) {
            recommendations.add("Increase member participation in savings programs")
        }
        
        if (savingsToLoanRatio < 1.0) {
            recommendations.add("Maintain savings equal to outstanding loans")
        }
        
        if (savingsToLoanRatio > 2.0) {
            recommendations.add("Excellent financial health - consider expanding loan program")
        }
        
        return recommendations
    }
}

// ===========================================
// DATA CLASSES FOR PRACTICAL FEATURES
// ===========================================

data class ComplianceReport(
    val isCompliant: Boolean,
    val attendanceRate: Double,
    val meetingsRequired: Int,
    val meetingsHeld: Int,
    val totalMembers: Int,
    val recommendation: String
)

data class LoanEligibilityResult(
    val isEligible: Boolean,
    val maxLoanAmount: Int,
    val totalSavings: Double,
    val averageMonthlySavings: Double,
    val monthsInGroup: Int,
    val groupAverageSavings: Double,
    val recommendation: String
)

data class MeetingReminder(
    val title: String,
    val date: Date,
    val type: MeetingType,
    val importance: MeetingImportance
)

data class SMSReminder(
    val recipientName: String,
    val phoneNumber: String,
    val message: String,
    val estimatedCost: Double,
    val deliveryMethod: String,
    val suggestedAlternative: String
)

data class EmergencyContactList(
    val contacts: List<EmergencyContact>,
    val totalContacts: Int,
    val localContacts: Int,
    val internationalContacts: Int,
    val recommendation: String
)

data class EmergencyContact(
    val memberName: String,
    val phoneNumber: String,
    val relationship: String,
    val priority: EmergencyPriority
)

data class FinancialLiteracyReport(
    val totalMembers: Int,
    val savingMembers: Int,
    val borrowingMembers: Int,
    val savingsRate: Double,
    val totalSavings: Double,
    val totalLoans: Long,
    val savingsToLoanRatio: Double,
    val financialHealthScore: Int,
    val recommendations: List<String>
)

data class MeetingTimeRecommendation(
    val times: List<String>,
    val reason: String
)

data class VernacularContent(
    val content: String,
    val language: String
)

// ===========================================
// ENUMS FOR PRACTICAL FEATURES
// ===========================================

enum class MeetingType {
    REGULAR, SPECIAL, EMERGENCY
}

enum class MeetingImportance {
    HIGH, MEDIUM, LOW
}

enum class MeetingFrequency {
    WEEKLY, MONTHLY, QUARTERLY
}

enum class EmergencyPriority {
    HIGH, MEDIUM, LOW
}

enum class VernacularContentType {
    LOAN_INSTRUCTIONS, SAVINGS_TIPS, EMERGENCY_CONTACT
}
