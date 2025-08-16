package `in`.syncboard.planmate.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 * Dark Color Scheme for PlanMate
 * Used when device is in dark mode or user manually selects dark theme
 */
private val DarkColorScheme = darkColorScheme(
    // Primary colors
    primary = Primary200,           // Lighter blue for dark backgrounds
    onPrimary = Primary900,         // Dark blue text on primary
    primaryContainer = Primary800,   // Container background
    onPrimaryContainer = Primary100, // Text on container

    // Secondary colors
    secondary = Secondary200,
    onSecondary = Secondary900,
    secondaryContainer = Secondary800,
    onSecondaryContainer = Secondary100,

    // Tertiary colors (green for money/success)
    tertiary = Tertiary200,
    onTertiary = Tertiary900,
    tertiaryContainer = Tertiary800,
    onTertiaryContainer = Tertiary100,

    // Error colors
    error = Error200,
    onError = Error900,
    errorContainer = Error800,
    onErrorContainer = Error100,

    // Background colors
    background = Neutral95,          // Very dark background
    onBackground = Neutral10,        // Light text on dark background
    surface = Neutral90,             // Card surfaces
    onSurface = Neutral20,          // Text on surfaces

    // Surface variants
    surfaceVariant = Neutral80,
    onSurfaceVariant = Neutral30,

    // Outline colors
    outline = Neutral60,
    outlineVariant = Neutral70,

    // Other colors
    inverseSurface = Neutral20,
    inverseOnSurface = Neutral90,
    inversePrimary = Primary700,
    surfaceTint = Primary200,
)

/**
 * Light Color Scheme for PlanMate
 * Used when device is in light mode (default)
 */
private val LightColorScheme = lightColorScheme(
    // Primary colors (blue theme for trust and finance)
    primary = Primary500,           // Main blue color
    onPrimary = Neutral0,          // White text on primary
    primaryContainer = Primary100,  // Light blue containers
    onPrimaryContainer = Primary900, // Dark blue text on light containers

    // Secondary colors (purple for accents)
    secondary = Secondary500,
    onSecondary = Neutral0,
    secondaryContainer = Secondary100,
    onSecondaryContainer = Secondary900,

    // Tertiary colors (green for money/success)
    tertiary = Tertiary500,
    onTertiary = Neutral0,
    tertiaryContainer = Tertiary100,
    onTertiaryContainer = Tertiary900,

    // Error colors (red for expenses/warnings)
    error = Error500,
    onError = Neutral0,
    errorContainer = Error100,
    onErrorContainer = Error900,

    // Background colors
    background = Neutral10,          // Very light gray background
    onBackground = Neutral90,        // Dark text on light background
    surface = Neutral0,              // White surfaces (cards, sheets)
    onSurface = Neutral90,          // Dark text on white surfaces

    // Surface variants
    surfaceVariant = Neutral30,
    onSurfaceVariant = Neutral70,

    // Outline colors for borders
    outline = Neutral50,
    outlineVariant = Neutral40,

    // Other colors
    inverseSurface = Neutral90,
    inverseOnSurface = Neutral10,
    inversePrimary = Primary200,
    surfaceTint = Primary500,
)

/**
 * Main PlanMate Theme Composable
 * This wraps your entire app and provides Material 3 theming
 */
@Composable
fun PlanMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Auto-detect system theme
    // Dynamic color is available on Android 12+
    // It uses colors from user's wallpaper
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Choose color scheme based on theme and Android version
    val colorScheme = when {
        // Use dynamic colors if available (Android 12+)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        // Use our custom dark theme
        darkTheme -> DarkColorScheme

        // Use our custom light theme
        else -> LightColorScheme
    }

    // Get current view and system UI controller
    val view = LocalView.current
    val systemUiController = rememberSystemUiController()

    // Handle status bar and navigation bar colors
    if (!view.isInEditMode) {
        SideEffect {
            // Set status bar color
            systemUiController.setStatusBarColor(
                color = colorScheme.primary,
                darkIcons = !darkTheme
            )

            // Set navigation bar color
            systemUiController.setNavigationBarColor(
                color = colorScheme.background,
                darkIcons = !darkTheme
            )
        }
    }

    // Apply Material 3 theme to all child composables
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,      // Custom typography (defined in Type.kt)
        content = content            // Your app content
    )
}