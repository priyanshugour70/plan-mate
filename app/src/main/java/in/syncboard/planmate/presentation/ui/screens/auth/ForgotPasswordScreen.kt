// Path: app/src/main/java/in/syncboard/planmate/presentation/ui/screens/auth/ForgotPasswordScreen.kt

package `in`.syncboard.planmate.presentation.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
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
 * Forgot Password Screen
 */
@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var isEmailSent by remember { mutableStateOf(false) }

    val uiState = viewModel.uiState

    // Show loading overlay when processing
    if (uiState.isLoading) {
        LoadingState(message = "Sending reset email...")
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
        // Header with Back Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Go back",
                    tint = Color.White
                )
            }
        }

        // Title Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Forgot Password?",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Don't worry! Enter your email and we'll send you instructions to reset your password",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Reset Form Card
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
                if (!isEmailSent) {
                    // Email Input Form
                    Text(
                        text = "Reset Password",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    CustomTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (uiState.errorMessage != null) {
                                viewModel.clearError()
                            }
                        },
                        label = "Email Address",
                        placeholder = "Enter your registered email",
                        leadingIcon = Icons.Default.Email,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Error Message
                    if (uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Send Reset Email Button
                    GradientButton(
                        text = "Send Reset Email",
                        onClick = {
                            viewModel.resetPassword(email)
                            isEmailSent = true
                        },
                        enabled = email.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // Success Message
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Email Sent!",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Tertiary700,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Text(
                            text = "We've sent password reset instructions to:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Text(
                            text = "Please check your email and follow the instructions to reset your password.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        OutlinedButton(
                            onClick = {
                                isEmailSent = false
                                email = ""
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Try Different Email")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Back to Login Link
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Remember your password? ",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            TextButton(
                onClick = onNavigateBack
            ) {
                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}