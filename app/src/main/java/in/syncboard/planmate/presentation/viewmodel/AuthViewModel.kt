// Path: app/src/main/java/in/syncboard/planmate/presentation/viewmodel/AuthViewModel.kt

package `in`.syncboard.planmate.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import `in`.syncboard.planmate.domain.repository.AuthRepository
import `in`.syncboard.planmate.domain.entity.User
import javax.inject.Inject

/**
 * UI State for Authentication Screens
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false,
    val isRegistrationSuccessful: Boolean = false,
    val currentUser: User? = null,
    val isLoggedIn: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            try {
                val isLoggedIn = authRepository.isUserLoggedIn()
                if (isLoggedIn) {
                    authRepository.getCurrentUser().fold(
                        onSuccess = { user ->
                            uiState = uiState.copy(
                                currentUser = user,
                                isLoggedIn = true
                            )
                        },
                        onFailure = {
                            // Clear invalid session
                            authRepository.logout()
                            uiState = uiState.copy(isLoggedIn = false)
                        }
                    )
                } else {
                    uiState = uiState.copy(isLoggedIn = false)
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoggedIn = false,
                    errorMessage = "Failed to check authentication state"
                )
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            // Validate inputs
            if (email.isBlank() || password.isBlank()) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Please fill in all fields"
                )
                return@launch
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Please enter a valid email"
                )
                return@launch
            }

            authRepository.login(email, password).fold(
                onSuccess = { user ->
                    uiState = uiState.copy(
                        isLoading = false,
                        isLoginSuccessful = true,
                        currentUser = user,
                        isLoggedIn = true,
                        errorMessage = null
                    )
                },
                onFailure = { exception ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Login failed"
                    )
                }
            )
        }
    }

    fun register(name: String, email: String, phone: String, password: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            // Validate inputs
            when {
                name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = "Please fill in all fields"
                    )
                    return@launch
                }

                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
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

            authRepository.register(name, email, phone, password).fold(
                onSuccess = { user ->
                    uiState = uiState.copy(
                        isLoading = false,
                        isRegistrationSuccessful = true,
                        currentUser = user,
                        isLoggedIn = true,
                        errorMessage = null
                    )
                },
                onFailure = { exception ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Registration failed"
                    )
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout().fold(
                onSuccess = {
                    uiState = AuthUiState() // Reset to initial state
                },
                onFailure = { exception ->
                    uiState = uiState.copy(
                        errorMessage = exception.message ?: "Logout failed"
                    )
                }
            )
        }
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }

    fun resetState() {
        uiState = uiState.copy(
            isLoginSuccessful = false,
            isRegistrationSuccessful = false,
            errorMessage = null
        )
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Please enter a valid email"
                )
                return@launch
            }

            authRepository.resetPassword(email).fold(
                onSuccess = {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = "Password reset instructions sent to your email"
                    )
                },
                onFailure = { exception ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Failed to send reset email"
                    )
                }
            )
        }
    }
}