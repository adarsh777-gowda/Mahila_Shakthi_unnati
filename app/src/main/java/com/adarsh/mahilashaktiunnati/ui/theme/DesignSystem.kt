package com.adarsh.mahilashaktiunnati.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Professional Design System
object DesignSystem {
    
    // Spacing System (8-point grid)
    object Spacing {
        val xs = 4.dp      // Extra Small
        val sm = 8.dp      // Small
        val md = 16.dp     // Medium
        val lg = 24.dp     // Large
        val xl = 32.dp     // Extra Large
        val xxl = 48.dp    // Extra Extra Large
        val xxxl = 64.dp   // Triple Extra Large
    }
    
    // Padding System
    object Padding {
        val screenHorizontal = 24.dp
        val screenVertical = 24.dp
        val cardHorizontal = 20.dp
        val cardVertical = 20.dp
        val buttonHorizontal = 24.dp
        val buttonVertical = 16.dp
        val sectionHorizontal = 16.dp
        val sectionVertical = 16.dp
        val itemHorizontal = 12.dp
        val itemVertical = 12.dp
    }
    
    // Color System
    object Colors {
        // Primary Colors
        val Primary = Color(0xFFE91E63)
        val PrimaryVariant = Color(0xFF9C27B0)
        val OnPrimary = Color(0xFFFFFFFF)
        
        // Secondary Colors
        val Secondary = Color(0xFF4CAF50)
        val SecondaryVariant = Color(0xFF388E3C)
        val Tertiary = Color(0xFF795548)
        val OnSecondary = Color(0xFFFFFFFF)
        
        // Surface Colors
        val Surface = Color(0xFFF8F9FA)
        val SurfaceVariant = Color(0xFFFFFFFF)
        val OnSurface = Color(0xFF1C1B1F)
        val OnSurfaceVariant = Color(0xFF49454F)
        
        // Background Colors
        val Background = Color(0xFFFCE4EC)
        val BackgroundVariant = Color(0xFFF3E5F5)
        
        // Status Colors
        val Success = Color(0xFF4CAF50)
        val Warning = Color(0xFFFF9800)
        val Error = Color(0xFFF44336)
        val Info = Color(0xFF2196F3)
        
        // Text Colors
        val TextPrimary = Color(0xFF212121)
        val TextSecondary = Color(0xFF757575)
        val TextDisabled = Color(0xFFBDBDBD)
        val TextHint = Color(0xFF9E9E9E)
        
        // Border Colors
        val Border = Color(0xFFE0E0E0)
        val BorderFocused = Color(0xFFE91E63)
        val BorderError = Color(0xFFF44336)
        
        // Card Colors
        val CardBackground = Color(0xFFFFFFFF)
        val CardBackgroundVariant = Color(0xFFF8F9FA)
        val CardShadow = Color(0x1F000000)
    }
    
    // Shape System
    object Shapes {
        val Small = RoundedCornerShape(8.dp)
        val Medium = RoundedCornerShape(12.dp)
        val Large = RoundedCornerShape(16.dp)
        val ExtraLarge = RoundedCornerShape(20.dp)
        val Circle = RoundedCornerShape(50)
        val Full = RoundedCornerShape(50)
    }
    
    // Elevation System
    object Elevation {
        val None = 0.dp
        val Small = 2.dp
        val Medium = 4.dp
        val Large = 8.dp
        val ExtraLarge = 12.dp
        val SuperLarge = 16.dp
    }
    
    // Shadow System - These are helper functions to create shadow modifiers
    object Shadows {
        // Shadow elevation values for different components
        val SmallElevation = 2.dp
        val MediumElevation = 4.dp
        val LargeElevation = 8.dp
        val ExtraLargeElevation = 12.dp
        val ColoredElevation = 6.dp
    }
    
    // Typography System
    object Typography {
        const val Heading1 = 32
        const val Heading2 = 28
        const val Heading3 = 24
        const val Heading4 = 20
        const val Heading5 = 18
        const val Heading6 = 16
        const val Body1 = 16
        const val Body2 = 14
        const val Caption = 12
        const val Overline = 10
    }
    
