// Path: app/src/main/java/in/syncboard/planmate/presentation/viewmodel/ProfileViewModel.kt

package `in`.syncboard.planmate.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import `in`.syncboard.planmate.domain.repository.AuthRepository
import `in`.syncboard.planmate.domain.repository.UserRepository
import `in`.syncboard.planmate.domain.repository.TransactionRepository
import `in`.syncboard.planmate.domain.repository.SettingsRepository
import `in`.syncboard.planmate.domain.entity.User
import `in`.syncboard.planmate.domain.entity.Settings
import `in`.syncboard.planmate.domain.entity.TransactionType
import javax.inject.Inject

/**
 * Profile Statistics Data Class
 */
data class ProfileStats(
    val totalExpenses: Int = 0,
    val moneySaved: Double = 0.0,
    val goalsAchieved: Int = 0,
    val accountAge: String = ""
)

/**
 * UI State for Profile Screen
 */
data class ProfileUiState(
    val isLoading: Boolean = true,
    val currentUser: User? = null,
    val userStats: ProfileStats = ProfileStats(),
    val userSettings: Settings? = null,
    val errorMessage: String? = null
)

/**
 * Edit Profile UI State
 */
data class EditProfileUiState(
    val isEditing: Boolean = false,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val transactionRepository: TransactionRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    var editProfileState by mutableStateOf(EditProfileUiState())
        private set

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            try {
                // Get current user
                authRepository.getCurrentUser().fold(
                    onSuccess = { user ->
                        if (user != null) {
                            uiState = uiState.copy(currentUser = user)
                            loadUserStats(user.id)
                            loadUserSettings(user.id)
                        } else {
                            uiState = uiState.copy(
                                isLoading = false,
                                errorMessage = "User not found"
                            )
                        }
                    },
                    onFailure = { exception ->
                        uiState = uiState.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Failed to load user"
                        )
                    }
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "An unexpected error occurred"
                )
            }
        }
    }

    private suspend fun loadUserStats(userId: String) {
        try {
            // Get total expenses count
            transactionRepository.getTransactionsByUser(userId).fold(
                onSuccess = { transactions ->
                    val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
                    val income = transactions.filter { it.type == TransactionType.INCOME }

                    val totalIncome = income.sumOf { it.amount }
                    val totalExpenses = expenses.sumOf { it.amount }
                    val moneySaved = totalIncome - totalExpenses

                    // Calculate account age
                    val accountAge = calculateAccountAge(uiState.currentUser?.createdAt ?: 0)

                    val stats = ProfileStats(
                        totalExpenses = expenses.size,
                        moneySaved = moneySaved,
                        goalsAchieved = 0, // Would be calculated from actual goals
                        accountAge = accountAge
                    )

                    uiState = uiState.copy(userStats = stats)
                },
                onFailure = { /* Handle error silently */ }
            )
        } catch (e: Exception) {
            // Handle error silently
        }
    }

    private suspend fun loadUserSettings(userId: String) {
        settingsRepository.getSettings(userId).fold(
            onSuccess = { settings ->
                uiState = uiState.copy(
                    userSettings = settings,
                    isLoading = false
                )
            },
            onFailure = {
                uiState = uiState.copy(isLoading = false)
            }
        )
    }

    fun startEditingProfile() {
        uiState.currentUser?.let { user ->
            editProfileState = editProfileState.copy(
                isEditing = true,
                name = user.name,
                email = user.email,
                phone = user.phoneNumber ?: ""
            )
        }
    }

    fun updateName(name: String) {
        editProfileState = editProfileState.copy(name = name)
    }

    fun updateEmail(email: String) {
        editProfileState = editProfileState.copy(email = email)
    }

    fun updatePhone(phone: String) {
        editProfileState = editProfileState.copy(phone = phone)
    }

    fun saveProfile() {
        val user = uiState.currentUser ?: return

        viewModelScope.launch {
            editProfileState = editProfileState.copy(isSaving = true)

            try {
                val updatedUser = user.copy(
                    name = editProfileState.name,
                    email = editProfileState.email,
                    phoneNumber = editProfileState.phone.ifBlank { null },
                    updatedAt = System.currentTimeMillis()
                )

                userRepository.updateUser(updatedUser).fold(
                    onSuccess = {
                        uiState = uiState.copy(currentUser = updatedUser)
                        editProfileState = EditProfileUiState() // Reset edit state
                    },
                    onFailure = { exception ->
                        editProfileState = editProfileState.copy(
                            isSaving = false,
                            errorMessage = exception.message ?: "Failed to update profile"
                        )
                    }
                )
            } catch (e: Exception) {
                editProfileState = editProfileState.copy(
                    isSaving = false,
                    errorMessage = "Failed to update profile"
                )
            }
        }
    }

    fun cancelEditing() {
        editProfileState = EditProfileUiState()
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout().fold(
                onSuccess = {
                    onLogoutSuccess()
                },
                onFailure = { exception ->
                    uiState = uiState.copy(
                        errorMessage = exception.message ?: "Failed to logout"
                    )
                }
            )
        }
    }

    private fun calculateAccountAge(createdAt: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - createdAt
        val days = diff / (24 * 60 * 60 * 1000)

        return when {
            days < 30 -> "${days} days"
            days < 365 -> "${days / 30} months"
            else -> "${days / 365} years"
        }
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
        editProfileState = editProfileState.copy(errorMessage = null)
    }

    fun refreshProfile() {
        loadUserProfile()
    }
}