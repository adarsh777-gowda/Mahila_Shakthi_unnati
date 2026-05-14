package com.adarsh.mahilashaktiunnati.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adarsh.mahilashaktiunnati.R
import com.adarsh.mahilashaktiunnati.utils.LanguageManager

@Composable
fun LanguageSelector(
    context: android.content.Context,
    onLanguageChanged: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }
    val currentLanguage = LanguageManager.getCurrentLanguageName(context)
    
    // Language toggle button
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (LanguageManager.isKannadaSelected(context)) 
                    Color(0xFFE91E63) 
                else 
                    Color(0xFF2196F3)
            )
            .clickable { showDialog = true }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "${stringResource(R.string.language)}: $currentLanguage",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
    
    // Language selection dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.select_language),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // English option
                    LanguageOption(
                        language = "English",
                        isSelected = !LanguageManager.isKannadaSelected(context),
                        languageCode = "en",
                        context = context,
                        onSelected = {
                            LanguageManager.setLanguage(context, "en")
                            showDialog = false
                            onLanguageChanged()
                        }
                    )
                    
                    // Kannada option
                    LanguageOption(
                        language = stringResource(R.string.kannada),
                        isSelected = LanguageManager.isKannadaSelected(context),
                        languageCode = "kn",
                        context = context,
                        onSelected = {
                            LanguageManager.setLanguage(context, "kn")
                            showDialog = false
                            onLanguageChanged()
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        )
    }
}

@Composable
private fun LanguageOption(
    language: String,
    isSelected: Boolean,
    languageCode: String,
    context: android.content.Context,
    onSelected: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        when (languageCode) {
            "kn" -> Color(0xFFE91E63).copy(alpha = 0.2f)
            else -> Color(0xFF2196F3).copy(alpha = 0.2f)
        }
    } else {
        Color.Transparent
    }
    
    val borderColor = if (isSelected) {
        when (languageCode) {
            "kn" -> Color(0xFFE91E63)
            else -> Color(0xFF2196F3)
        }
    } else {
        Color.Gray.copy(alpha = 0.3f)
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = { onSelected() },
                colors = RadioButtonDefaults.colors(
                    selectedColor = borderColor
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = language,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) borderColor else Color.Black
            )
        }
    }
}
