// Path: app/src/main/java/in/syncboard/planmate/MainActivity.kt

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import `in`.syncboard.planmate.presentation.navigation.PlanMateNavigation
import `in`.syncboard.planmate.presentation.viewmodel.AuthViewModel
import `in`.syncboard.planmate.ui.theme.PlanMateTheme

/**
 * Main Activity - Entry point of the app
 * Updated to integrate with authentication flow
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

                    // Get shared AuthViewModel
                    val authViewModel: AuthViewModel = hiltViewModel()

                    // Start the navigation system with authentication
                    PlanMateNavigation(
                        navController = navController,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}