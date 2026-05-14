package com.adarsh.mahilashaktiunnati.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Settings
import com.adarsh.mahilashaktiunnati.ui.components.AnimatedCard
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.adarsh.mahilashaktiunnati.R
import com.adarsh.mahilashaktiunnati.generateDashboardPdfReport
import com.adarsh.mahilashaktiunnati.openPdf
import com.adarsh.mahilashaktiunnati.sharePdf
import com.adarsh.mahilashaktiunnati.utils.ValidationUtils
import com.adarsh.mahilashaktiunnati.viewmodel.MemberViewModel
import com.adarsh.mahilashaktiunnati.ui.components.LanguageSelector
import com.google.firebase.auth.FirebaseAuth
import coil.compose.AsyncImage

@Composable
fun DashboardScreen(
    context: android.content.Context,
    viewModel: MemberViewModel,
    isOffline: Boolean = false,
    onMemberSelected: (Int) -> Unit,
    onLogout: () -> Unit,
    onNavigateToAI: () -> Unit,
    onLanguageChanged: () -> Unit = {},
    onNavigateToPracticalFeatures: () -> Unit = {}
) {
    val members by viewModel.members.collectAsState()
    val totalSavings by viewModel.totalSavings.collectAsState()
    val totalLoan by viewModel.totalLoan.collectAsState(initial = 0L)
    val uiMessage by viewModel.uiMessage.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var query by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    val filteredMembers = if (query.isBlank()) {
        members
    } else {
        val q = query.trim().lowercase()
        members.filter { it.name.lowercase().contains(q) || it.phone.contains(q) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Language Selector
            LanguageSelector(
                context = context,
                onLanguageChanged = onLanguageChanged
            )
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                item {
                    // Sync status indicator
                    when (syncStatus) {
                        is MemberViewModel.SyncStatus.Syncing -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.syncing), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        is MemberViewModel.SyncStatus.Success -> {
                            Text(
                                text = stringResource(R.string.sync_completed),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        is MemberViewModel.SyncStatus.Error -> {
                            Text(
                                text = stringResource(R.string.sync_failed, (syncStatus as MemberViewModel.SyncStatus.Error).message ?: ""),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        else -> Unit
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.syncNow() },
                            enabled = syncStatus !is MemberViewModel.SyncStatus.Syncing,
                            modifier = Modifier.weight(1f)
                        ) { 
                            Text(if (syncStatus is MemberViewModel.SyncStatus.Syncing) "Syncing..." else "Sync to Cloud") 
                        }

                        Button(
                            onClick = { viewModel.syncFromFirestore() },
                            enabled = syncStatus !is MemberViewModel.SyncStatus.Syncing,
                            modifier = Modifier.weight(1f)
                        ) { 
                            Text(if (syncStatus is MemberViewModel.SyncStatus.Syncing) "Fetching..." else "Fetch from Cloud") 
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            FirebaseAuth.getInstance().signOut()
                            onLogout()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Logout") }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    // Offline status indicator
                    if (isOffline) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "📡", style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Offline Mode - Data will sync when connection is restored",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mahila-Shakti Dashboard",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        
                        Row {
                            IconButton(onClick = onNavigateToAI) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = "AI Assistant",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            IconButton(onClick = onNavigateToPracticalFeatures) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Practical Features",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SummaryCard(
                            title = "Total Savings",
                            value = "₹${totalSavings ?: 0}",
                            modifier = Modifier.weight(1f)
                        )
                        SummaryCard(
                            title = "Total Loan",
                            value = "₹${totalLoan ?: 0}",
                            modifier = Modifier.weight(1f)
                        )
                        SummaryCard(
                            title = "Members",
                            value = "${members.size}",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Add Member", style = MaterialTheme.typography.titleMedium)

                    OutlinedTextField(
                        value = name,
                        onValueChange = { 
                            name = it
                            nameError = null
                        },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = nameError != null,
                        supportingText = nameError?.let { { Text(it) } }
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { 
                            phone = it
                            phoneError = null
                        },
                        label = { Text("Phone") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = phoneError != null,
                        supportingText = phoneError?.let { { Text(it) } }
                    )

                    Button(
                        onClick = {
                            val nameValidation = ValidationUtils.validateName(name)
                            val phoneValidation = ValidationUtils.validatePhoneNumber(phone)
                            
                            if (nameValidation.isValid && phoneValidation.isValid) {
                                viewModel.addMember(name.trim(), phone.trim())
                                name = ""
                                phone = ""
                                nameError = null
                                phoneError = null
                            } else {
                                nameError = if (nameValidation.isValid) null else nameValidation.errorMessage
                                phoneError = if (phoneValidation.isValid) null else phoneValidation.errorMessage
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Add Member") }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Members", style = MaterialTheme.typography.titleMedium)

                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("Search by name/phone") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(filteredMembers) { member ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onMemberSelected(member.id) }
                    ) {
                        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = member.photoUri,
                                contentDescription = "Member photo",
                                modifier = Modifier.size(48.dp)
                            )
                            Column {
                                Text(member.name, style = MaterialTheme.typography.titleSmall)
                                Text(member.phone, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                item {
                    val localContext = LocalContext.current
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val file = generateDashboardPdfReport(
                                    context = localContext,
                                    members = members,
                                    totalSavings = (totalSavings ?: 0L).toInt(),
                                    totalLoan = (totalLoan ?: 0L).toInt()
                                )
                                openPdf(localContext, file)
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("Open PDF") }

                        Button(
                            onClick = {
                                val file = generateDashboardPdfReport(
                                    context = localContext,
                                    members = members,
                                    totalSavings = (totalSavings ?: 0L).toInt(),
                                    totalLoan = (totalLoan ?: 0L).toInt()
                                )
                                sharePdf(localContext, file)
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("Share PDF") }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.titleLarge)
        }
    }
}
