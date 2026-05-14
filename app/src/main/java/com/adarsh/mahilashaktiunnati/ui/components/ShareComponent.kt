package com.adarsh.mahilashaktiunnati.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CornerSize
import android.content.Intent
import com.adarsh.mahilashaktiunnati.ui.theme.DesignSystem
import com.adarsh.mahilashaktiunnati.ui.theme.Gradients
import com.adarsh.mahilashaktiunnati.ui.theme.ComponentStyles
import com.adarsh.mahilashaktiunnati.utils.ExportUtils
import com.adarsh.mahilashaktiunnati.data.relations.MemberWithSavingsAndLoans
import com.adarsh.mahilashaktiunnati.data.entities.Member
import com.adarsh.mahilashaktiunnati.data.entities.Savings
import com.adarsh.mahilashaktiunnati.data.entities.Loan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    member: Member? = null,
    memberWithSavingsAndLoans: MemberWithSavingsAndLoans? = null,
    allMembersData: List<MemberWithSavingsAndLoans>? = null
) {
    val context = LocalContext.current
    var selectedShareOption by remember { mutableStateOf(ShareOption.NONE) }
    
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            containerColor = DesignSystem.Colors.Surface,
            shape = DesignSystem.Shapes.ExtraLarge.copy(
                bottomStart = CornerSize(0.dp),
                bottomEnd = CornerSize(0.dp)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = DesignSystem.Padding.screenHorizontal,
                        vertical = DesignSystem.Padding.cardVertical
                    ),
                verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Share Report",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = DesignSystem.Colors.TextPrimary
                    )
                    
                    IconButton(
                        onClick = onDismiss
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = DesignSystem.Colors.TextSecondary
                        )
                    }
                }
                
                // Share options
                ShareOptionsGrid(
                    member = member,
                    memberWithSavingsAndLoans = memberWithSavingsAndLoans,
                    allMembersData = allMembersData,
                    onShareSelected = { option ->
                        selectedShareOption = option
                        handleShareOption(
                            context = context,
                            option = option,
                            member = member,
                            memberWithSavingsAndLoans = memberWithSavingsAndLoans,
                            allMembersData = allMembersData
                        )
                        onDismiss()
                    }
                )
                
                // Share button
                Button(
                    onClick = {
                        // Handle default share
                        handleDefaultShare(
                            context = context,
                            member = member,
                            memberWithSavingsAndLoans = memberWithSavingsAndLoans,
                            allMembersData = allMembersData
                        )
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(DesignSystem.Buttons.Height),
                    colors = ComponentStyles.getPrimaryButtonColors(),
                    shape = DesignSystem.Shapes.Medium
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(DesignSystem.Spacing.sm))
                    Text(
                        text = "Share to WhatsApp",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ShareOptionsGrid(
    member: Member?,
    memberWithSavingsAndLoans: MemberWithSavingsAndLoans?,
    allMembersData: List<MemberWithSavingsAndLoans>?,
    onShareSelected: (ShareOption) -> Unit
) {
    val shareOptions = getAvailableShareOptions(member, memberWithSavingsAndLoans, allMembersData)
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md),
        verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.sm),
        contentPadding = PaddingValues(vertical = DesignSystem.Spacing.sm)
    ) {
        items(shareOptions) { option ->
            ShareOptionCard(
                option = option,
                onClick = { onShareSelected(option) }
            )
        }
    }
}

