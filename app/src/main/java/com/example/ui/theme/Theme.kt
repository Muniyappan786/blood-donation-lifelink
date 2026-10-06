package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF8A80),
    onPrimary = Color(0xFF5F0004),
    primaryContainer = BloodRed10,
    onPrimaryContainer = Color(0xFFFFDAD6),
    secondary = MedicalTeal80,
    onSecondary = Color(0xFF00382F),
    secondaryContainer = MedicalTeal20,
    onSecondaryContainer = Color(0xFFA6F2E2),
    tertiary = Color(0xFFFFB4A9),
    background = NeutralDarkSurface,
    onBackground = NeutralDarkText,
    surface = NeutralDarkSurface,
    onSurface = NeutralDarkText,
    surfaceVariant = NeutralDarkCard,
    onSurfaceVariant = Color(0xFFD7C1C1)
)

private val LightColorScheme = lightColorScheme(
    primary = BloodRed40,
    onPrimary = Color.White,
    primaryContainer = BloodRed90,
    onPrimaryContainer = BloodRed10,
    secondary = MedicalTeal40,
    onSecondary = Color.White,
    secondaryContainer = MedicalTeal80,
    onSecondaryContainer = MedicalTeal20,
    tertiary = Color(0xFF9C413D),
    background = NeutralLightSurface,
    onBackground = NeutralLightText,
    surface = NeutralLightSurface,
    onSurface = NeutralLightText,
    surfaceVariant = Color(0xFFF3ECEC),
    onSurfaceVariant = Color(0xFF534343)
)

@Composable
fun BloodBridgeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent medical branding
    content: @Composable () -> Unit
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
