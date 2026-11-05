package com.truthlens.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = Brand500,
    primaryVariant = Brand600,
    secondary = Accent500,
    secondaryVariant = Accent600,
    background = Background,
    surface = Surface,
    error = Danger500,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Slate300,
    onSurface = Slate300,
    onError = Color.White
)

@Composable
fun TruthLensTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DarkColorPalette,
        content = content
    )
}