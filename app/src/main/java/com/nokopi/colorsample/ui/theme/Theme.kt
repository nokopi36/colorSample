package com.nokopi.colorsample.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Buff400,
    onPrimary = Color.White,
    primaryContainer = Buff100,
    onPrimaryContainer = Brown900,
    secondary = Brown700,
    onSecondary = Color.White,
    secondaryContainer = BuffLight,
    onSecondaryContainer = Brown900,
    background = NeutralLightSurface,
    onBackground = Brown900,
    surface = NeutralLightSurface,
    onSurface = Brown900,
    surfaceVariant = NeutralLightSurfaceVariant,
    onSurfaceVariant = Brown700,
    outline = OutlineLight,
    outlineVariant = Buff200,
)

private val DarkColors = darkColorScheme(
    primary = BuffDarkPrimary,
    onPrimary = OnBuffDark,
    primaryContainer = Brown700,
    onPrimaryContainer = BuffLight,
    secondary = Buff200,
    onSecondary = OnBuffDark,
    secondaryContainer = Brown700,
    onSecondaryContainer = BuffLight,
    background = NeutralDarkSurface,
    onBackground = NeutralDarkOnSurface,
    surface = NeutralDarkSurface,
    onSurface = NeutralDarkOnSurface,
    surfaceVariant = NeutralDarkSurfaceVariant,
    onSurfaceVariant = NeutralDarkOnSurfaceVariant,
    outline = OutlineDark,
    outlineVariant = NeutralDarkSurfaceVariant,
)

@Composable
fun ColorSampleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = ColorSampleTypography,
        content = content,
    )
}
