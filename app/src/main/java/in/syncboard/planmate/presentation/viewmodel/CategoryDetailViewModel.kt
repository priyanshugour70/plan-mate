// Path: app/src/main/java/in/syncboard/planmate/presentation/viewmodel/CategoryDetailViewModel.kt

package `in`.syncboard.planmate.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import `in`.syncboard.planmate.domain.repository.AuthRepository
import `in`.syncboard.planmate.domain.repository.CategoryRepository
import `in`.syncboard.planmate.domain.repository.TransactionRepository
import `in`.syncboard.planmate.domain.repository.BudgetRepository
import `in`.syncboard.planmate.domain.entity.*
import java.util.*
import javax.inject.Inject

/**
 * UI State for Category Detail Screen
 */
data class CategoryDetailUiState(
    val isLoading: Boolean = true,
    val category: Category? = null,
    val budget: Budget? = null,
    val transactionCount: Int = 0,
    val totalAmount: Double = 0.0,
    val averageAmount: Double = 0.0,
    val recentTransactions: List<TransactionItem> = emptyList(),
    val monthlyTrend: List<Pair<String, Double>> = emptyList(),
    val errorMessage: String? = null,
    val currentUserId: String = ""
)

@HiltViewModel
class CategoryDetailViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    var uiState by mutableStateOf(CategoryDetailUiState())
        private set

    fun loadCategoryDetails(categoryId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            try {
                // Get current user
                authRepository.getCurrentUser().fold(
                    onSuccess = { user ->
                        if (user != null) {
                            uiState = uiState.copy(currentUserId = user.id)
                            loadCategoryData(user.id, categoryId)
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

    private suspend fun loadCategoryData(userId: String, categoryId: String) {
        try {
            // Load all data concurrently
            val categoryDeferred = viewModelScope.async {
                categoryRepository.getCategoryById(categoryId)
            }

            val transactionsDeferred = viewModelScope.async {
                transactionRepository.getTransactionsByCategory(userId, categoryId)
            }

            val budgetDeferred = viewModelScope.async {
                budgetRepository.getCurrentBudgets(userId)
            }

            // Wait for all data
            val results = awaitAll(
                categoryDeferred,
                transactionsDeferred,
                budgetDeferred
            )

            // Process category
            @Suppress("UNCHECKED_CAST")
            (results[0] as? Result<Category?>)?.fold(
                onSuccess = { category ->
                    uiState = uiState.copy(category = category)
                },
                onFailure = {
                    uiState = uiState.copy(errorMessage = "Category not found")
                    return@loadCategoryData
                }
            )

            // Process transactions
            @Suppress("UNCHECKED_CAST")
            (results[1] as? Result<List<Transaction>>)?.fold(
                onSuccess = { transactions ->
                    processTransactionData(transactions)
                },
                onFailure = { /* Handle silently */ }
            )

            // Process budget
            @Suppress("UNCHECKED_CAST")
            (results[2] as? Result<List<Budget>>)?.fold(
                onSuccess = { budgets ->
                    val categoryBudget = budgets.find { it.categoryId == categoryId }
                    uiState = uiState.copy(budget = categoryBudget)
                },
                onFailure = { /* Handle silently */ }
            )

            uiState = uiState.copy(isLoading = false)

        } catch (e: Exception) {
            uiState = uiState.copy(
                isLoading = false,
                errorMessage = "Failed to load category details"
            )
        }
    }

    private fun processTransactionData(transactions: List<Transaction>) {
        val transactionCount = transactions.size
        val totalAmount = transactions.sumOf { it.amount }
        val averageAmount = if (transactions.isNotEmpty()) totalAmount / transactions.size else 0.0

        // Convert to TransactionItems for UI
        val transactionItems = transactions.map { transaction ->
            TransactionItem(
                id = transaction.id,
                title = transaction.title,
                amount = transaction.amount,
                category = transaction.category.name,
                categoryIcon = transaction.category.icon,
                categoryColor = transaction.category.color,
                date = formatDate(transaction.transactionDate),
                time = formatTime(transaction.transactionDate),
                isIncome = transaction.type == TransactionType.INCOME,
                location = transaction.location
            )
        }.sortedByDescending { it.date }

        // Calculate monthly trend (simplified)
        val monthlyTrend = calculateMonthlyTrend(transactions)

        uiState = uiState.copy(
            transactionCount = transactionCount,
            totalAmount = totalAmount,
            averageAmount = averageAmount,
            recentTransactions = transactionItems,
            monthlyTrend = monthlyTrend
        )
    }

    private fun calculateMonthlyTrend(transactions: List<Transaction>): List<Pair<String, Double>> {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        val monthlyData = mutableMapOf<String, Double>()

        // Calculate last 6 months
        for (i in 5 downTo 0) {
            calendar.set(Calendar.YEAR, currentYear)
            calendar.set(Calendar.MONTH, currentMonth - i)

            val monthYear = "${calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, java.util.Locale.getDefault())} ${calendar.get(Calendar.YEAR)}"
            val monthStart = calendar.apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val monthEnd = calendar.apply {
                add(Calendar.MONTH, 1)
                add(Calendar.DAY_OF_MONTH, -1)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            val monthlyAmount = transactions.filter { transaction ->
                transaction.transactionDate in monthStart..monthEnd
            }.sumOf { it.amount }

            monthlyData[monthYear] = monthlyAmount

            // Reset calendar for next iteration
            calendar.add(Calendar.MONTH, -1)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return monthlyData.toList()
    }

    private fun formatDate(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 24 * 60 * 60 * 1000 -> "Today"
            diff < 2 * 24 * 60 * 60 * 1000 -> "Yesterday"
            diff < 7 * 24 * 60 * 60 * 1000 -> {
                val date = Date(timestamp)
                java.text.SimpleDateFormat("EEEE", java.util.Locale.getDefault()).format(date)
            }
            else -> {
                val date = Date(timestamp)
                java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault()).format(date)
            }
        }
    }

    private fun formatTime(timestamp: Long): String {
        val date = Date(timestamp)
        return java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(date)
    }

    fun refreshData() {
        uiState.category?.let { category ->
            loadCategoryDetails(category.id)
        }
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}