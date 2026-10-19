package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.example.model.AppFontStyle

private val PsicoLightColorScheme = lightColorScheme(
    primary = PinePrimary,
    onPrimary = OnPinePrimary,
    primaryContainer = PinePrimaryContainer,
    onPrimaryContainer = PinePrimaryFixed,
    secondary = SageSecondary,
    onSecondary = OnSageSecondary,
    secondaryContainer = SageSecondaryContainer,
    onSecondaryContainer = OnSageSecondaryContainer,
    tertiary = PeachTertiary,
    onTertiary = OnPeachTertiary,
    tertiaryContainer = PeachTertiaryFixed,
    onTertiaryContainer = OnPeachTertiaryFixed,
    background = CanvasBackground,
    onBackground = TextOnSurface,
    surface = SurfaceCanvas,
    onSurface = TextOnSurface,
    surfaceVariant = SurfaceContainerHigh,
    onSurfaceVariant = TextOnSurfaceVariant,
    outline = OutlineColor,
    outlineVariant = OutlineVariant,
    error = ErrorRed,
    onError = OnErrorRed,
    errorContainer = ErrorContainerRed,
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest
)

private val PsicoDarkColorScheme = darkColorScheme(
    primary = DarkPinePrimary,
    onPrimary = DarkOnPinePrimary,
    primaryContainer = DarkPinePrimaryContainer,
    onPrimaryContainer = DarkPinePrimaryFixed,
    secondary = DarkSageSecondary,
    onSecondary = DarkOnSageSecondary,
    secondaryContainer = DarkSageSecondaryContainer,
    onSecondaryContainer = DarkOnSageSecondaryContainer,
    tertiary = DarkPeachTertiary,
    onTertiary = DarkOnPeachTertiary,
    tertiaryContainer = DarkPeachTertiaryFixed,
    onTertiaryContainer = DarkOnPeachTertiaryFixed,
    background = DarkCanvasBackground,
    onBackground = DarkTextOnSurface,
    surface = DarkSurfaceCanvas,
    onSurface = DarkTextOnSurface,
    surfaceVariant = DarkSurfaceContainerHigh,
    onSurfaceVariant = DarkTextOnSurfaceVariant,
    outline = DarkOutlineColor,
    outlineVariant = DarkOutlineVariant,
    error = ErrorRed,
    onError = OnErrorRed,
    errorContainer = ErrorContainerRed,
    surfaceContainerLowest = DarkSurfaceContainerLowest,
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    fontStyle: AppFontStyle = AppFontStyle.DEFAULT,
    fontScale: Float = 1.0f,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) PsicoDarkColorScheme else PsicoLightColorScheme
    val typography = getAppTypography(fontStyle, fontScale)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
