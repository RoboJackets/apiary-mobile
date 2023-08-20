package org.robojackets.apiary.base.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = GoldLight,
    primaryVariant = GoldDark,
    secondary = Color.White,
)

private val LightColorPalette = lightColors(
    primary = Gold,
    primaryVariant = GoldDark,
    secondary = Color.Black,

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

val LightColorScheme = lightColorScheme(
    primary = Gold,
    secondary = GoldDark,
    tertiary = Color.Black,
)
val DarkColorScheme = darkColorScheme(
    primary = Gold,
    secondary = GoldDark,
    tertiary = Color.White,
)

@Composable
fun Apiary_MobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
