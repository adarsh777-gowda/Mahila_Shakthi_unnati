package com.adarsh.mahilashaktiunnati.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adarsh.mahilashaktiunnati.ai.AIAssistantService
import com.adarsh.mahilashaktiunnati.ai.ChatMessage
import com.adarsh.mahilashaktiunnati.ai.FinancialInsight
import com.adarsh.mahilashaktiunnati.ai.PredictiveAnalysis
import com.adarsh.mahilashaktiunnati.data.entities.Member
import com.adarsh.mahilashaktiunnati.data.entities.Savings
import com.adarsh.mahilashaktiunnati.data.entities.Loan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AIViewModel : ViewModel() {
    
    private val aiService = AIAssistantService()
    
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()
    
    private val _insights = MutableStateFlow<List<FinancialInsight>>(emptyList())
    val insights: StateFlow<List<FinancialInsight>> = _insights.asStateFlow()
    
    private val _predictions = MutableStateFlow<List<PredictiveAnalysis>>(emptyList())
    val predictions: StateFlow<List<PredictiveAnalysis>> = _predictions.asStateFlow()
    
    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()
    
    private val _aiMessage = MutableStateFlow<String?>(null)
    val aiMessage: StateFlow<String?> = _aiMessage.asStateFlow()
    
    fun analyzeData(members: List<Member>, savings: List<Savings>, loans: List<Loan>) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            try {
                val newInsights = aiService.analyzeFinancialData(members, savings, loans)
                val newPredictions = aiService.generatePredictiveAnalysis(members, savings, loans)
                
                _insights.value = newInsights
                _predictions.value = newPredictions
                
                _aiMessage.value = "AI analysis complete! Found ${newInsights.size} insights and ${newPredictions.size} predictions."
            } catch (e: Exception) {
                _aiMessage.value = "Analysis failed: ${e.message}"
            } finally {
                _isAnalyzing.value = false
            }
        }
    }
    
    fun sendMessage(
        query: String,
        members: List<com.adarsh.mahilashaktiunnati.data.entities.Member>,
        savings: List<com.adarsh.mahilashaktiunnati.data.entities.Savings>,
        loans: List<com.adarsh.mahilashaktiunnati.data.entities.Loan>
    ) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            
            // Add user message
            val userMessage = ChatMessage(
                id = System.currentTimeMillis().toString(),
                content = query,
                isUser = true,
                timestamp = System.currentTimeMillis()
            )
            
            val updatedMessages = _chatMessages.value + userMessage
            _chatMessages.value = updatedMessages
            
            try {
                // Generate AI response
                val aiResponse = aiService.processUserQuery(query, members, savings, loans)
                
                // Add AI response
                _chatMessages.value = updatedMessages + aiResponse
                
                // Update insights if new ones were generated
                if (aiResponse.insights.isNotEmpty()) {
                    _insights.value = aiResponse.insights
                }
                
            } catch (e: Exception) {
                val errorMessage = ChatMessage(
                    id = (System.currentTimeMillis() + 1).toString(),
                    content = "Sorry, I encountered an error: ${e.message}",
                    isUser = false,
                    timestamp = System.currentTimeMillis()
                )
                _chatMessages.value = updatedMessages + errorMessage
            } finally {
                _isAnalyzing.value = false
            }
        }
    }
    
    fun getRecommendations(members: List<Member>, savings: List<Savings>, loans: List<Loan>) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            try {
                val currentInsights = _insights.value
                val recommendations = aiService.generateRecommendations(currentInsights)
                
                val recommendationMessage = ChatMessage(
                    id = System.currentTimeMillis().toString(),
                    content = """
                        💡 **AI Recommendations**
                        
                        Based on your current data analysis:
                        
                        ${recommendations.joinToString("\n\n")}
                        
                        Would you like detailed implementation plans for any of these recommendations?
                    """.trimIndent(),
                    isUser = false,
                    timestamp = System.currentTimeMillis()
                )
                
                _chatMessages.value = _chatMessages.value + recommendationMessage
                _aiMessage.value = "Generated ${recommendations.size} personalized recommendations"
                
            } catch (e: Exception) {
                _aiMessage.value = "Failed to generate recommendations: ${e.message}"
            } finally {
                _isAnalyzing.value = false
            }
        }
    }
    
    fun clearChat() {
        _chatMessages.value = emptyList()
        _aiMessage.value = "Chat cleared"
    }
    
    fun consumeMessage() {
        _aiMessage.value = null
    }
}
