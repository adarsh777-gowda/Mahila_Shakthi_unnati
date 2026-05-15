package com.adarsh.mahilashaktiunnati.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adarsh.mahilashaktiunnati.R
import com.adarsh.mahilashaktiunnati.ui.components.LanguageSelector
import com.adarsh.mahilashaktiunnati.ui.models.QuickActionItem
import com.adarsh.mahilashaktiunnati.ui.theme.ComponentStyles
import com.adarsh.mahilashaktiunnati.ui.theme.DesignSystem
import com.adarsh.mahilashaktiunnati.ui.theme.Gradients
import com.adarsh.mahilashaktiunnati.viewmodel.MemberViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalHomeScreen(
    context: android.content.Context,
    viewModel: MemberViewModel,
    onNavigateToMembers: () -> Unit,
    onNavigateToSavings: () -> Unit,
    onNavigateToLoans: () -> Unit,
    onNavigateToAI: () -> Unit,
    onNavigateToPracticalFeatures: () -> Unit,
    onNavigateToAdvancedFeatures: () -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onLogout: () -> Unit,
    onLanguageChanged: () -> Unit = {}
) {
    val members by viewModel.members.collectAsState()
    val totalSavings by viewModel.totalSavings.collectAsState()
    val totalLoan by viewModel.totalLoan.collectAsState()
    
    // Background gradient
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(Gradients.Background))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = DesignSystem.Padding.screenHorizontal,
                    vertical = DesignSystem.Padding.screenVertical
                ),
            verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.lg)
        ) {
            // Language Selector
            item {
                LanguageSelector(
                    context = context,
                    onLanguageChanged = onLanguageChanged
                )
            }
            
            // Header Section with Shadow
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(DesignSystem.Shapes.Large)
                        .shadow(
                            elevation = 12.dp,
                            shape = DesignSystem.Shapes.Large,
                            ambientColor = Color.Black.copy(alpha = 0.25f),
                            spotColor = Color.Black.copy(alpha = 0.5f)
                        ),
                    colors = ComponentStyles.getCardColors(),
                    elevation = ComponentStyles.getCardElevation()
                ) {
                    HomeHeader(onLogout = onLogout)
                }
            }
            
            // Stats Cards Section
            item {
                StatsCardsSection(
                    membersCount = members.size,
                    totalSavings = (totalSavings ?: 0).toLong(),
                    totalLoans = (totalLoan ?: 0).toLong()
                )
            }
            
            // Quick Actions Section with Shadow
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(DesignSystem.Shapes.Large)
                        .shadow(
                            elevation = 8.dp,
                            shape = DesignSystem.Shapes.Large,
                            ambientColor = Color.Black.copy(alpha = 0.2f),
                            spotColor = Color.Black.copy(alpha = 0.4f)
                        ),
                    colors = ComponentStyles.getCardColors(),
                    elevation = ComponentStyles.getCardElevation()
                ) {
                    QuickActionsSection(
                        onNavigateToMembers = onNavigateToMembers,
                        onNavigateToSavings = onNavigateToSavings,
                        onNavigateToLoans = onNavigateToLoans,
                        onNavigateToAI = onNavigateToAI,
                        onNavigateToPracticalFeatures = onNavigateToPracticalFeatures,
                        onNavigateToAdvancedFeatures = onNavigateToAdvancedFeatures,
                        onNavigateToReport = onNavigateToReport,
                        onNavigateToHelp = onNavigateToHelp
                    )
                }
            }
            
            // Recent Activity Section with Shadow
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(DesignSystem.Shapes.Large)
                        .shadow(
                            elevation = 8.dp,
                            shape = DesignSystem.Shapes.Large,
                            ambientColor = Color.Black.copy(alpha = 0.2f),
                            spotColor = Color.Black.copy(alpha = 0.4f)
                        ),
                    colors = ComponentStyles.getCardColors(),
                    elevation = ComponentStyles.getCardElevation()
                ) {
                    RecentActivitySection()
                }
            }
            
            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(DesignSystem.Spacing.xxl))
            }
        }
    }
}

