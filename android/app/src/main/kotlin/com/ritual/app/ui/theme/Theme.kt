package com.ritual.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RitualColorScheme = darkColorScheme(
    background = AppBackground,
    surface = CardBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    primary = TextPrimary,
    onPrimary = TextOnAccent,
)

@Composable
fun RitualTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RitualColorScheme,
        typography = RitualTypography,
        content = content,
    )
}
