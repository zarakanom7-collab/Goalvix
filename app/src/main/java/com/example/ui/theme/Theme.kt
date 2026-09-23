package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GoalvixDarkColorScheme = darkColorScheme(
    primary = GoalvixGreenNeon,
    onPrimary = Color.Black,
    primaryContainer = GoalvixGreenDark,
    onPrimaryContainer = Color.White,
    secondary = GoalvixGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = GoalvixGoldLight,
    background = DarkBg,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkCardBorder,
    error = AccentRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GoalvixDarkColorScheme,
        typography = Typography,
        content = content
    )
}
