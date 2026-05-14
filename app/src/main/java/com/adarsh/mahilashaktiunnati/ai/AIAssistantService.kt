package com.adarsh.mahilashaktiunnati.ai

import com.adarsh.mahilashaktiunnati.data.entities.Member
import com.adarsh.mahilashaktiunnati.data.entities.Savings
import com.adarsh.mahilashaktiunnati.data.entities.Loan
import java.util.UUID

data class FinancialInsight(
    val title: String,
    val description: String,
    val type: InsightType,
    val priority: Priority,
    val actionable: Boolean,
    val recommendation: String? = null
)

enum class InsightType {
    SAVINGS_TREND,
    LOAN_RISK,
    MEMBER_ENGAGEMENT,
    FINANCIAL_HEALTH,
    RECOMMENDATION
}

enum class Priority {
    HIGH,
    MEDIUM,
    LOW
}

data class PredictiveAnalysis(
    val category: String,
    val prediction: String,
    val confidence: Double,
    val timeframe: String,
    val factors: List<String>
)

data class ChatMessage(
    val id: String,
    val content: String,
    val isUser: Boolean,
    val timestamp: Long,
    val insights: List<FinancialInsight> = emptyList()
)

class AIAssistantService {
    
    fun analyzeFinancialData(
        members: List<Member>,
        savings: List<Savings>,
        loans: List<Loan>
    ): List<FinancialInsight> {
        val insights = mutableListOf<FinancialInsight>()
        
        // Analyze savings trends
        insights.addAll(analyzeSavingsTrends(savings, members))
        
        // Analyze loan risks
        insights.addAll(analyzeLoanRisks(loans, members))
        
        // Analyze member engagement
        insights.addAll(analyzeMemberEngagement(members, savings, loans))
        
        // Analyze overall financial health
        insights.addAll(analyzeFinancialHealth(savings, loans))
        
        return insights.sortedByDescending { it.priority.ordinal }
    }
    
    fun generatePredictiveAnalysis(
        savings: List<Savings>,
        loans: List<Loan>
    ): List<PredictiveAnalysis> {
        val predictions = mutableListOf<PredictiveAnalysis>()
        
        // Predict savings growth
        if (savings.size >= 3) {
            val recentSavings = savings.takeLast(10)
            val trend = calculateSavingsTrend(recentSavings)
            val prediction = PredictiveAnalysis(
                category = "Savings Growth",
                prediction = "Based on recent trends, savings are showing ${if (trend.confidence > 0.6) "positive" else "neutral"} growth",
                confidence = trend.confidence,
                timeframe = "Next 3 months",
                factors = listOf("Recent savings pattern", "Member participation rate")
            )
            predictions.add(prediction)
        }
        
        // Predict loan repayment risks
        val unpaidLoans = loans.filter { !it.isPaid }
        if (unpaidLoans.isNotEmpty()) {
            val riskAnalysis = PredictiveAnalysis(
                category = "Loan Risk",
                prediction = "${unpaidLoans.size} active loans require monitoring",
                confidence = 0.8,
                timeframe = "Next 30 days",
                factors = listOf("Number of active loans", "Payment history")
            )
            predictions.add(riskAnalysis)
        }
        
        return predictions
    }
    
    fun processUserQuery(
        query: String,
        members: List<Member>,
        savings: List<Savings>,
        loans: List<Loan>
    ): ChatMessage {
        val response = when {
            query.contains("savings", ignoreCase = true) -> {
                val totalSavings = savings.sumOf { it.amount }
                val avgSavings = if (members.isNotEmpty()) totalSavings.toDouble() / members.size else 0.0
                "Total group savings: ₹$totalSavings. Average per member: ₹$avgSavings"
            }
            query.contains("loan", ignoreCase = true) -> {
                val activeLoans = loans.count { !it.isPaid }
                val totalLoanAmount = loans.filter { !it.isPaid }.sumOf { it.principalAmount }
                "Active loans: $activeLoans. Total amount: ₹$totalLoanAmount"
            }
            query.contains("member", ignoreCase = true) -> {
                "Total members: ${members.size}. Active members: ${members.count { it.isActive }}"
            }
            else -> {
                "I can help you with information about savings, loans, and members. Please ask a specific question."
            }
        }
        
        return ChatMessage(
            id = UUID.randomUUID().toString(),
            content = response,
            timestamp = System.currentTimeMillis(),
            isUser = false,
            insights = analyzeFinancialData(members, savings, loans)
        )
    }
    
