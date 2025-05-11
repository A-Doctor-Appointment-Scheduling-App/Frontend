package com.example.doccur.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorPalette = lightColors(
    primary = Blue,
    primaryVariant = DarkBlue,
    secondary = Teal,
    secondaryVariant = DarkTeal,
    background = BackgroundLight,
    surface = White,
    error = Red,
    onPrimary = White,
    onSecondary = White,
    onBackground = Black,
    onSurface = Black,
    onError = White
)

private val DarkColorPalette = darkColors(
    primary = LightBlue,
    primaryVariant = Blue,
    secondary = LightTeal,
    secondaryVariant = Teal,
    background = DarkGray,
    surface = Black,
    error = Red,
    onPrimary = Black,
    onSecondary = Black,
    onBackground = White,
    onSurface = White,
    onError = White
)

@Composable
fun DocCurTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}