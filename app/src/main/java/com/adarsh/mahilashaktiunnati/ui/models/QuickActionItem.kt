package com.adarsh.mahilashaktiunnati.ui.models

import androidx.compose.ui.graphics.Color

data class QuickActionItem(
    val title: String,
    val icon: String,
    val description: String,
    val color: Color,
    val onClick: () -> Unit
)
