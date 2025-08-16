// Path: app/src/main/java/in/syncboard/planmate/presentation/ui/screens/auth/LoginScreen.kt

package `in`.syncboard.planmate.presentation.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import `in`.syncboard.planmate.presentation.ui.components.CustomTextField
import `in`.syncboard.planmate.presentation.ui.components.GradientButton
import `in`.syncboard.planmate.presentation.ui.components.LoadingState
import `in`.syncboard.planmate.presentation.viewmodel.AuthViewModel
import `in`.syncboard.planmate.ui.theme.*

/**
 * Login Screen - Updated to work with real authentication
 */
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val uiState = viewModel.uiState

    // Handle successful login
    LaunchedEffect(uiState.isLoginSuccessful) {
        if (uiState.isLoginSuccessful) {
            onLoginSuccess()
            viewModel.resetState()
        }
    }

    // Show loading overlay when authenticating
    if (uiState.isLoading) {
        LoadingState(message = "Signing you in...")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Primary500, Secondary500)
                )
            )
            .verticalScroll(rememberScrollState())
    ) {
        // Header Section with App Logo and Title
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 60.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Logo
            Card(
                modifier = Modifier.size(80.dp),
                shape = CardLargeShape,
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.2f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Wallet,
                        contentDescription = "PlanMate Logo",
                        modifier = Modifier.size(40.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App Name
            Text(
                text = "PlanMate",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            // App Tagline
            Text(
                text = "Plan smart. Spend wise. Live better.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Login Form Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = CardLargeShape,
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Welcome Text
                Text(
                    text = "Welcome Back",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Sign in to continue to PlanMate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 32.dp),
                    textAlign = TextAlign.Center
                )

                // Email Input
                CustomTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (uiState.errorMessage != null) {
                            viewModel.clearError()
                        }
                    },
                    label = "Email Address",
                    placeholder = "Enter your email",
                    leadingIcon = Icons.Default.Email,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Input
                CustomTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        if (uiState.errorMessage != null) {
                            viewModel.clearError()
                        }
                    },
                    label = "Password",
                    placeholder = "Enter your password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Error Message
                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Login Button
                GradientButton(
                    text = "Sign In",
                    onClick = {
                        viewModel.login(email, password)
                    },
                    enabled = email.isNotBlank() && password.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Forgot Password
                TextButton(
                    onClick = onNavigateToForgotPassword,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Forgot Password?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Primary500
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Register Link
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't have an account? ",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            TextButton(
                onClick = onNavigateToRegister
            ) {
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}