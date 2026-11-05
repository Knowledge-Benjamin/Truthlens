package com.truthlens.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.truthlens.ui.theme.TendrilsShapes
import com.truthlens.ui.theme.TendrilsTheme

/**
 * Reusable organic card component for Tendrils.
 * Automatically adapts background and borders to light/dark themes.
 */
@Composable
fun TendrilsCard(
    modifier: Modifier = Modifier,
    shape: Shape = TendrilsShapes.medium,
    backgroundColor: Color = TendrilsTheme.colors.surface,
    borderColor: Color = TendrilsTheme.colors.border,
    borderWidth: Dp = 1.dp,
    padding: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(BorderStroke(borderWidth, borderColor), shape)
            .then(clickableModifier)
            .padding(padding),
        content = content
    )
}
