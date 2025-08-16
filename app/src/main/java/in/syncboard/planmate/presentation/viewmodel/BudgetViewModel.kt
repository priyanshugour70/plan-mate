// Path: app/src/main/java/in/syncboard/planmate/presentation/viewmodel/BudgetViewModel.kt

package `in`.syncboard.planmate.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import `in`.syncboard.planmate.domain.repository.AuthRepository
import `in`.syncboard.planmate.domain.repository.BudgetRepository
import `in`.syncboard.planmate.domain.repository.CategoryRepository
import `in`.syncboard.planmate.domain.repository.TransactionRepository
import `in`.syncboard.planmate.domain.entity.*
import java.util.*
import javax.inject.Inject

/**
 * Budget Category Data Class for UI
 */
data class BudgetCategoryItem(
    val id: String,
    val name: String,
    val icon: String,
    val budget: Double,
    val spent: Double,
    val color: androidx.compose.ui.graphics.Color
)

/**
 * UI State for Budget Screen
 */
data class BudgetUiState(
    val isLoading: Boolean = true,
    val totalBudget: Double = 0.0,
    val totalSpent: Double = 0.0,
    val budgetCategories: List<BudgetCategoryItem> = emptyList(),
    val availableCategories: List<Category> = emptyList(),
    val errorMessage: String? = null,
    val currentUserId: String = ""
)

/**
 * Add Budget UI State
 */