@Composable
private fun HomeHeader(onLogout: () -> Unit) {
    Column(
        modifier = Modifier.padding(DesignSystem.Padding.cardHorizontal, DesignSystem.Padding.cardVertical)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.welcome_admin),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = DesignSystem.Colors.TextPrimary
                )
                
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    color = DesignSystem.Colors.TextSecondary
                )
            }
            
            IconButton(
                onClick = onLogout,
                modifier = Modifier
                    .size(40.dp)
                    .clip(DesignSystem.Shapes.Circle)
                    .background(DesignSystem.Colors.Error)
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = stringResource(R.string.logout),
                    tint = DesignSystem.Colors.OnPrimary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(DesignSystem.Spacing.md))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(DesignSystem.Shapes.Medium),
            colors = CardDefaults.cardColors(
                containerColor = DesignSystem.Colors.Primary.copy(alpha = 0.1f)
            )
        ) {
            Text(
                text = stringResource(R.string.welcome_message),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = DesignSystem.Colors.Primary,
                modifier = Modifier.padding(12.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun StatsCardsSection(
    membersCount: Int,
    totalSavings: Long,
    totalLoans: Long
) {
    val stats = listOf(
        StatItem(
            icon = Icons.Default.People,
            title = stringResource(R.string.members),
            value = membersCount.toString(),
            color = DesignSystem.Colors.Info,
            background = DesignSystem.Colors.Info.copy(alpha = 0.1f)
        ),
        StatItem(
            icon = Icons.Default.Savings,
            title = stringResource(R.string.savings),
            value = stringResource(R.string.currency_format, totalSavings),
            color = DesignSystem.Colors.Success,
            background = DesignSystem.Colors.Success.copy(alpha = 0.1f)
        ),
        StatItem(
            icon = Icons.Default.AccountBalance,
            title = stringResource(R.string.loans),
            value = totalLoans.toString(),
            color = DesignSystem.Colors.Warning,
            background = DesignSystem.Colors.Warning.copy(alpha = 0.1f)
        )
    )
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md),
        contentPadding = PaddingValues(horizontal = DesignSystem.Spacing.sm)
    ) {
        items(stats) { stat ->
            StatCard(stat = stat)
        }
    }
}

@Composable
private fun StatCard(stat: StatItem) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .clip(DesignSystem.Shapes.Medium)
            .shadow(
                elevation = 4.dp,
                shape = DesignSystem.Shapes.Medium,
                ambientColor = stat.color.copy(alpha = 0.2f),
                spotColor = stat.color.copy(alpha = 0.4f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = stat.background
        ),
        elevation = CardDefaults.cardElevation(DesignSystem.Elevation.Small)
    ) {
        Column(
            modifier = Modifier.padding(DesignSystem.Padding.itemHorizontal, DesignSystem.Padding.itemVertical),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.sm)
        ) {
            Icon(
                imageVector = stat.icon,
                contentDescription = stat.title,
                tint = stat.color,
                modifier = Modifier.size(DesignSystem.Icons.Medium)
            )
            
            Text(
                text = stat.value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = stat.color,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = stat.title,
                style = MaterialTheme.typography.bodySmall,
                color = DesignSystem.Colors.TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun QuickActionsSection(
    onNavigateToMembers: () -> Unit,
    onNavigateToSavings: () -> Unit,
    onNavigateToLoans: () -> Unit,
    onNavigateToAI: () -> Unit,
    onNavigateToPracticalFeatures: () -> Unit,
    onNavigateToAdvancedFeatures: () -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToHelp: () -> Unit
) {
    val quickActions = listOf(
        QuickActionItem(
            title = stringResource(R.string.members),
            icon = "👥",
            description = stringResource(R.string.members_description),
            color = DesignSystem.Colors.Info,
            onClick = onNavigateToMembers
        ),
        QuickActionItem(
            title = stringResource(R.string.savings),
            icon = "💰",
            description = stringResource(R.string.savings_description),
            color = DesignSystem.Colors.Success,
            onClick = onNavigateToSavings
        ),
        QuickActionItem(
            title = stringResource(R.string.loans),
            icon = "🏦",
            description = stringResource(R.string.loans_description),
            color = DesignSystem.Colors.Warning,
            onClick = onNavigateToLoans
        ),
        QuickActionItem(
            title = stringResource(R.string.ai_assistant),
            icon = "🤖",
            description = stringResource(R.string.ai_description),
            color = DesignSystem.Colors.Primary,
            onClick = onNavigateToAI
        ),
        QuickActionItem(
            title = stringResource(R.string.practical_features),
            icon = "🛠️",
            description = stringResource(R.string.essential_tools_description),
            color = DesignSystem.Colors.Secondary,
            onClick = onNavigateToPracticalFeatures
        ),
        QuickActionItem(
            title = stringResource(R.string.advanced_features),
            icon = "🚀",
            description = stringResource(R.string.production_tools_description),
            color = DesignSystem.Colors.Tertiary,
            onClick = onNavigateToAdvancedFeatures
        ),
        QuickActionItem(
            title = stringResource(R.string.reports),
            icon = "📊",
            description = stringResource(R.string.reports_description),
            color = DesignSystem.Colors.Secondary,
            onClick = onNavigateToReport
        ),
        QuickActionItem(
            title = stringResource(R.string.help),
            icon = "❓",
            description = stringResource(R.string.help_description),
            color = DesignSystem.Colors.Tertiary,
            onClick = onNavigateToHelp
        )
    )

    Column(
        modifier = Modifier.padding(DesignSystem.Grid.Padding),
        verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)
    ) {
        Text(
            text = stringResource(R.string.quick_actions),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = DesignSystem.Colors.TextPrimary
        )

        // Use Column/Row instead of LazyVerticalGrid to avoid nested scroll issues in LazyColumn
        quickActions.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DesignSystem.Grid.Spacing)
            ) {
                rowItems.forEach { action ->
                    Box(modifier = Modifier.weight(1f)) {
                        QuickActionCard(action = action)
                    }
                }
                if (rowItems.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(action: QuickActionItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(DesignSystem.Shapes.Large)
            .shadow(
                elevation = 6.dp,
                shape = DesignSystem.Shapes.Large,
                ambientColor = Color.Black.copy(alpha = 0.15f),
                spotColor = Color.Black.copy(alpha = 0.3f)
            )
            .clickable { action.onClick() },
        colors = ComponentStyles.getCardColors(),
        elevation = ComponentStyles.getCardElevation()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.sm)
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(DesignSystem.Shapes.Medium)
                    .background(action.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = action.icon,
                    fontSize = 24.sp,
                    color = action.color
                )
            }
            
            // Title
            Text(
                text = action.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = DesignSystem.Colors.TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            // Description
            Text(
                text = action.description,
                style = MaterialTheme.typography.bodySmall,
                color = DesignSystem.Colors.TextSecondary,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
fun RecentActivitySection() {
    val newSavingsTitle = stringResource(R.string.new_savings_activity)
    val newSavingsDescription = stringResource(R.string.new_savings_description)
    val newMemberTitle = stringResource(R.string.new_member_activity)
    val newMemberDescription = stringResource(R.string.new_member_description)
    val loanApprovalTitle = stringResource(R.string.loan_approval_activity)
    val loanApprovalDescription = stringResource(R.string.loan_approval_description)
    val reportGenerationTitle = stringResource(R.string.report_generation_activity)
    val reportGenerationDescription = stringResource(R.string.report_generation_description)
    val twoHoursAgo = stringResource(R.string.two_hours_ago)
    val fiveHoursAgo = stringResource(R.string.five_hours_ago)
    val oneDayAgo = stringResource(R.string.one_day_ago)
    val twoDaysAgo = stringResource(R.string.two_days_ago)
    val activities = remember(
        newSavingsTitle,
        newSavingsDescription,
        newMemberTitle,
        newMemberDescription,
        loanApprovalTitle,
        loanApprovalDescription,
        reportGenerationTitle,
        reportGenerationDescription,
        twoHoursAgo,
        fiveHoursAgo,
        oneDayAgo,
        twoDaysAgo
    ) {
        buildRecentActivities(
            newSavingsTitle = newSavingsTitle,
            newSavingsDescription = newSavingsDescription,
            newMemberTitle = newMemberTitle,
            newMemberDescription = newMemberDescription,
            loanApprovalTitle = loanApprovalTitle,
            loanApprovalDescription = loanApprovalDescription,
            reportGenerationTitle = reportGenerationTitle,
            reportGenerationDescription = reportGenerationDescription,
            twoHoursAgo = twoHoursAgo,
            fiveHoursAgo = fiveHoursAgo,
            oneDayAgo = oneDayAgo,
            twoDaysAgo = twoDaysAgo
        )
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)
    ) {
        Text(
            text = stringResource(R.string.recent_activities),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = DesignSystem.Colors.TextPrimary
        )
        
        activities.forEach { activity ->
            RecentActivityCard(activity = activity)
            if (activity != activities.last()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = DesignSystem.Colors.Border
                )
            }
        }
    }
}

@Composable
fun RecentActivityCard(activity: RecentActivity) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(DesignSystem.Shapes.Medium)
                .background(activity.color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = activity.icon,
                fontSize = 20.sp,
                color = activity.color
            )
        }
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = activity.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = DesignSystem.Colors.TextPrimary
            )
            
            Text(
                text = activity.description,
                style = MaterialTheme.typography.bodySmall,
                color = DesignSystem.Colors.TextSecondary
            )
        }
        
        Text(
            text = activity.time,
            style = MaterialTheme.typography.bodySmall,
            color = DesignSystem.Colors.TextHint
        )
    }
}

