package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val PubColorScheme = lightColorScheme(
    primary = PubPrimary,
    onPrimary = Color.White,
    primaryContainer = PubPrimaryDark,
    onPrimaryContainer = Color.White,
    secondary = PubAmber,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFFDF0DB),
    onSecondaryContainer = Color(0xFF4A3004),
    tertiary = PubGold,
    onTertiary = Color.Black,
    background = PubBackground,
    onBackground = PubTextPrimary,
    surface = PubSurface,
    onSurface = PubTextPrimary,
    surfaceVariant = Color(0xFFEAEFEA),
    onSurfaceVariant = PubTextSecondary,
    outline = PubBorder,
    error = PubDanger,
    onError = Color.White
)

@Composable
fun CecilPubTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = PubColorScheme,
        typography = Typography,
        content = content
    )
}