data class AddBudgetUiState(
    val selectedCategory: Category? = null,
    val amount: String = "",
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isValidForm: Boolean = false
)

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    var uiState by mutableStateOf(BudgetUiState())
        private set

    var addBudgetState by mutableStateOf(AddBudgetUiState())
        private set

    init {
        loadUserAndBudgets()
    }

    private fun loadUserAndBudgets() {
        viewModelScope.launch {
            authRepository.getCurrentUser().fold(
                onSuccess = { user ->
                    if (user != null) {
                        uiState = uiState.copy(currentUserId = user.id)
                        loadBudgetData(user.id)
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

    private suspend fun loadBudgetData(userId: String) {
        try {
            // Load current budgets
            budgetRepository.getCurrentBudgets(userId).fold(
                onSuccess = { budgets ->
                    // Load categories for each budget
                    val budgetItems = mutableListOf<BudgetCategoryItem>()

                    for (budget in budgets) {
                        categoryRepository.getCategoryById(budget.categoryId).fold(
                            onSuccess = { category ->
                                if (category != null) {
                                    budgetItems.add(
                                        BudgetCategoryItem(
                                            id = budget.id,
                                            name = category.name,
                                            icon = category.icon,
                                            budget = budget.allocatedAmount,
                                            spent = budget.spentAmount,
                                            color = parseColor(category.color)
                                        )
                                    )
                                }
                            },
                            onFailure = { /* Skip this budget item */ }
                        )
                    }

                    val totalBudget = budgetItems.sumOf { it.budget }
                    val totalSpent = budgetItems.sumOf { it.spent }

                    uiState = uiState.copy(
                        budgetCategories = budgetItems,
                        totalBudget = totalBudget,
                        totalSpent = totalSpent
                    )
                },
                onFailure = { exception ->
                    uiState = uiState.copy(
                        errorMessage = exception.message ?: "Failed to load budgets"
                    )
                }
            )

            // Load available categories for creating new budgets
            loadAvailableCategories(userId)

            uiState = uiState.copy(isLoading = false)

        } catch (e: Exception) {
            uiState = uiState.copy(
                isLoading = false,
                errorMessage = "Failed to load budget data"
            )
        }
    }

    private suspend fun loadAvailableCategories(userId: String) {
        categoryRepository.getCategoriesByType(userId, TransactionType.EXPENSE).fold(
            onSuccess = { categories ->
                // Filter out categories that already have budgets
                val existingCategoryIds = uiState.budgetCategories.map { it.id }.toSet()
                val availableCategories = categories.filter { it.id !in existingCategoryIds }

                uiState = uiState.copy(availableCategories = availableCategories)
                addBudgetState = addBudgetState.copy(
                    selectedCategory = availableCategories.firstOrNull()
                )
            },
            onFailure = { /* Handle error silently */ }
        )
    }

    fun updateBudgetAmount(budgetId: String, newAmount: Double) {
        viewModelScope.launch {
            // Find the budget and update it
            val budget = uiState.budgetCategories.find { it.id == budgetId }
            if (budget != null && uiState.currentUserId.isNotEmpty()) {
                // Create updated budget entity
                val updatedBudget = Budget(
                    id = budgetId,
                    userId = uiState.currentUserId,
                    categoryId = "", // Would need to be stored/retrieved
                    allocatedAmount = newAmount,
                    spentAmount = budget.spent,
                    period = BudgetPeriod.MONTHLY,
                    startDate = getMonthStart(),
                    endDate = getMonthEnd(),
                    isActive = true,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                budgetRepository.updateBudget(updatedBudget).fold(
                    onSuccess = {
                        loadBudgetData(uiState.currentUserId)
                    },
                    onFailure = { exception ->
                        uiState = uiState.copy(
                            errorMessage = exception.message ?: "Failed to update budget"
                        )
                    }
                )
            }
        }
    }

    // Add Budget Functions
    fun updateSelectedCategory(category: Category) {
        addBudgetState = addBudgetState.copy(selectedCategory = category)
        validateAddBudgetForm()
    }

    fun updateBudgetAmountInput(amount: String) {
        addBudgetState = addBudgetState.copy(amount = amount)
        validateAddBudgetForm()
    }

    fun updateBudgetPeriod(period: BudgetPeriod) {
        addBudgetState = addBudgetState.copy(period = period)
    }

    private fun validateAddBudgetForm() {
        val isValid = addBudgetState.selectedCategory != null &&
                addBudgetState.amount.isNotBlank() &&
                addBudgetState.amount.toDoubleOrNull() != null &&
                addBudgetState.amount.toDoubleOrNull()!! > 0

        addBudgetState = addBudgetState.copy(isValidForm = isValid)
    }

    fun addBudget(onSuccess: () -> Unit) {
        if (!addBudgetState.isValidForm || uiState.currentUserId.isEmpty()) return

        viewModelScope.launch {
            addBudgetState = addBudgetState.copy(isSaving = true)

            try {
                val budget = Budget(
                    id = UUID.randomUUID().toString(),
                    userId = uiState.currentUserId,
                    categoryId = addBudgetState.selectedCategory!!.id,
                    allocatedAmount = addBudgetState.amount.toDouble(),
                    spentAmount = 0.0,
                    period = addBudgetState.period,
                    startDate = getMonthStart(),
                    endDate = getMonthEnd(),
                    isActive = true,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                budgetRepository.createBudget(budget).fold(
                    onSuccess = {
                        addBudgetState = AddBudgetUiState() // Reset form
                        loadBudgetData(uiState.currentUserId) // Refresh data
                        onSuccess()
                    },
                    onFailure = { exception ->
                        addBudgetState = addBudgetState.copy(
                            isSaving = false,
                            errorMessage = exception.message ?: "Failed to create budget"
                        )
                    }
                )
            } catch (e: Exception) {
                addBudgetState = addBudgetState.copy(
                    isSaving = false,
                    errorMessage = "Failed to create budget"
                )
            }
        }
    }

    fun deleteBudget(budgetId: String) {
        viewModelScope.launch {
            budgetRepository.deleteBudget(budgetId).fold(
                onSuccess = {
                    if (uiState.currentUserId.isNotEmpty()) {
                        loadBudgetData(uiState.currentUserId)
                    }
                },
                onFailure = { exception ->
                    uiState = uiState.copy(
                        errorMessage = exception.message ?: "Failed to delete budget"
                    )
                }
            )
        }
    }

    fun getRemainingBudget(): Double {
        return uiState.totalBudget - uiState.totalSpent
    }

    fun getBudgetUsagePercentage(): Int {
        return if (uiState.totalBudget > 0) {
            ((uiState.totalSpent / uiState.totalBudget) * 100).toInt()
        } else {
            0
        }
    }

    fun isBudgetOverLimit(): Boolean {
        return uiState.totalSpent > uiState.totalBudget
    }

    fun getBudgetTip(): String {
        val percentage = getBudgetUsagePercentage()
        return when {
            percentage >= 90 -> "You're close to your budget limit. Consider reducing expenses."
            percentage >= 75 -> "You've used most of your budget. Monitor your spending carefully."
            percentage >= 50 -> "You're on track! Keep monitoring your expenses."
            else -> "Great start! You have plenty of room in your budget."
        }
    }

    fun formatAmount(amount: Double): String {
        return "₹${String.format("%,.0f", amount)}"
    }

    private fun getMonthStart(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getMonthEnd(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    private fun parseColor(colorString: String): androidx.compose.ui.graphics.Color {
        return try {
            androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(colorString))
        } catch (e: Exception) {
            `in`.syncboard.planmate.ui.theme.Primary500
        }
    }

    fun refreshData() {
        if (uiState.currentUserId.isNotEmpty()) {
            viewModelScope.launch {
                loadBudgetData(uiState.currentUserId)
            }
        }
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
        addBudgetState = addBudgetState.copy(errorMessage = null)
    }
}