package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AppleRed,
    onPrimary = Color.White,
    secondary = ApplePink,
    onSecondary = Color.White,
    background = Color.Black,
    surface = DarkSurface,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = LightSurface
)

private val LightColorScheme = lightColorScheme(
    primary = AppleRed,
    onPrimary = Color.White,
    secondary = AppleBlue,
    onSecondary = Color.White,
    background = Color.White,
    surface = AppleGray,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun MusicStreamTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic for more "Apple" look
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
