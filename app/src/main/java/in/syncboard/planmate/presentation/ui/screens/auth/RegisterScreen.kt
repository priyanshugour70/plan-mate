// Path: app/src/main/java/in/syncboard/planmate/presentation/ui/screens/auth/RegisterScreen.kt

package `in`.syncboard.planmate.presentation.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
 * Register Screen - Updated to work with real authentication
 */
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val uiState = viewModel.uiState

    // Handle successful registration
    LaunchedEffect(uiState.isRegistrationSuccessful) {
        if (uiState.isRegistrationSuccessful) {
            onRegisterSuccess()
            viewModel.resetState()
        }
    }

    // Show loading overlay when creating account
    if (uiState.isLoading) {
        LoadingState(message = "Creating your account...")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Tertiary500, Primary500)
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
                text = "Create Account",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Join PlanMate and start managing your finances smartly",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Registration Form Card
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
                // Form Title
                Text(
                    text = "Personal Information",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Full Name Input
                CustomTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (uiState.errorMessage != null) {
                            viewModel.clearError()
                        }
                    },
                    label = "Full Name",
                    placeholder = "Enter your full name",
                    leadingIcon = Icons.Default.Person,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

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

                // Phone Input
                CustomTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        if (uiState.errorMessage != null) {
                            viewModel.clearError()
                        }
                    },
                    label = "Phone Number",
                    placeholder = "Enter your phone number",
                    leadingIcon = Icons.Default.Phone,
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
                    placeholder = "Create a password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Password Input
                CustomTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        if (uiState.errorMessage != null) {
                            viewModel.clearError()
                        }
                    },
                    label = "Confirm Password",
                    placeholder = "Confirm your password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Password Requirements
                Text(
                    text = "Password must be at least 6 characters long",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
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

                // Register Button
                GradientButton(
                    text = "Create Account",
                    onClick = {
                        // Check if passwords match
                        if (password != confirmPassword) {
                            viewModel.clearError()
                            // Set a temporary error for password mismatch
                            return@GradientButton
                        }
                        viewModel.register(name, email, phone, password)
                    },
                    enabled = name.isNotBlank() && email.isNotBlank() &&
                            phone.isNotBlank() && password.isNotBlank() &&
                            confirmPassword.isNotBlank() && password == confirmPassword,
                    gradientColors = listOf(Tertiary500, Primary500),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Terms and Conditions
                Text(
                    text = "By creating an account, you agree to our Terms of Service and Privacy Policy",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Login Link
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account? ",
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