package `in`.syncboard.planmate.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import `in`.syncboard.planmate.core.constants.AppConstants
import javax.inject.Inject

/**
 * Budget Category Data Class
 */
data class BudgetCategory(
    val id: String,
    val name: String,
    val icon: String,
    val allocated: Double,
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
    val categories: List<BudgetCategory> = emptyList(),
    val errorMessage: String? = null
)

/**
 * Budget ViewModel
 * Manages budget allocation and category-wise spending
 */
@HiltViewModel
class BudgetViewModel @Inject constructor(
    // Future: Inject repositories here
    // private val budgetRepository: BudgetRepository
) : ViewModel() {

    var uiState by mutableStateOf(BudgetUiState())
        private set

    init {
        loadBudgetData()
    }

    /**
     * Load budget data from local storage
     */
    private fun loadBudgetData() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            // Simulate loading delay
            delay(1000)

            // Mock budget categories data
            val categories = listOf(
                BudgetCategory(
                    id = "1",
                    name = "Food & Dining",
                    icon = "🍕",
                    allocated = 15000.0,
                    spent = 12340.0,
                    color = `in`.syncboard.planmate.ui.theme.FoodColor
                ),
                BudgetCategory(
                    id = "2",
                    name = "Transportation",
                    icon = "🚗",
                    allocated = 8000.0,
                    spent = 5420.0,
                    color = `in`.syncboard.planmate.ui.theme.TransportColor
                ),
                BudgetCategory(
                    id = "3",
                    name = "Shopping",
                    icon = "🛍️",
                    allocated = 12000.0,
                    spent = 10200.0,
                    color = `in`.syncboard.planmate.ui.theme.ShoppingColor
                ),
                BudgetCategory(
                    id = "4",
                    name = "Entertainment",
                    icon = "🎮",
                    allocated = 5000.0,
                    spent = 3200.0,
                    color = `in`.syncboard.planmate.ui.theme.EntertainmentColor
                ),
                BudgetCategory(
                    id = "5",
                    name = "Health",
                    icon = "🏥",
                    allocated = 6000.0,
                    spent = 2800.0,
                    color = `in`.syncboard.planmate.ui.theme.HealthColor
                ),
                BudgetCategory(
                    id = "6",
                    name = "Bills & Utilities",
                    icon = "⚡",
                    allocated = 8000.0,
                    spent = 7800.0,
                    color = `in`.syncboard.planmate.ui.theme.BillsColor
                )
            )

            val totalBudget = categories.sumOf { it.allocated }
            val totalSpent = categories.sumOf { it.spent }

            uiState = uiState.copy(
                isLoading = false,
                totalBudget = totalBudget,
                totalSpent = totalSpent,
                categories = categories
            )
        }
    }

    /**
     * Update budget for a category
     */
    fun updateCategoryBudget(categoryId: String, newBudget: Double) {
        viewModelScope.launch {
            val updatedCategories = uiState.categories.map { category ->
                if (category.id == categoryId) {
                    category.copy(allocated = newBudget)
                } else {
                    category
                }
            }

            val newTotalBudget = updatedCategories.sumOf { it.allocated }

            uiState = uiState.copy(
                categories = updatedCategories,
                totalBudget = newTotalBudget
            )
        }
    }

    /**
     * Add new budget category
     */
    fun addCategory(name: String, icon: String, budget: Double) {
        viewModelScope.launch {
            val newCategory = BudgetCategory(
                id = System.currentTimeMillis().toString(),
                name = name,
                icon = icon,
                allocated = budget,
                spent = 0.0,
                color = `in`.syncboard.planmate.ui.theme.Primary500
            )

            val updatedCategories = uiState.categories + newCategory
            val newTotalBudget = updatedCategories.sumOf { it.allocated }

            uiState = uiState.copy(
                categories = updatedCategories,
                totalBudget = newTotalBudget
            )
        }
    }

    /**
     * Get remaining budget
     */
    fun getRemainingBudget(): Double {
        return uiState.totalBudget - uiState.totalSpent
    }

    /**
     * Get budget usage percentage
     */
    fun getBudgetUsagePercentage(): Int {
        return if (uiState.totalBudget > 0) {
            ((uiState.totalSpent / uiState.totalBudget) * 100).toInt()
        } else {
            0
        }
    }

    /**
     * Check if budget is over limit
     */
    fun isBudgetOverLimit(): Boolean {
        return uiState.totalSpent > uiState.totalBudget
    }

    /**
     * Get budget warning level
     */
    fun getBudgetWarningLevel(): BudgetWarningLevel {
        val percentage = getBudgetUsagePercentage()
        return when {
            percentage >= AppConstants.BUDGET_DANGER_THRESHOLD -> BudgetWarningLevel.DANGER
            percentage >= AppConstants.BUDGET_WARNING_THRESHOLD -> BudgetWarningLevel.WARNING
            else -> BudgetWarningLevel.SAFE
        }
    }

    /**
     * Get budget tip based on current usage
     */
    fun getBudgetTip(): String {
        val percentage = getBudgetUsagePercentage()
        return when {
            percentage >= 90 -> "You're close to your budget limit. Consider reducing expenses."
            percentage >= 75 -> "You've used most of your budget. Monitor your spending carefully."
            percentage >= 50 -> "You're on track! Keep monitoring your expenses."
            else -> "Great start! You have plenty of room in your budget."
        }
    }

    /**
     * Format currency amounts
     */
    fun formatAmount(amount: Double): String {
        return "₹${String.format("%,.0f", amount)}"
    }

    /**
     * Refresh budget data
     */
    fun refreshData() {
        loadBudgetData()
    }

    /**
     * Clear error message
     */
    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}

/**
 * Budget Warning Level Enum
 */
enum class BudgetWarningLevel {
    SAFE,
    WARNING,
    DANGER
}