    fun generateRecommendations(insights: List<FinancialInsight>): List<String> {
        val recommendations = mutableListOf<String>()
        
        insights.forEach { insight ->
            when (insight.type) {
                InsightType.SAVINGS_TREND -> {
                    if (insight.priority == Priority.HIGH) {
                        recommendations.add("Consider organizing a savings motivation meeting")
                    }
                }
                InsightType.LOAN_RISK -> {
                    recommendations.add("Review loan repayment schedules and follow up with members")
                }
                InsightType.MEMBER_ENGAGEMENT -> {
                    recommendations.add("Plan engagement activities to improve member participation")
                }
                else -> {
                    insight.recommendation?.let { recommendations.add(it) }
                }
            }
        }
        
        return recommendations.distinct()
    }
    
    private fun analyzeSavingsTrends(savings: List<Savings>, members: List<Member>): List<FinancialInsight> {
        val insights = mutableListOf<FinancialInsight>()
        
        if (savings.isEmpty()) return insights
        
        val totalSavings = savings.sumOf { it.amount }
        val avgSavingsPerMember = if (members.isNotEmpty()) totalSavings.toDouble() / members.size else 0.0
        val weeklySavings = savings.groupBy { 
            java.util.Calendar.getInstance().apply {
                time = java.util.Date(it.date)
                get(java.util.Calendar.WEEK_OF_YEAR)
            }
        }
        
        // Find best performing week
        val bestWeek = weeklySavings.maxByOrNull { entry -> entry.value.sumOf { saving -> saving.amount } }
        bestWeek?.let {
            insights.add(
                FinancialInsight(
                    title = "Peak Savings Week",
                    description = "Week ${it.key} shows highest savings activity with ₹${it.value.sumOf { saving -> saving.amount }}",
                    type = InsightType.SAVINGS_TREND,
                    priority = Priority.MEDIUM,
                    actionable = true,
                    recommendation = "Consider organizing more awareness campaigns during this week period"
                )
            )
        }
        
        // Low savings warning
        if (avgSavingsPerMember < 500.0) {
            insights.add(
                FinancialInsight(
                    title = "Low Average Savings",
                    description = "Average savings per member is ₹$avgSavingsPerMember, which is below recommended levels",
                    type = InsightType.RECOMMENDATION,
                    priority = Priority.HIGH,
                    actionable = true,
                    recommendation = "Implement savings encouragement programs and financial literacy workshops"
                )
            )
        }
        
        return insights
    }
    
    private fun analyzeLoanRisks(loans: List<Loan>, members: List<Member>): List<FinancialInsight> {
        val insights = mutableListOf<FinancialInsight>()
        
        if (loans.isEmpty()) return insights
        
        val pendingLoans = loans.filter { !it.isPaid }
        val totalLoanAmount = pendingLoans.sumOf { it.principalAmount }
        val defaultRiskMembers = analyzeDefaultRisk(pendingLoans, members)
        
        // High loan concentration warning
        if (totalLoanAmount > 50000) {
            insights.add(
                FinancialInsight(
                    title = "High Loan Exposure",
                    description = "Total pending loans amount to ₹$totalLoanAmount, which represents significant risk",
                    type = InsightType.LOAN_RISK,
                    priority = Priority.HIGH,
                    actionable = true,
                    recommendation = "Review loan approval criteria and consider reducing loan amounts"
                )
            )
        }
        
        // Member-specific risk alerts
        defaultRiskMembers.forEach { member ->
            insights.add(
                FinancialInsight(
                    title = "Loan Default Risk",
                    description = "Member ${member.name} shows signs of potential loan default",
                    type = InsightType.LOAN_RISK,
                    priority = Priority.HIGH,
                    actionable = true,
                    recommendation = "Schedule counseling session and review repayment plan"
                )
            )
        }
        
        return insights
    }
    
    private fun analyzeMemberEngagement(
        members: List<Member>,
        savings: List<Savings>,
        loans: List<Loan>
    ): List<FinancialInsight> {
        val insights = mutableListOf<FinancialInsight>()
        
        val activeMembers = members.filter { member ->
            savings.any { it.memberId == member.id } || loans.any { it.memberId == member.id }
        }
        
        val inactiveMembers = members - activeMembers.toSet()
        
        if (inactiveMembers.isNotEmpty()) {
            insights.add(
                FinancialInsight(
                    title = "Inactive Members",
                    description = "${inactiveMembers.size} members show no financial activity",
                    type = InsightType.MEMBER_ENGAGEMENT,
                    priority = Priority.MEDIUM,
                    actionable = true,
                    recommendation = "Reach out to inactive members and understand their concerns"
                )
            )
        }
        
        // Highly engaged members
        val highlyEngaged = activeMembers.filter { member ->
            savings.count { it.memberId == member.id } >= 4 || loans.count { it.memberId == member.id } >= 2
        }
        
        if (highlyEngaged.isNotEmpty()) {
            insights.add(
                FinancialInsight(
                    title = "Highly Engaged Members",
                    description = "${highlyEngaged.size} members show excellent participation",
                    type = InsightType.MEMBER_ENGAGEMENT,
                    priority = Priority.LOW,
                    actionable = true,
                    recommendation = "Recognize these members and consider them for leadership roles"
                )
            )
        }
        
        return insights
    }
    
