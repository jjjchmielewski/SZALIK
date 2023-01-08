package com.szalik.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

private val DarkColorPalette = darkColors(
    primary = ButtonDark,
    primaryVariant = ButtonVariantDark,
    background = BackgroundDark,
    onPrimary = Color.White
)

private val LightColorPalette = lightColors(
    primary = ButtonLight,
    primaryVariant = ButtonVariantLight,
    background = BackgroundLight,
    onPrimary = Color.Black

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun SzalikTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val activity = LocalView.current.context as Activity
    val colors: Colors
    if (darkTheme) {
        colors = DarkColorPalette
        activity.window.navigationBarColor = colors.primary.toArgb()
        activity.window.statusBarColor = colors.background.toArgb()
    } else {
        colors = LightColorPalette
        activity.window.navigationBarColor = colors.primary.toArgb()
        activity.window.statusBarColor = colors.background.toArgb()
    }




    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}