@Composable
private fun ShareOptionCard(
    option: ShareOption,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = DesignSystem.Shapes.Medium,
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.2f)
            )
            .clickable { onClick() },
        colors = ComponentStyles.getCardColors(),
        elevation = ComponentStyles.getCardElevation()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignSystem.Padding.itemVertical),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.sm)
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = option.title,
                tint = option.color,
                modifier = Modifier.size(32.dp)
            )
            
            Text(
                text = option.title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = DesignSystem.Colors.TextPrimary,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = option.description,
                style = MaterialTheme.typography.bodySmall,
                color = DesignSystem.Colors.TextSecondary,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

private fun handleShareOption(
    context: android.content.Context,
    option: ShareOption,
    member: Member?,
    memberWithSavingsAndLoans: MemberWithSavingsAndLoans?,
    allMembersData: List<MemberWithSavingsAndLoans>?
) {
    val shareText = when (option) {
        ShareOption.MEMBER_SUMMARY -> {
            memberWithSavingsAndLoans?.let {
                ExportUtils.formatMemberSummary(
                    member = it.member,
                    savings = it.savings,
                    loans = it.loans
                )
            } ?: return
        }
        ShareOption.WEEKLY_REPORT -> {
            memberWithSavingsAndLoans?.let {
                val weekStart = java.util.Calendar.getInstance().apply {
                    add(java.util.Calendar.DAY_OF_MONTH, -7)
                }.time
                val weekEnd = java.util.Date()
                
                ExportUtils.formatWeeklyReport(
                    member = it.member,
                    weekSavings = it.savings.filter { saving ->
                        saving.date >= weekStart.time && saving.date <= weekEnd.time
                    },
                    weekStartDate = weekStart,
                    weekEndDate = weekEnd
                )
            } ?: return
        }
        ShareOption.GROUP_SUMMARY -> {
            allMembersData?.let {
                ExportUtils.formatGroupSummary(it)
            } ?: return
        }
        ShareOption.PDF_EXPORT -> {
            // PDF export would require additional implementation
            // For now, fall back to text export
            memberWithSavingsAndLoans?.let {
                ExportUtils.formatMemberSummary(
                    member = it.member,
                    savings = it.savings,
                    loans = it.loans
                )
            } ?: return
        }
        else -> return
    }
    
    val intent = ExportUtils.createWhatsAppShareIntent(context, shareText)
    context.startActivity(Intent.createChooser(intent, "Share via"))
}

private fun handleDefaultShare(
    context: android.content.Context,
    member: Member?,
    memberWithSavingsAndLoans: MemberWithSavingsAndLoans?,
    allMembersData: List<MemberWithSavingsAndLoans>?
) {
    val shareText = if (memberWithSavingsAndLoans != null) {
        ExportUtils.formatMemberSummary(
            member = memberWithSavingsAndLoans.member,
            savings = memberWithSavingsAndLoans.savings,
            loans = memberWithSavingsAndLoans.loans
        )
    } else if (allMembersData != null) {
        ExportUtils.formatGroupSummary(allMembersData)
    } else {
        return
    }
    
    val intent = ExportUtils.createWhatsAppShareIntent(context, shareText)
    context.startActivity(Intent.createChooser(intent, "Share via"))
}

private fun getAvailableShareOptions(
    member: Member?,
    memberWithSavingsAndLoans: MemberWithSavingsAndLoans?,
    allMembersData: List<MemberWithSavingsAndLoans>?
): List<ShareOption> {
    val options = mutableListOf<ShareOption>()
    
    if (memberWithSavingsAndLoans != null) {
        options.add(ShareOption.MEMBER_SUMMARY)
        options.add(ShareOption.WEEKLY_REPORT)
    }
    
    if (allMembersData != null && allMembersData.isNotEmpty()) {
        options.add(ShareOption.GROUP_SUMMARY)
    }
    
    // PDF export option (placeholder for future implementation)
    if (memberWithSavingsAndLoans != null) {
        options.add(ShareOption.PDF_EXPORT)
    }
    
    return options
}

enum class ShareOption(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
) {
    MEMBER_SUMMARY(
        title = "Member Summary",
        description = "Complete member details",
        icon = Icons.Default.Person,
        color = DesignSystem.Colors.Primary
    ),
    WEEKLY_REPORT(
        title = "Weekly Report",
        description = "Last 7 days activity",
        icon = Icons.Default.DateRange,
        color = DesignSystem.Colors.Success
    ),
    GROUP_SUMMARY(
        title = "Group Summary",
        description = "All members overview",
        icon = Icons.Default.Groups,
        color = DesignSystem.Colors.Info
    ),
    PDF_EXPORT(
        title = "PDF Export",
        description = "Download as PDF",
        icon = Icons.Default.PictureAsPdf,
        color = DesignSystem.Colors.Warning
    ),
    NONE(
        title = "",
        description = "",
        icon = Icons.Default.Info,
        color = DesignSystem.Colors.TextSecondary
    )
}

@Composable
fun QuickShareButton(
    member: Member,
    savingsEntries: List<Savings>,
    loans: List<Loan>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val shareText = ExportUtils.formatMemberSummary(member, savingsEntries, loans)
    
    IconButton(
        onClick = {
            val intent = ExportUtils.createWhatsAppShareIntent(context, shareText)
            context.startActivity(Intent.createChooser(intent, "Share via"))
        },
        modifier = modifier
            .clip(DesignSystem.Shapes.Medium)
            .background(DesignSystem.Colors.Primary.copy(alpha = 0.1f))
            .padding(DesignSystem.Spacing.sm)
    ) {
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Share",
            tint = DesignSystem.Colors.Primary
        )
    }
}

@Composable
fun FloatingShareButton(
    member: Member,
    savingsEntries: List<Savings>,
    loans: List<Loan>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val shareText = ExportUtils.formatMemberSummary(member, savingsEntries, loans)
    
    FloatingActionButton(
        onClick = {
            val intent = ExportUtils.createWhatsAppShareIntent(context, shareText)
            context.startActivity(Intent.createChooser(intent, "Share via"))
        },
        modifier = modifier.shadow(
            elevation = 8.dp,
            shape = DesignSystem.Shapes.Large,
            ambientColor = DesignSystem.Colors.Primary.copy(alpha = 0.3f),
            spotColor = DesignSystem.Colors.Primary.copy(alpha = 0.5f)
        ),
        containerColor = DesignSystem.Colors.Primary
    ) {
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Share",
            tint = DesignSystem.Colors.OnPrimary
        )
    }
}