    private fun analyzeFinancialHealth(
        savings: List<Savings>,
        loans: List<Loan>
    ): List<FinancialInsight> {
        val insights = mutableListOf<FinancialInsight>()
        
        val totalSavings = savings.sumOf { it.amount }
        val totalLoans = loans.filter { !it.isPaid }.sumOf { it.principalAmount }
        val healthRatio = if (totalLoans > 0) totalSavings.toDouble() / totalLoans.toDouble() else Double.POSITIVE_INFINITY
        
        when {
            healthRatio < 0.5 -> {
                insights.add(
                    FinancialInsight(
                        title = "Critical Financial Health",
                        description = "Savings to loan ratio is ${(healthRatio * 100).toInt()}%, indicating high risk",
                        type = InsightType.FINANCIAL_HEALTH,
                        priority = Priority.HIGH,
                        actionable = true,
                        recommendation = "Immediate action required: freeze new loans and boost savings campaigns"
                    )
                )
            }
            healthRatio < 1.0 -> {
                insights.add(
                    FinancialInsight(
                        title = "Moderate Financial Risk",
                        description = "Savings to loan ratio is ${(healthRatio * 100).toInt()}%, needs attention",
                        type = InsightType.FINANCIAL_HEALTH,
                        priority = Priority.MEDIUM,
                        actionable = true,
                        recommendation = "Increase savings targets and review loan policies"
                    )
                )
            }
            else -> {
                insights.add(
                    FinancialInsight(
                        title = "Healthy Financial Position",
                        description = "Savings to loan ratio is excellent at ${(healthRatio * 100).toInt()}%",
                        type = InsightType.FINANCIAL_HEALTH,
                        priority = Priority.LOW,
                        actionable = false
                    )
                )
            }
        }
        
        return insights
    }
    
    // Helper functions for predictive analysis
    private fun calculateSavingsTrend(savings: List<Savings>): SavingsTrend {
        // Simple linear regression for trend calculation
        val weeklyData = savings.groupBy { 
            java.util.Calendar.getInstance().apply {
                time = java.util.Date(it.date)
                get(java.util.Calendar.WEEK_OF_YEAR)
            }
        }.map { (week, weekSavings) ->
            week to weekSavings.sumOf { it.amount }
        }.sortedBy { it.first }
        
        if (weeklyData.size < 2) {
            return SavingsTrend(0, 0.5, listOf("Insufficient data"))
        }
        
        val trend = calculateLinearTrend(weeklyData.map { it.second.toDouble() })
        val nextMonthPrediction = (weeklyData.last().second + trend).toInt()
        
        return SavingsTrend(
            nextMonthPrediction = nextMonthPrediction,
            confidence = if (weeklyData.size >= 4) 0.8 else 0.5,
            factors = listOf("Historical trend", "Seasonal patterns", "Member participation")
        )
    }
    
    private fun analyzeDefaultRisk(loans: List<Loan>, members: List<Member>): List<Member> {
        // Identify members with potential default risk
        return members.filter { member ->
            val memberLoans = loans.filter { it.memberId == member.id && !it.isPaid }
            memberLoans.isNotEmpty() && memberLoans.sumOf { it.principalAmount } > 10000 // High loan amounts
        }
    }
    
    private fun calculateLinearTrend(values: List<Double>): Double {
        if (values.size < 2) return 0.0
        
        val n = values.size.toDouble()
        val sumX = values.indices.sumOf { it.toDouble() }
        val sumY = values.sum()
        val sumXY = values.mapIndexed { index, value -> index.toDouble() * value }.sum()
        val sumX2 = values.indices.sumOf { it.toDouble() * it.toDouble() }
        
        return (n * sumXY - sumX * sumY).toDouble() / (n * sumX2 - sumX * sumX).toDouble()
    }
    
    data class SavingsTrend(
        val nextMonthPrediction: Int,
        val confidence: Double,
        val factors: List<String>
    )
}
