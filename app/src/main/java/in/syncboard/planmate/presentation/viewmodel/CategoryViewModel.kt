// Path: app/src/main/java/in/syncboard/planmate/presentation/viewmodel/CategoryViewModel.kt

package `in`.syncboard.planmate.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import `in`.syncboard.planmate.domain.repository.AuthRepository
import `in`.syncboard.planmate.domain.repository.CategoryRepository
import `in`.syncboard.planmate.domain.repository.TransactionRepository
import `in`.syncboard.planmate.domain.entity.*
import java.util.*
import javax.inject.Inject

/**
 * Category Item for UI
 */
data class CategoryItem(
    val id: String,
    val name: String,
    val type: TransactionType,
    val icon: String,
    val color: String,
    val isDefault: Boolean,
    val transactionCount: Int = 0,
    val totalAmount: Double = 0.0,
    val isActive: Boolean = true
)

/**
 * UI State for Category Screen
 */
data class CategoryUiState(
    val isLoading: Boolean = true,
    val expenseCategories: List<CategoryItem> = emptyList(),
    val incomeCategories: List<CategoryItem> = emptyList(),
    val allCategories: List<CategoryItem> = emptyList(),
    val errorMessage: String? = null,
    val currentUserId: String = ""
)

/**
 * Add Category UI State
 */
