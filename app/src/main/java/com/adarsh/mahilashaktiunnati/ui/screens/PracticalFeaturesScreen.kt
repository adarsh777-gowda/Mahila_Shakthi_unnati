package com.adarsh.mahilashaktiunnati.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.adarsh.mahilashaktiunnati.R
import com.adarsh.mahilashaktiunnati.viewmodel.MemberViewModel
import com.adarsh.mahilashaktiunnati.utils.LanguageManager
import com.adarsh.mahilashaktiunnati.ui.components.LanguageSelector

@Composable
fun PracticalFeaturesScreen(
    context: android.content.Context,
    viewModel: MemberViewModel,
    onBack: () -> Unit,
    onLanguageChanged: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    
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
            text = stringResource(R.string.practical_features),
            style = MaterialTheme.typography.headlineMedium
        )
        
        Text(
            text = stringResource(R.string.essential_tools_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tab Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tabs = listOf(
        stringResource(R.string.compliance) to stringResource(R.string.compliance_tab),
        stringResource(R.string.loan_eligibility) to stringResource(R.string.loan_eligibility_tab),
        stringResource(R.string.meetings) to stringResource(R.string.meetings_tab),
        stringResource(R.string.sms) to stringResource(R.string.sms_tab),
        stringResource(R.string.emergency) to stringResource(R.string.emergency_tab),
        stringResource(R.string.financial) to stringResource(R.string.financial_tab),
        stringResource(R.string.vernacular) to stringResource(R.string.vernacular_tab)
    )
            tabs.forEachIndexed { index, tab ->
                FilterChip(
                    onClick = { selectedTab = index },
                    label = { Text(tab.second) },
                    selected = selectedTab == index,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Content based on selected tab
        when (selectedTab) {
            0 -> SimpleSection(stringResource(R.string.compliance), stringResource(R.string.compliance_description))
            1 -> SimpleSection(stringResource(R.string.loan_eligibility), stringResource(R.string.loan_eligibility_description))
            2 -> SimpleSection(stringResource(R.string.meetings), stringResource(R.string.meetings_description))
            3 -> SimpleSection(stringResource(R.string.sms), stringResource(R.string.sms_description))
            4 -> SimpleSection(stringResource(R.string.emergency), stringResource(R.string.emergency_description))
            5 -> SimpleSection(stringResource(R.string.financial), stringResource(R.string.financial_description))
            6 -> SimpleSection(stringResource(R.string.vernacular), stringResource(R.string.vernacular_description))
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Back Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.back_to_dashboard))
            }
        }
    }
}

@Composable
private fun SimpleSection(
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
