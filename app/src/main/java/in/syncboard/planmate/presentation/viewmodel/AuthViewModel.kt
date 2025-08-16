package `in`.syncboard.planmate.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Authentication Screens
 * Holds all the state needed for login and registration
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false,
    val isRegistrationSuccessful: Boolean = false
)

/**
 * Authentication ViewModel
 * Handles login and registration logic
 *
 * @HiltViewModel - Tells Hilt this is a ViewModel that can be injected
 * @Inject constructor() - Allows Hilt to create instances of this class
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    // Future: We'll inject repository here for actual authentication
    // private val authRepository: AuthRepository
) : ViewModel() {

    // Mutable state that the UI can observe
    var uiState by mutableStateOf(AuthUiState())
        private set

    /**
     * Handle user login
     * For now, this simulates login without actual backend
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            // Show loading state
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            // Simulate network call delay
            delay(2000)

            // Simple validation (replace with real authentication later)
            if (email.isBlank() || password.isBlank()) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Please fill in all fields"
                )
                return@launch
            }

            if (!email.contains("@")) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Please enter a valid email"
                )
                return@launch
            }

            if (password.length < 6) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Password must be at least 6 characters"
                )
                return@launch
            }

            // Simulate successful login
            uiState = uiState.copy(
                isLoading = false,
                isLoginSuccessful = true,
                errorMessage = null
            )
        }
    }

    /**
     * Handle user registration
     * For now, this simulates registration without actual backend
     */
    fun register(name: String, email: String, phone: String, password: String) {
        viewModelScope.launch {
            // Show loading state
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            // Simulate network call delay
            delay(2000)

            // Simple validation
            when {
                name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = "Please fill in all fields"
                    )
                    return@launch
                }

                !email.contains("@") -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = "Please enter a valid email"
                    )
                    return@launch
                }

                password.length < 6 -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = "Password must be at least 6 characters"
                    )
                    return@launch
                }

                phone.length < 10 -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = "Please enter a valid phone number"
                    )
                    return@launch
                }
            }

            // Simulate successful registration
            uiState = uiState.copy(
                isLoading = false,
                isRegistrationSuccessful = true,
                errorMessage = null
            )
        }
    }

    /**
     * Clear any error messages
     * Called when user starts typing or dismisses error
     */
    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }

    /**
     * Reset authentication state
     * Called when navigating between login/register screens
     */
    fun resetState() {
        uiState = AuthUiState()
    }
}