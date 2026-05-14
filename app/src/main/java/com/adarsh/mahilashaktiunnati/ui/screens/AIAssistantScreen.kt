package com.adarsh.mahilashaktiunnati.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adarsh.mahilashaktiunnati.R
import com.adarsh.mahilashaktiunnati.ai.FinancialInsight
import com.adarsh.mahilashaktiunnati.ai.PredictiveAnalysis
import com.adarsh.mahilashaktiunnati.ai.Priority
import com.adarsh.mahilashaktiunnati.ai.InsightType
import com.adarsh.mahilashaktiunnati.viewmodel.AIViewModel
import com.adarsh.mahilashaktiunnati.viewmodel.MemberViewModel
import com.adarsh.mahilashaktiunnati.utils.LanguageManager
import com.adarsh.mahilashaktiunnati.ui.components.LanguageSelector
import kotlinx.coroutines.launch

val Orange = Color(0xFFFFA500)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantScreen(
    context: android.content.Context,
    aiViewModel: AIViewModel,
    memberViewModel: MemberViewModel,
    onBack: () -> Unit,
    onLanguageChanged: () -> Unit = {}
) {
    val chatMessages by aiViewModel.chatMessages.collectAsState()
    val insights by aiViewModel.insights.collectAsState()
    val predictions by aiViewModel.predictions.collectAsState()
    val isAnalyzing by aiViewModel.isAnalyzing.collectAsState()
    val aiMessage by aiViewModel.aiMessage.collectAsState()
    
    val members by memberViewModel.members.collectAsState()
    // For AI analysis, we might need all savings and loans, not just for member 0
    // If the ViewModel has these as StateFlows, we should use those.
    // Assuming we need all data for general analysis.
    val totalSavings by memberViewModel.totalSavings.collectAsState()
    val totalLoan by memberViewModel.totalLoan.collectAsState()
    
    var query by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    // Auto-scroll to latest message
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(chatMessages.size - 1)
            }
        }
    }
    
    // Show AI message snackbar
    LaunchedEffect(aiMessage) {
        aiMessage?.let {
            // Show message handling (you can implement snackbar here if needed)
            aiViewModel.consumeMessage()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "AI Assistant",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.ai_assistant),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Row {
                IconButton(
                    onClick = { 
                        // Note: Passing empty lists if specific ones aren't available yet
                        // Ideally AIViewModel should fetch its own data or take it from MemberViewModel
                        aiViewModel.analyzeData(members.map { com.adarsh.mahilashaktiunnati.data.entities.Member(it.id, it.name, it.phone, joinDate = System.currentTimeMillis(), isActive = true) }, emptyList(), emptyList())
                    },
                    enabled = !isAnalyzing
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = stringResource(R.string.analyze_data),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                IconButton(
                    onClick = { aiViewModel.clearChat() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.clear_chat)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Quick Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { 
                    aiViewModel.sendMessage(stringResource(R.string.analyze_savings_trends), members.map { com.adarsh.mahilashaktiunnati.data.entities.Member(it.id, it.name, it.phone, joinDate = System.currentTimeMillis(), isActive = true) }, emptyList(), emptyList())
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.savings), fontSize = 12.sp)
            }
            
            Button(
                onClick = { 
                    aiViewModel.sendMessage(stringResource(R.string.assess_loan_risks), members.map { com.adarsh.mahilashaktiunnati.data.entities.Member(it.id, it.name, it.phone, joinDate = System.currentTimeMillis(), isActive = true) }, emptyList(), emptyList())
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.loans), fontSize = 12.sp)
            }
            
            Button(
                onClick = { 
                    aiViewModel.analyzeData(members.map { com.adarsh.mahilashaktiunnati.data.entities.Member(it.id, it.name, it.phone, joinDate = System.currentTimeMillis(), isActive = true) }, emptyList(), emptyList())
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.analyze_all_data), fontSize = 12.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Chat Messages
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(chatMessages) { message ->
                    ChatMessageItem(message = message)
                }
            }
        }
        
        // Insights Section (if available)
        if (insights.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            InsightsSection(insights = insights)
        }
        
        // Predictions Section (if available)
        if (predictions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            PredictionsSection(predictions = predictions)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Input Area
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Ask AI about your financial data...") },
                modifier = Modifier.weight(1f),
                enabled = !isAnalyzing,
                maxLines = 3
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            IconButton(
                onClick = {
                    if (query.isNotBlank()) {
                        aiViewModel.sendMessage(query, members.map { com.adarsh.mahilashaktiunnati.data.entities.Member(it.id, it.name, it.phone, joinDate = System.currentTimeMillis(), isActive = true) }, emptyList(), emptyList())
                        query = ""
                    }
                },
                enabled = !isAnalyzing && query.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ChatMessageItem(message: com.adarsh.mahilashaktiunnati.ai.ChatMessage) {
    val backgroundColor = if (message.isUser) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    
    val alignment = if (message.isUser) {
        Arrangement.End
    } else {
        Arrangement.Start
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = alignment
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(backgroundColor)
                .padding(12.dp)
                .fillMaxWidth(0.8f)
        ) {
            Column {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (message.isUser) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

@Composable
private fun InsightsSection(insights: List<FinancialInsight>) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🔍 AI Insights",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            insights.take(3).forEach { insight ->
                InsightItem(insight = insight)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun InsightItem(insight: FinancialInsight) {
    val priorityColor = when (insight.priority) {
        Priority.HIGH -> Red
        Priority.MEDIUM -> Orange
        Priority.LOW -> Green
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = priorityColor.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = insight.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(priorityColor)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = insight.priority.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = insight.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (insight.actionable && insight.recommendation != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "💡 ${insight.recommendation}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun PredictionsSection(predictions: List<PredictiveAnalysis>) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📊 Predictive Analytics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            predictions.forEach { prediction ->
                PredictionItem(prediction = prediction)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun PredictionItem(prediction: PredictiveAnalysis) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = prediction.category,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = prediction.prediction,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Confidence: ${(prediction.confidence * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "• ${prediction.timeframe}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
