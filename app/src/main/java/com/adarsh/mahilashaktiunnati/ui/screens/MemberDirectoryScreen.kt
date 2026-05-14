package com.adarsh.mahilashaktiunnati.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.adarsh.mahilashaktiunnati.R
import com.adarsh.mahilashaktiunnati.ui.theme.DesignSystem
import com.adarsh.mahilashaktiunnati.ui.theme.Gradients
import com.adarsh.mahilashaktiunnati.ui.theme.ComponentStyles
import com.adarsh.mahilashaktiunnati.viewmodel.EnhancedMemberViewModel
import com.adarsh.mahilashaktiunnati.data.entities.Member
import com.adarsh.mahilashaktiunnati.utils.LanguageManager
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
    
    // Background gradient
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
            // Header with total savings
            DirectoryHeader(
                totalSavings = uiState.totalSavings,
                activeMembersCount = uiState.members.size,
                activeLoansCount = uiState.activeLoansCount,
                onBack = onBack,
                onAddMember = onAddMember
            )
            
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.md))
            
            // Language Selector
            LanguageSelector(
                context = context,
                onLanguageChanged = onLanguageChanged
            )
            
            // Search bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { 
                    searchQuery = it
                    viewModel.searchMembers(it)
                },
                placeholder = stringResource(R.string.search_members_hint)
            )
            
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.md))
            
            // Member list
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = DesignSystem.Colors.Primary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            } else if (uiState.members.isEmpty()) {
                EmptyState(
                    message = if (searchQuery.isBlank()) 
                        stringResource(R.string.no_members_found)
                    else 
                        stringResource(R.string.no_members_found_for_query, searchQuery)
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
        
        // Error message
        uiState.error?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DesignSystem.Padding.screenHorizontal)
                    .align(Alignment.BottomCenter)
                    .shadow(
                        elevation = 8.dp,
                        shape = DesignSystem.Shapes.Large,
                        ambientColor = DesignSystem.Colors.Error.copy(alpha = 0.2f),
                        spotColor = DesignSystem.Colors.Error.copy(alpha = 0.4f)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = DesignSystem.Colors.Error.copy(alpha = 0.9f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(DesignSystem.Padding.sectionHorizontal),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = error,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    IconButton(
                        onClick = { viewModel.clearError() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
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
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = DesignSystem.Shapes.Large,
                ambientColor = Color.Black.copy(alpha = 0.25f),
                spotColor = Color.Black.copy(alpha = 0.5f)
            ),
        colors = ComponentStyles.getCardColors(),
        elevation = ComponentStyles.getCardElevation()
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = DesignSystem.Padding.cardHorizontal,
                vertical = DesignSystem.Padding.cardVertical
            )
        ) {
            // Top row with back button and title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(DesignSystem.Shapes.Medium)
                        .background(DesignSystem.Colors.Primary.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = DesignSystem.Colors.Primary
                    )
                }
                
                Text(
                    text = "Member Directory",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = DesignSystem.Colors.TextPrimary
                )
                
                IconButton(
                    onClick = onAddMember,
                    modifier = Modifier
                        .clip(DesignSystem.Shapes.Medium)
                        .background(DesignSystem.Colors.Success.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Member",
                        tint = DesignSystem.Colors.Success
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.md))
            
            // Statistics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    title = "Total Savings",
                    value = "₹${totalSavings}",
                    icon = Icons.Default.Savings,
                    color = DesignSystem.Colors.Success
                )
                
                StatCard(
                    title = "Active Members",
                    value = activeMembersCount.toString(),
                    icon = Icons.Default.People,
                    color = DesignSystem.Colors.Info
                )
                
                StatCard(
                    title = "Active Loans",
                    value = activeLoansCount.toString(),
                    icon = Icons.Default.AccountBalance,
                    color = DesignSystem.Colors.Warning
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.xs)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = color
        )
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = DesignSystem.Colors.TextSecondary
        )
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder, color = DesignSystem.Colors.TextHint) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = DesignSystem.Colors.TextHint
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = DesignSystem.Colors.TextHint
                    )
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = DesignSystem.Shapes.Medium,
        colors = ComponentStyles.getInputFieldColors()
    )
}

@Composable
private fun MemberListItem(
    member: Member,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = DesignSystem.Shapes.Large,
                ambientColor = Color.Black.copy(alpha = 0.15f),
                spotColor = Color.Black.copy(alpha = 0.3f)
            )
            .clickable { onClick() },
        colors = ComponentStyles.getCardColors(),
        elevation = ComponentStyles.getCardElevation()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignSystem.Padding.cardHorizontal),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)
        ) {
            // Profile photo
            if (member.photoUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(member.photoUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile photo",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Brush.horizontalGradient(Gradients.Primary)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = member.name.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
            }
            
            // Member info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.xs)
            ) {
                Text(
                    text = member.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = DesignSystem.Colors.TextPrimary
                )
                
                Text(
                    text = member.phone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DesignSystem.Colors.TextSecondary
                )
                
                Text(
                    text = "Joined: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(member.joinDate))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = DesignSystem.Colors.TextHint
                )
            }
            
            // Arrow icon
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "View details",
                tint = DesignSystem.Colors.TextHint
            )
        }
    }
}

@Composable
private fun EmptyState(
    message: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(DesignSystem.Padding.screenHorizontal),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = DesignSystem.Colors.TextHint,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(DesignSystem.Spacing.md))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = DesignSystem.Colors.TextHint,
            textAlign = TextAlign.Center
        )
    }
}
