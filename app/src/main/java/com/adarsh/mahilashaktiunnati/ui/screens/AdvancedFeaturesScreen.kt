package com.adarsh.mahilashaktiunnati.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adarsh.mahilashaktiunnati.R
import com.adarsh.mahilashaktiunnati.ui.components.LanguageSelector
import com.adarsh.mahilashaktiunnati.ui.theme.MahilaShaktiUnnatiTheme
import com.adarsh.mahilashaktiunnati.viewmodel.MemberViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedFeaturesScreen(
    context: android.content.Context,
    viewModel: MemberViewModel,
    onBack: () -> Unit,
    onLanguageChanged: () -> Unit = {}
) {
    // Note: viewModel is currently not used but kept for consistency with other screens
    AdvancedFeaturesContent(
        context = context,
        onBack = onBack,
        onLanguageChanged = onLanguageChanged
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedFeaturesContent(
    context: android.content.Context,
    onBack: () -> Unit,
    onLanguageChanged: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Language Selector
        LanguageSelector(
            context = context,
            onLanguageChanged = onLanguageChanged
        )
        
        // Header
        Text(
            text = stringResource(R.string.advanced_features),
            style = MaterialTheme.typography.headlineMedium
        )
        
        Text(
            text = stringResource(R.string.production_tools_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tab Selection
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val tabs = listOf(
                stringResource(R.string.voice_recording) to stringResource(R.string.voice_recording_tab),
                stringResource(R.string.biometrics) to stringResource(R.string.biometrics_tab),
                stringResource(R.string.qr_attendance) to stringResource(R.string.qr_attendance_tab),
                stringResource(R.string.loan_reminders) to stringResource(R.string.loan_reminders_tab),
                stringResource(R.string.multi_language) to stringResource(R.string.multi_language_tab),
                stringResource(R.string.emergency_backup) to stringResource(R.string.emergency_backup_tab)
            )
            
            // Display tabs in rows of 2 to avoid cramped layout
            tabs.chunked(2).forEachIndexed { rowIndex, rowTabs ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowTabs.forEachIndexed { colIndex, tab ->
                        val index = rowIndex * 2 + colIndex
                        FilterChip(
                            onClick = { selectedTab = index },
                            label = { Text(tab.second) },
                            selected = selectedTab == index,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Content based on selected tab
        when (selectedTab) {
            0 -> SimpleAdvancedSection(stringResource(R.string.voice_recording), stringResource(R.string.voice_recording_description))
            1 -> SimpleAdvancedSection(stringResource(R.string.biometrics), stringResource(R.string.biometrics_description))
            2 -> SimpleAdvancedSection(stringResource(R.string.qr_attendance), stringResource(R.string.qr_attendance_description))
            3 -> SimpleAdvancedSection(stringResource(R.string.loan_reminders), stringResource(R.string.loan_reminders_description))
            4 -> SimpleAdvancedSection(stringResource(R.string.multi_language), stringResource(R.string.multi_language_description))
            5 -> SimpleAdvancedSection(stringResource(R.string.emergency_backup), stringResource(R.string.emergency_backup_description))
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Back Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.back_to_dashboard))
            }
        }
    }
}

@Composable
private fun SimpleAdvancedSection(
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { /* TODO: Implement feature */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.coming_soon))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AdvancedFeaturesScreenPreview() {
    MahilaShaktiUnnatiTheme {
        AdvancedFeaturesContent(
            context = LocalContext.current,
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SimpleAdvancedSectionPreview() {
    MahilaShaktiUnnatiTheme {
        SimpleAdvancedSection(
            title = "Voice Recording",
            description = "Record voice notes for members and meetings."
        )
    }
}