    // Button System
    object Buttons {
        val Height = 48.dp
        val HeightSmall = 36.dp
        val HeightLarge = 56.dp
        val CornerRadius = 12.dp
        val PaddingHorizontal = 24.dp
        val PaddingVertical = 16.dp
    }
    
    // Card System
    object Cards {
        val CornerRadius = 16.dp
        val PaddingHorizontal = 20.dp
        val PaddingVertical = 20.dp
        val Elevation = 8.dp
        val Spacing = 16.dp
    }
    
    // Input Field System
    object InputFields {
        val Height = 56.dp
        val CornerRadius = 12.dp
        val PaddingHorizontal = 16.dp
        val PaddingVertical = 16.dp
        val BorderWidth = 1.dp
        val FocusedBorderWidth = 2.dp
    }
    
    // Icon System
    object Icons {
        val Small = 16.dp
        val Medium = 24.dp
        val Large = 32.dp
        val ExtraLarge = 48.dp
    }
    
    // Grid System
    object Grid {
        val Columns = 2
        val Spacing = 12.dp
        val Padding = 16.dp
    }
    
    // List System
    object Lists {
        val ItemHeight = 72.dp
        val ItemPadding = 16.dp
        val ItemSpacing = 8.dp
        val SectionSpacing = 24.dp
    }
}

// Professional Gradients
object Gradients {
    val Primary = listOf(
        DesignSystem.Colors.Primary,
        DesignSystem.Colors.PrimaryVariant
    )
    
    val Background = listOf(
        DesignSystem.Colors.Background,
        DesignSystem.Colors.BackgroundVariant
    )
    
    val Success = listOf(
        DesignSystem.Colors.Success,
        DesignSystem.Colors.SecondaryVariant
    )
    
    val Error = listOf(
        DesignSystem.Colors.Error,
        Color(0xFFD32F2F)
    )
}

// Professional Component Styles - These are helper functions to create consistent styling
object ComponentStyles {
    
    // Card Styles
    @Composable
    fun getCardColors() = androidx.compose.material3.CardDefaults.cardColors(
        containerColor = DesignSystem.Colors.CardBackground,
        contentColor = DesignSystem.Colors.OnSurface
    )
    
    @Composable
    fun getCardElevation() = androidx.compose.material3.CardDefaults.cardElevation(
        defaultElevation = DesignSystem.Elevation.Medium
    )
    
    // Button Styles
    @Composable
    fun getPrimaryButtonColors() = androidx.compose.material3.ButtonDefaults.buttonColors(
        containerColor = DesignSystem.Colors.Primary,
        contentColor = DesignSystem.Colors.OnPrimary,
        disabledContainerColor = DesignSystem.Colors.TextDisabled,
        disabledContentColor = DesignSystem.Colors.TextDisabled
    )
    
    @Composable
    fun getPrimaryButtonElevation() = androidx.compose.material3.ButtonDefaults.buttonElevation(
        defaultElevation = DesignSystem.Elevation.Small,
        pressedElevation = DesignSystem.Elevation.Medium
    )
    
    @Composable
    fun getSecondaryButtonColors() = androidx.compose.material3.ButtonDefaults.buttonColors(
        containerColor = DesignSystem.Colors.Secondary,
        contentColor = DesignSystem.Colors.OnSecondary,
        disabledContainerColor = DesignSystem.Colors.TextDisabled,
        disabledContentColor = DesignSystem.Colors.TextDisabled
    )
    
    @Composable
    fun getOutlinedButtonColors() = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
        contentColor = DesignSystem.Colors.Primary,
        disabledContentColor = DesignSystem.Colors.TextDisabled
    )
    
    // Input Field Styles
    @Composable
    fun getInputFieldColors() = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
        focusedBorderColor = DesignSystem.Colors.BorderFocused,
        unfocusedBorderColor = DesignSystem.Colors.Border,
        errorBorderColor = DesignSystem.Colors.BorderError,
        focusedLabelColor = DesignSystem.Colors.Primary,
        cursorColor = DesignSystem.Colors.Primary,
        errorLabelColor = DesignSystem.Colors.Error,
        errorCursorColor = DesignSystem.Colors.Error
    )
}
