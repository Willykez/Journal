package com.forexjournal.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily

// Using the system monospace family — avoids bundling a custom font file
// (no network access was available to fetch JetBrains Mono at build time
// for this project). Swap in a bundled font later via res/font if desired.
val MonoFont = FontFamily.Monospace

private val AppDarkColorScheme = darkColorScheme(
    primary = AppTeal,
    onPrimary = AppBg,
    secondary = AppGreen,
    background = AppBg,
    surface = AppPanel,
    surfaceVariant = AppPanelElevated,
    onBackground = AppText,
    onSurface = AppText,
    onSurfaceVariant = AppMuted,
    outline = AppBorder,
    error = AppRed
)

@Composable
fun ForexTradeAnalystTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppDarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
