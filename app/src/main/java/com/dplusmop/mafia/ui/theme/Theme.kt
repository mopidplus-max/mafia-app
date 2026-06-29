package com.dplusmop.mafia.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Тёмная, кинематографичная палитра в духе ночного города и мафии
val MafiaRed = Color(0xFFE53935)
val MafiaRedDark = Color(0xFF8B0000)
val MafiaGold = Color(0xFFD4AF37)
val MafiaBackground = Color(0xFF0E0B14)
val MafiaSurface = Color(0xFF1A1625)
val MafiaSurfaceVariant = Color(0xFF241F33)
val MafiaOnSurface = Color(0xFFF3F1F7)
val MafiaOnSurfaceMuted = Color(0xFFA89FBD)
val MafiaGreen = Color(0xFF22C55E)
val MafiaBlue = Color(0xFF3B82F6)

private val MafiaColorScheme = darkColorScheme(
    primary = MafiaRed,
    onPrimary = Color.White,
    secondary = MafiaGold,
    onSecondary = Color(0xFF1A1300),
    background = MafiaBackground,
    onBackground = MafiaOnSurface,
    surface = MafiaSurface,
    onSurface = MafiaOnSurface,
    surfaceVariant = MafiaSurfaceVariant,
    onSurfaceVariant = MafiaOnSurfaceMuted,
    error = Color(0xFFFF6B6B),
    outline = Color(0xFF3A3450),
)

@Composable
fun MafiaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MafiaColorScheme,
        typography = MafiaTypography,
        content = content
    )
}