data class AddCategoryUiState(
    val name: String = "",
    val selectedType: TransactionType = TransactionType.EXPENSE,
    val selectedIcon: String = "📁",
    val selectedColor: String = "#2196F3",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isValidForm: Boolean = false
)

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    var uiState by mutableStateOf(CategoryUiState())
        private set

    var addCategoryState by mutableStateOf(AddCategoryUiState())
        private set

    init {
        loadUserAndCategories()
    }

    private fun loadUserAndCategories() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            authRepository.getCurrentUser().fold(
                onSuccess = { user ->
                    if (user != null) {
                        uiState = uiState.copy(currentUserId = user.id)
                        loadCategories(user.id)
                        // Initialize default categories if none exist
                        initializeDefaultCategories(user.id)
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
        }
    }

    private suspend fun loadCategories(userId: String) {
        try {
            // Load user categories
            categoryRepository.getCategoriesByUser(userId).fold(
                onSuccess = { categories ->
                    val categoryItems = categories.map { category ->
                        // Get transaction stats for each category (simplified)
                        CategoryItem(
                            id = category.id,
                            name = category.name,
                            type = category.type,
                            icon = category.icon,
                            color = category.color,
                            isDefault = category.isDefault,
                            transactionCount = 0, // Would need additional query
                            totalAmount = 0.0, // Would need additional query
                            isActive = category.isActive
                        )
                    }

                    val expenseCategories = categoryItems.filter { it.type == TransactionType.EXPENSE && it.isActive }
                    val incomeCategories = categoryItems.filter { it.type == TransactionType.INCOME && it.isActive }

                    uiState = uiState.copy(
                        expenseCategories = expenseCategories,
                        incomeCategories = incomeCategories,
                        allCategories = categoryItems.filter { it.isActive },
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Failed to load categories"
                    )
                }
            )
        } catch (e: Exception) {
            uiState = uiState.copy(
                isLoading = false,
                errorMessage = "Failed to load categories"
            )
        }
    }

    private suspend fun initializeDefaultCategories(userId: String) {
        // Check if user has any categories
        if (uiState.allCategories.isEmpty()) {
            // Create default categories
            val defaultExpenseCategories = listOf(
                Category(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    name = "Food & Dining",
                    type = TransactionType.EXPENSE,
                    icon = "🍕",
                    color = "#FF9800",
                    isDefault = true,
                    parentCategoryId = null,
                    isActive = true,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    name = "Transportation",
                    type = TransactionType.EXPENSE,
                    icon = "🚗",
                    color = "#2196F3",
                    isDefault = true,
                    parentCategoryId = null,
                    isActive = true,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    name = "Shopping",
                    type = TransactionType.EXPENSE,
                    icon = "🛍️",
                    color = "#E91E63",
                    isDefault = true,
                    parentCategoryId = null,
                    isActive = true,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    name = "Entertainment",
                    type = TransactionType.EXPENSE,
                    icon = "🎮",
                    color = "#9C27B0",
                    isDefault = true,
                    parentCategoryId = null,
                    isActive = true,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    name = "Health",
                    type = TransactionType.EXPENSE,
                    icon = "🏥",
                    color = "#4CAF50",
                    isDefault = true,
                    parentCategoryId = null,
                    isActive = true,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    name = "Bills & Utilities",
                    type = TransactionType.EXPENSE,
                    icon = "⚡",
                    color = "#FF5722",
                    isDefault = true,
                    parentCategoryId = null,
                    isActive = true,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            )

            val defaultIncomeCategories = listOf(
                Category(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    name = "Salary",
                    type = TransactionType.INCOME,
                    icon = "💰",
                    color = "#4CAF50",
                    isDefault = true,
                    parentCategoryId = null,
                    isActive = true,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    name = "Freelance",
                    type = TransactionType.INCOME,
                    icon = "💻",
                    color = "#2196F3",
                    isDefault = true,
                    parentCategoryId = null,
                    isActive = true,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    name = "Investment",
                    type = TransactionType.INCOME,
                    icon = "📈",
                    color = "#FF9800",
                    isDefault = true,
                    parentCategoryId = null,
                    isActive = true,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    name = "Business",
                    type = TransactionType.INCOME,
                    icon = "🏢",
                    color = "#9C27B0",
                    isDefault = true,
                    parentCategoryId = null,
                    isActive = true,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            )

            // Save all default categories
            (defaultExpenseCategories + defaultIncomeCategories).forEach { category ->
                categoryRepository.createCategory(category)
            }

            // Reload categories after creating defaults
            loadCategories(userId)
        }
    }

    // Add Category Functions
    fun updateCategoryName(name: String) {
        addCategoryState = addCategoryState.copy(name = name)
        validateForm()
    }

    fun updateCategoryType(type: TransactionType) {
        addCategoryState = addCategoryState.copy(selectedType = type)
    }

    fun updateCategoryIcon(icon: String) {
        addCategoryState = addCategoryState.copy(selectedIcon = icon)
    }

    fun updateCategoryColor(color: String) {
        addCategoryState = addCategoryState.copy(selectedColor = color)
    }

    private fun validateForm() {
        val isValid = addCategoryState.name.isNotBlank() &&
                addCategoryState.selectedIcon.isNotBlank() &&
                addCategoryState.selectedColor.isNotBlank()

        addCategoryState = addCategoryState.copy(isValidForm = isValid)
    }

    fun saveCategory(onSuccess: () -> Unit) {
        if (!addCategoryState.isValidForm || uiState.currentUserId.isEmpty()) return

        viewModelScope.launch {
            addCategoryState = addCategoryState.copy(isSaving = true)

            try {
                val category = Category(
                    id = UUID.randomUUID().toString(),
                    userId = uiState.currentUserId,
                    name = addCategoryState.name.trim(),
                    type = addCategoryState.selectedType,
                    icon = addCategoryState.selectedIcon,
                    color = addCategoryState.selectedColor,
                    isDefault = false,
                    parentCategoryId = null,
                    isActive = true,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                categoryRepository.createCategory(category).fold(
                    onSuccess = {
                        addCategoryState = AddCategoryUiState() // Reset form
                        loadCategories(uiState.currentUserId) // Refresh data
                        onSuccess()
                    },
                    onFailure = { exception ->
                        addCategoryState = addCategoryState.copy(
                            isSaving = false,
                            errorMessage = exception.message ?: "Failed to save category"
                        )
                    }
                )
            } catch (e: Exception) {
                addCategoryState = addCategoryState.copy(
                    isSaving = false,
                    errorMessage = "Failed to save category"
                )
            }
        }
    }

    fun editCategory(categoryId: String) {
        // TODO: Implement edit functionality
        // Navigate to edit screen or show edit dialog
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(categoryId).fold(
                onSuccess = {
                    if (uiState.currentUserId.isNotEmpty()) {
                        loadCategories(uiState.currentUserId)
                    }
                },
                onFailure = { exception ->
                    uiState = uiState.copy(
                        errorMessage = exception.message ?: "Failed to delete category"
                    )
                }
            )
        }
    }

    fun resetAddCategoryForm() {
        addCategoryState = AddCategoryUiState()
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
        addCategoryState = addCategoryState.copy(errorMessage = null)
    }

    fun refreshData() {
        if (uiState.currentUserId.isNotEmpty()) {
            viewModelScope.launch {
                loadCategories(uiState.currentUserId)
            }
        }
    }

    // Helper function to get categories for other screens
    fun getCategoriesByType(type: TransactionType): List<Category> {
        val categoryItems = if (type == TransactionType.EXPENSE) {
            uiState.expenseCategories
        } else {
            uiState.incomeCategories
        }

        return categoryItems.map { item ->
            Category(
                id = item.id,
                userId = uiState.currentUserId,
                name = item.name,
                type = item.type,
                icon = item.icon,
                color = item.color,
                isDefault = item.isDefault,
                parentCategoryId = null,
                isActive = item.isActive,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        }
    }
}