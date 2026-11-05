package com.truthlens.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Tendrils Design System - Visual Tokens
 * Inspired by natural growth, light, discovery, and calm digital environments.
 */

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

// CompositionLocals for global theme state access
val LocalThemeMode = compositionLocalOf { mutableStateOf(ThemeMode.SYSTEM) }
val LocalTendrilsColors = staticCompositionLocalOf { TendrilsDarkColors }
val LocalTendrilsSpacing = staticCompositionLocalOf { TendrilsSpacing() }

// --- COLOR TOKENS ---
object TendrilsPalette {
    // Sprout & Growth (Primary botanical tones)
    val Sprout50 = Color(0xFFF1F8F4)
    val Sprout100 = Color(0xFFDCEFE3)
    val Sprout300 = Color(0xFF90D0A9)
    val Sprout400 = Color(0xFF5EBA84)
    val Sprout500 = Color(0xFF389E66) // Core Tendril green
    val Sprout600 = Color(0xFF287E50)
    val Sprout900 = Color(0xFF133C26)

    // Warmth & Sunlight (Amber / Honey accent for discovery & curiosity)
    val Amber50 = Color(0xFFFFFBEB)
    val Amber100 = Color(0xFFFEF3C7)
    val Amber400 = Color(0xFFFBBF24)
    val Amber500 = Color(0xFFF59E0B)
    val Amber600 = Color(0xFFD97706)

    // Calm Sky & Atmosphere (Trustworthy calm cyan/teal)
    val Sky50 = Color(0xFFF0FDF4)
    val Sky400 = Color(0xFF38BDF8)
    val Sky500 = Color(0xFF0EA5E9)
    val Teal400 = Color(0xFF2DD4BF)
    val Teal600 = Color(0xFF0D9488)

    // Warm Earth & Slate Neutrals
    val Cream50 = Color(0xFFFAFAF7)   // Light mode main background
    val Cream100 = Color(0xFFF4F3EE)  // Light mode elevated surface
    val Cream200 = Color(0xFFE9E7DF)  // Light mode subtle border
    val Slate600 = Color(0xFF64748B)
    val Slate700 = Color(0xFF475569)
    val Slate800 = Color(0xFF1E293B)
    val Slate900 = Color(0xFF0F172A)
    val ForestNight = Color(0xFF0C1311) // Dark mode background (calm organic deep dark)
    val ForestSurface = Color(0xFF14211D) // Dark mode elevated card surface
    val ForestBorder = Color(0xFF23352F)  // Dark mode subtle border

    // Functional / State Colors
    val Coral500 = Color(0xFFF43F5E) // Error / alert
    val Coral100 = Color(0xFFFFE4E6)
    val Violet400 = Color(0xFFA78BFA) // Thinking state
    val Emerald400 = Color(0xFF34D399) // Success / completed state
    val Indigo400 = Color(0xFF818CF8) // Listening state
}

// Semantic Color Scheme
class TendrilsColors(
    val primary: Color,
    val primaryVariant: Color,
    val secondary: Color,
    val accent: Color,
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val error: Color,
    val isDark: Boolean
)

val TendrilsLightColors = TendrilsColors(
    primary = TendrilsPalette.Sprout600,
    primaryVariant = TendrilsPalette.Sprout500,
    secondary = TendrilsPalette.Amber500,
    accent = TendrilsPalette.Teal600,
    background = TendrilsPalette.Cream50,
    surface = Color.White,
    surfaceElevated = TendrilsPalette.Cream100,
    border = TendrilsPalette.Cream200,
    textPrimary = TendrilsPalette.Slate900,
    textSecondary = TendrilsPalette.Slate700,
    textMuted = TendrilsPalette.Slate600,
    error = TendrilsPalette.Coral500,
    isDark = false
)

val TendrilsDarkColors = TendrilsColors(
    primary = TendrilsPalette.Sprout400,
    primaryVariant = TendrilsPalette.Sprout300,
    secondary = TendrilsPalette.Amber400,
    accent = TendrilsPalette.Teal400,
    background = TendrilsPalette.ForestNight,
    surface = TendrilsPalette.ForestSurface,
    surfaceElevated = Color(0xFF1B2A25),
    border = TendrilsPalette.ForestBorder,
    textPrimary = Color(0xFFF1F5F4),
    textSecondary = Color(0xFFB5C9C1),
    textMuted = Color(0xFF7E978E),
    error = Color(0xFFFB7185),
    isDark = true
)

// --- SPACING TOKENS ---
data class TendrilsSpacing(
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 20.dp,
    val xxl: Dp = 24.dp,
    val xxxl: Dp = 32.dp,
    val giant: Dp = 48.dp
)

// --- SHAPE TOKENS (Organic & Rounded) ---
val TendrilsShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(26.dp)
)
val PillShape = RoundedCornerShape(percent = 50)

// --- TYPOGRAPHY TOKENS ---
val TendrilsTypography = Typography(
    h1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = (-0.5).sp
    ),
    h2 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        letterSpacing = (-0.2).sp
    ),
    h3 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    body2 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        letterSpacing = 0.2.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)

/**
 * Main Tendrils Application Theme Provider
 */
@Composable
fun TendrilsTheme(
    themeMode: ThemeMode = LocalThemeMode.current.value,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colors = if (isDark) TendrilsDarkColors else TendrilsLightColors
    val materialColors = if (isDark) {
        darkColors(
            primary = colors.primary,
            primaryVariant = colors.primaryVariant,
            secondary = colors.secondary,
            background = colors.background,
            surface = colors.surface,
            error = colors.error,
            onPrimary = Color(0xFF06180E),
            onSecondary = Color(0xFF1E1303),
            onBackground = colors.textPrimary,
            onSurface = colors.textPrimary,
            onError = Color.White
        )
    } else {
        lightColors(
            primary = colors.primary,
            primaryVariant = colors.primaryVariant,
            secondary = colors.secondary,
            background = colors.background,
            surface = colors.surface,
            error = colors.error,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = colors.textPrimary,
            onSurface = colors.textPrimary,
            onError = Color.White
        )
    }

    CompositionLocalProvider(
        LocalTendrilsColors provides colors,
        LocalTendrilsSpacing provides TendrilsSpacing()
    ) {
        MaterialTheme(
            colors = materialColors,
            typography = TendrilsTypography,
            shapes = TendrilsShapes,
            content = content
        )
    }
}

// Convenient accessors
object TendrilsTheme {
    val colors: TendrilsColors
        @Composable
        get() = LocalTendrilsColors.current

    val spacing: TendrilsSpacing
        @Composable
        get() = LocalTendrilsSpacing.current

    val typography: Typography
        @Composable
        get() = MaterialTheme.typography

    val shapes: Shapes
        @Composable
        get() = MaterialTheme.shapes
}

