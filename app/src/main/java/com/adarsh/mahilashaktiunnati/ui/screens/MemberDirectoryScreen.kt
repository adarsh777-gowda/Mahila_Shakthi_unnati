package com.adarsh.mahilashaktiunnati.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.adarsh.mahilashaktiunnati.R
import com.adarsh.mahilashaktiunnati.ui.theme.DesignSystem
import com.adarsh.mahilashaktiunnati.ui.theme.Gradients
import com.adarsh.mahilashaktiunnati.ui.theme.ComponentStyles
import com.adarsh.mahilashaktiunnati.viewmodel.EnhancedMemberViewModel
import com.adarsh.mahilashaktiunnati.data.entities.Member
import com.adarsh.mahilashaktiunnati.ui.components.LanguageSelector
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberDirectoryScreen(
    context: android.content.Context,
    viewModel: EnhancedMemberViewModel,
    onMemberClick: (Int) -> Unit,
    onAddMember: () -> Unit,
    onBack: () -> Unit,
    onLanguageChanged: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(Gradients.Background))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = DesignSystem.Padding.screenHorizontal,
                    vertical = DesignSystem.Padding.screenVertical
                )
        ) {
            DirectoryHeader(
                totalSavings = uiState.totalSavings,
                activeMembersCount = uiState.members.size,
                activeLoansCount = uiState.activeLoansCount,
                onBack = onBack,
                onAddMember = onAddMember
            )
            
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.md))
            
            LanguageSelector(
                context = context,
                onLanguageChanged = onLanguageChanged
            )
            
            SearchBar(
                query = searchQuery,
                onQueryChange = { 
                    searchQuery = it
                    viewModel.searchMembers(it)
                },
                placeholder = stringResource(R.string.search_members_hint)
            )
            
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.md))
            
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = DesignSystem.Colors.Primary)
                }
            } else if (uiState.members.isEmpty()) {
                EmptyState(
                    message = if (searchQuery.isBlank()) 
                        stringResource(R.string.no_members_found)
                    else 
                        stringResource(R.string.no_members_found_for_query, searchQuery),
                    icon = Icons.Default.People
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.sm)
                ) {
                    items(uiState.members) { member ->
                        MemberListItem(
                            member = member,
                            onClick = { onMemberClick(member.id) }
                        )
                    }
                }
            }
        }
        
        uiState.error?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                colors = CardDefaults.cardColors(containerColor = DesignSystem.Colors.Error)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = error, color = Color.White)
                    IconButton(onClick = { viewModel.clearError() }) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close), tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun DirectoryHeader(
    totalSavings: Long,
    activeMembersCount: Int,
    activeLoansCount: Int,
    onBack: () -> Unit,
    onAddMember: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = ComponentStyles.getCardColors(),
        elevation = ComponentStyles.getCardElevation()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                }
                Text(stringResource(R.string.member_directory), style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = onAddMember) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatCard(stringResource(R.string.savings), "₹$totalSavings", Icons.Default.Savings, DesignSystem.Colors.Success)
                StatCard(stringResource(R.string.members), activeMembersCount.toString(), Icons.Default.People, DesignSystem.Colors.Info)
                StatCard(stringResource(R.string.loans), activeLoansCount.toString(), Icons.Default.AccountBalance, DesignSystem.Colors.Warning)
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = color)
        Text(value, style = MaterialTheme.typography.titleMedium, color = color)
        Text(title, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit, placeholder: String) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        shape = DesignSystem.Shapes.Medium
    )
}

@Composable
private fun MemberListItem(member: Member, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = ComponentStyles.getCardColors()
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = member.photoUri,
                contentDescription = stringResource(R.string.member_photo),
                modifier = Modifier.size(56.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(member.name, style = MaterialTheme.typography.titleMedium)
                Text(member.phone, style = MaterialTheme.typography.bodyMedium)
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
        }
    }
}

@Composable
private fun EmptyState(message: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
        Text(message, textAlign = TextAlign.Center, color = Color.Gray)
    }
}
