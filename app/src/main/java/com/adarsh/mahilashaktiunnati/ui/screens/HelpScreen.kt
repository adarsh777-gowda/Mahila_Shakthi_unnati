package com.adarsh.mahilashaktiunnati.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adarsh.mahilashaktiunnati.R
import com.adarsh.mahilashaktiunnati.ui.components.LanguageSelector

@Composable
fun HelpScreen(
    context: android.content.Context,
    onBack: () -> Unit,
    onLanguageChanged: () -> Unit = {}
) {
    val cardBackground = Color(0xFFF8F9FA)
    val accentColor = Color(0xFFE91E63)
    val successColor = Color(0xFF4CAF50)
    val infoColor = Color(0xFF2196F3)
    
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
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
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
                text = stringResource(R.string.help),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF880E4F)
            )
            
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color(0xFF880E4F)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(R.string.help_description),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF424242),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )
        Text(
            text = "ನಮ್ಮ ಸಹಾಯ ಮತ್ತು ಬೆಂಬಲ ಕೇಂದ್ರಕ್ಕೆ ಸ್ವಾಗತ",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF616161),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Help Categories
        val helpCategories = listOf(
            HelpCategory(
                icon = "📱",
                title = stringResource(R.string.loan_management),
                description = stringResource(R.string.loan_management_description),
                questions = listOf(
                    stringResource(R.string.loan_question_1),
                    stringResource(R.string.loan_question_2),
                    stringResource(R.string.loan_question_3)
                )
            ),
            HelpCategory(
                icon = "💰",
                title = stringResource(R.string.savings_management),
                description = stringResource(R.string.savings_management_description),
                questions = listOf(
                    stringResource(R.string.savings_question_1),
                    stringResource(R.string.savings_question_2),
                    stringResource(R.string.savings_question_3)
                )
            ),
            HelpCategory(
                icon = "📚",
                title = stringResource(R.string.technical_support),
                description = stringResource(R.string.technical_support_description),
                questions = listOf(
                    stringResource(R.string.technical_question_1),
                    stringResource(R.string.technical_question_2),
                    stringResource(R.string.technical_question_3)
                )
            ),
            HelpCategory(
                icon = "🤖",
                title = "AI ಸಹಾಯ",
                description = "AI ಮಾರ್ಗದರ್ಶನ ಸಹಾಯ",
                questions = listOf(
                    "ನಿಮ್ಮ ಉಳಿತಾಯದ ಟ್ರೆಂಡ್ ಅನ್ನು ಹೇಗೆ ನೋಡುವುದು?",
                    "ಸಾಲದ ಅರ್ಹತೆಯನ್ನು ಹೇಗೆ ತಿಳಿಯುವುದು?",
                    "AI ಸಹಾಯಕನೊಂದಿಗೆ ಹೇಗೆ ಮಾತನಾಡುವುದು?",
                    "ವರದಿಗಳನ್ನು ಹೇಗೆ ರಚಿಸುವುದು?"
                )
            )
        )
        
        helpCategories.forEach { category ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = cardBackground
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = category.icon,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = category.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF424242)
                            )
                            
                            Text(
                                text = category.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF616161),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        
                        IconButton(
                            onClick = { /* Navigate to detailed help */ }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Help,
                                contentDescription = "Get help",
                                tint = accentColor
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Quick Help Buttons
        Text(
            text = "ತ್ವರಿತ ಸಹಾಯ",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF424242),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { /* Call support */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = infoColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "📞 ಕರೆ ಮಾಡಿ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Button(
                onClick = { /* Video tutorial */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = successColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "📹 ವಿಡಿಯೋ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

data class HelpCategory(
    val icon: String,
    val title: String,
    val description: String,
    val questions: List<String>
)