// Data classes
data class StatItem(
    val icon: ImageVector,
    val title: String,
    val value: String,
    val color: Color,
    val background: Color
)

data class RecentActivity(
    val icon: String,
    val title: String,
    val description: String,
    val time: String,
    val color: Color
)

fun buildRecentActivities(
    newSavingsTitle: String,
    newSavingsDescription: String,
    newMemberTitle: String,
    newMemberDescription: String,
    loanApprovalTitle: String,
    loanApprovalDescription: String,
    reportGenerationTitle: String,
    reportGenerationDescription: String,
    twoHoursAgo: String,
    fiveHoursAgo: String,
    oneDayAgo: String,
    twoDaysAgo: String
): List<RecentActivity> {
    return listOf(
        RecentActivity(
            icon = "💰",
            title = newSavingsTitle,
            description = newSavingsDescription,
            time = twoHoursAgo,
            color = DesignSystem.Colors.Success
        ),
        RecentActivity(
            icon = "👥",
            title = newMemberTitle,
            description = newMemberDescription,
            time = fiveHoursAgo,
            color = DesignSystem.Colors.Info
        ),
        RecentActivity(
            icon = "💳",
            title = loanApprovalTitle,
            description = loanApprovalDescription,
            time = oneDayAgo,
            color = DesignSystem.Colors.Warning
        ),
        RecentActivity(
            icon = "📊",
            title = reportGenerationTitle,
            description = reportGenerationDescription,
            time = twoDaysAgo,
            color = DesignSystem.Colors.Primary
        )
    )
}
