package com.example.employeetracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// --- DARK THEME CONFIG ---
private val DarkColorScheme = darkColorScheme(
    primary = VioletLight,         // Light Purple (visible on dark)
    onPrimary = BackgroundSpace,
    secondary = NeonTeal,
    onSecondary = BackgroundSpace,
    tertiary = HotPink,
    
    background = BackgroundSpace,  // Deep Navy
    onBackground = TextWhite,
    
    surface = SurfaceDark,         // Cards
    onSurface = TextWhite,
    
    // THIS FIXES THE SEARCH BAR IN DARK MODE:
    surfaceVariant = SearchBarDark, 
    onSurfaceVariant = TextWhite
)

// --- LIGHT THEME CONFIG ---
private val LightColorScheme = lightColorScheme(
    primary = ElectricViolet,      // Deep Purple
    onPrimary = SurfaceWhite,
    secondary = NeonTeal,
    onSecondary = SurfaceWhite,
    tertiary = HotPink,
    
    background = BackgroundIce,    // Cool Grey-Blue (Not White!)
    onBackground = TextBlack,
    
    surface = SurfaceWhite,        // Pure White Cards (Pops against background)
    onSurface = TextBlack,
    
    // THIS FIXES THE SEARCH BAR IN LIGHT MODE:
    surfaceVariant = SearchBarLight, 
    onSurfaceVariant = TextBlack
)

@Composable
fun EmployeeTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set to FALSE to force your vibrant colors
    dynamicColor: Boolean = false, 
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Make the status bar the same color as the background for a clean look
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
