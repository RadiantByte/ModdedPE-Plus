package com.mcal.mcpelauncher.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * @author <a href="https://github.com/RadiantByte">RadiantByte</a>
 */

private val DarkColorScheme = darkColorScheme(
    primary = ModdedColors.Primary,
    onPrimary = ModdedColors.OnPrimary,
    secondary = ModdedColors.Secondary,
    background = ModdedColors.Background,
    surface = ModdedColors.Surface,
    onSurface = ModdedColors.OnSurface,
    error = ModdedColors.Error
)

private val LightColorScheme = lightColorScheme(
    primary = ModdedColors.Primary,
    onPrimary = ModdedColors.OnPrimary,
    secondary = ModdedColors.Secondary,
    background = Color(0xFFF7FAFC),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0B0F14),
    error = ModdedColors.Error
)

@Composable
fun ModdedTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colors,
        typography = ModdedTypography,
        content = content
    )
}