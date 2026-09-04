package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkBentoPrimary,
    onPrimary = Color(0xFF381E72),
    primaryContainer = DarkBentoPrimaryContainer,
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = Color(0xFFE8DEF8),
    tertiary = Color(0xFFEFB8C8),
    onTertiary = Color(0xFF492532),
    background = DarkBentoBackground,
    onBackground = Color(0xFFE6E0E9),
    surface = DarkBentoSurface,
    onSurface = Color(0xFFE6E0E9),
    surfaceVariant = DarkBentoSurfaceVariant,
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = DarkBentoBorder
)

private val LightColorScheme = lightColorScheme(
    primary = BentoPrimary,
    onPrimary = BentoOnPrimary,
    primaryContainer = BentoPrimaryContainer,
    onPrimaryContainer = BentoOnPrimaryContainer,
    secondary = Color(0xFF625B71),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = BentoSurfaceVariant,
    onSecondaryContainer = BentoTextPrimary,
    tertiary = BentoPinkOnAccent,
    tertiaryContainer = BentoPinkAccent,
    background = BentoBackground,
    onBackground = BentoTextPrimary,
    surface = BentoSurface,
    onSurface = BentoTextPrimary,
    surfaceVariant = BentoSurfaceVariant,
    onSurfaceVariant = BentoTextSecondary,
    outline = BentoBorder,
    outlineVariant = BentoBorderLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
