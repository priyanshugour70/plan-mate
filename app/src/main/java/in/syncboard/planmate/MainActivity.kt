package `in`.syncboard.planmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import `in`.syncboard.planmate.presentation.navigation.PlanMateNavigation
import `in`.syncboard.planmate.ui.theme.PlanMateTheme

/**
 * Main Activity - Entry point of the app
 *
 * @AndroidEntryPoint - Tells Hilt this Activity should have dependencies injected
 * This is the root of our app and sets up the navigation and theme
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display (full screen)
        enableEdgeToEdge()

        // Make status bar and navigation bar transparent
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            // Apply our custom Material 3 theme
            PlanMateTheme {
                // Create a surface container for the entire app
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Set up navigation controller
                    val navController = rememberNavController()

                    // Start the navigation system
                    PlanMateNavigation(navController = navController)
                }
            }
        }
    }
}