package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EtfcGold,
    onPrimary = Color.Black,
    primaryContainer = EtfcGoldDark,
    onPrimaryContainer = Color.White,
    secondary = EtfcRedPrimary,
    onSecondary = Color.White,
    secondaryContainer = EtfcRedDark,
    onSecondaryContainer = Color.White,
    tertiary = EtfcBlueCorner,
    onTertiary = Color.Black,
    background = EtfcObsidian,
    onBackground = EtfcTextPrimary,
    surface = EtfcSurface,
    onSurface = EtfcTextPrimary,
    surfaceVariant = EtfcSurfaceVariant,
    onSurfaceVariant = EtfcTextSecondary,
    outline = EtfcBorder,
    error = EtfcLiveRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    // Combat sports streaming always delivers the best immersive stadium experience in high-contrast Dark Mode
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
