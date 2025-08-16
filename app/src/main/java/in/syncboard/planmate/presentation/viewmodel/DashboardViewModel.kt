// Path: app/src/main/java/in/syncboard/planmate/presentation/viewmodel/DashboardViewModel.kt
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
import `in`.syncboard.planmate.domain.repository.TransactionRepository
import `in`.syncboard.planmate.domain.repository.BudgetRepository
import `in`.syncboard.planmate.domain.repository.CategoryRepository
import `in`.syncboard.planmate.domain.entity.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Enhanced Transaction for UI display
 */
data class TransactionItem(
    val id: String,
    val title: String,
    val amount: Double,
    val category: String,
    val categoryIcon: String,
    val categoryColor: String,
    val date: String,
    val time: String,
    val isIncome: Boolean = false,
    val location: String? = null
)

/**
 * Category spending data for analytics
 */
data class CategorySpending(
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: String,
    val totalAmount: Double,
    val transactionCount: Int,
    val percentage: Double
)

/**
 * Monthly comparison data
 */
data class MonthlyComparison(
    val currentMonth: Double,
    val lastMonth: Double,
    val percentageChange: Double,
    val isIncrease: Boolean
)

/**
 * Budget overview data
 */
data class BudgetOverview(
    val totalBudget: Double,
    val totalSpent: Double,
    val remainingBudget: Double,
    val usagePercentage: Double,
    val categoriesOverBudget: Int,
    val isOverBudget: Boolean
)

/**
 * Financial insights
 */
data class FinancialInsight(
    val title: String,
    val message: String,
    val type: InsightType,
    val actionRequired: Boolean = false
)

enum class InsightType {
    POSITIVE, WARNING, CRITICAL, INFO
}

/**
 * Enhanced UI State for Dashboard Screen
 */
data class DashboardUiState(
    val isLoading: Boolean = true,
    val currentUser: User? = null,
    val userName: String = "",

    // Balance & Overview
    val totalBalance: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val savingsRate: Double = 0.0,

    // Transaction Data
    val recentTransactions: List<TransactionItem> = emptyList(),
    val totalTransactionsThisMonth: Int = 0,

    // Category Analytics
    val topSpendingCategories: List<CategorySpending> = emptyList(),
    val categoryBreakdown: List<CategorySpending> = emptyList(),

    // Monthly Comparisons
    val incomeComparison: MonthlyComparison? = null,
    val expenseComparison: MonthlyComparison? = null,

    // Budget Overview
    val budgetOverview: BudgetOverview? = null,

    // Insights & Recommendations
    val financialInsights: List<FinancialInsight> = emptyList(),

    // Quick Stats
    val averageDailySpending: Double = 0.0,
    val daysUntilMonthEnd: Int = 0,
    val projectedMonthlySpending: Double = 0.0,

    val errorMessage: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    var uiState by mutableStateOf(DashboardUiState())
        private set

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            try {
                // Get current user
                authRepository.getCurrentUser().fold(
                    onSuccess = { user ->
                        if (user != null) {
                            uiState = uiState.copy(
                                currentUser = user,
                                userName = user.name
                            )
                            loadComprehensiveFinancialData(user.id)
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
                            errorMessage = exception.message ?: "Failed to load user data"
                        )
                    }
                )
            } catch (_: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "An unexpected error occurred"
                )
            }
        }
    }

    private suspend fun loadComprehensiveFinancialData(userId: String) {
        try {
            // Calculate date ranges
            val (currentMonthStart, currentMonthEnd) = getCurrentMonthRange()
            val (lastMonthStart, lastMonthEnd) = getLastMonthRange()

            // Load all data concurrently
            val currentMonthTransactionsDeferred = viewModelScope.async {
                transactionRepository.getTransactionsByDateRange(userId, currentMonthStart, currentMonthEnd)
            }

            val lastMonthTransactionsDeferred = viewModelScope.async {
                transactionRepository.getTransactionsByDateRange(userId, lastMonthStart, lastMonthEnd)
            }

            val recentTransactionsDeferred = viewModelScope.async {
                transactionRepository.getRecentTransactions(userId, 10)
            }

            val budgetDataDeferred = viewModelScope.async {
                budgetRepository.getCurrentBudgets(userId)
            }

            val categoriesDeferred = viewModelScope.async {
                categoryRepository.getCategoriesByUser(userId)
            }

            // Wait for all data
            val results = awaitAll(
                currentMonthTransactionsDeferred,
                lastMonthTransactionsDeferred,
                recentTransactionsDeferred,
                budgetDataDeferred,
                categoriesDeferred
            )

            // Process current month transactions
            @Suppress("UNCHECKED_CAST")
            (results[0] as? Result<List<Transaction>>)?.fold(
                onSuccess = { currentTransactions ->
                    processCurrentMonthData(currentTransactions)
                },
                onFailure = { /* Handle silently */ }
            )

            // Process last month transactions for comparison
            @Suppress("UNCHECKED_CAST")
            (results[1] as? Result<List<Transaction>>)?.fold(
                onSuccess = { lastTransactions ->
                    processMonthlyComparison(lastTransactions)
                },
                onFailure = { /* Handle silently */ }
            )

            // Process recent transactions
            @Suppress("UNCHECKED_CAST")
            (results[2] as? Result<List<Transaction>>)?.fold(
                onSuccess = { recentTransactions ->
                    processRecentTransactions(recentTransactions)
                },
                onFailure = { /* Handle silently */ }
            )

            // Process budget data
            @Suppress("UNCHECKED_CAST")
            (results[3] as? Result<List<Budget>>)?.fold(
                onSuccess = { budgets ->
                    processBudgetOverview(budgets)
                },
                onFailure = { /* Handle silently */ }
            )

            // Process categories
            @Suppress("UNCHECKED_CAST")
            (results[4] as? Result<List<Category>>)?.fold(
                onSuccess = { categories ->
                    processCategoryAnalytics(categories)
                },
                onFailure = { /* Handle silently */ }
            )

            // Generate insights
            generateFinancialInsights()

            // Calculate additional metrics
            calculateAdditionalMetrics()

            uiState = uiState.copy(isLoading = false)

        } catch (_: Exception) {
            uiState = uiState.copy(
                isLoading = false,
                errorMessage = "Failed to load financial data"
            )
        }
    }

    private fun processCurrentMonthData(transactions: List<Transaction>) {
        val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val balance = income - expenses
        val savingsRate = if (income > 0) ((income - expenses) / income) * 100 else 0.0

        uiState = uiState.copy(
            totalBalance = balance,
            monthlyIncome = income,
            monthlyExpense = expenses,
            savingsRate = savingsRate,
            totalTransactionsThisMonth = transactions.size
        )
    }

    private fun processMonthlyComparison(lastMonthTransactions: List<Transaction>) {
        val lastMonthIncome = lastMonthTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val lastMonthExpenses = lastMonthTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        val incomeChange = if (lastMonthIncome > 0) {
            ((uiState.monthlyIncome - lastMonthIncome) / lastMonthIncome) * 100
        } else 0.0

        val expenseChange = if (lastMonthExpenses > 0) {
            ((uiState.monthlyExpense - lastMonthExpenses) / lastMonthExpenses) * 100
        } else 0.0

        uiState = uiState.copy(
            incomeComparison = MonthlyComparison(
                currentMonth = uiState.monthlyIncome,
                lastMonth = lastMonthIncome,
                percentageChange = kotlin.math.abs(incomeChange),
                isIncrease = incomeChange > 0
            ),
            expenseComparison = MonthlyComparison(
                currentMonth = uiState.monthlyExpense,
                lastMonth = lastMonthExpenses,
                percentageChange = kotlin.math.abs(expenseChange),
                isIncrease = expenseChange > 0
            )
        )
    }

    private suspend fun processRecentTransactions(transactions: List<Transaction>) {
        val transactionItems = transactions.map { transaction ->
            // Get category details
            var categoryIcon = "💰"
            var categoryColor = "#6366F1"

            categoryRepository.getCategoryById(transaction.category.id).fold(
                onSuccess = { category ->
                    if (category != null) {
                        categoryIcon = category.icon
                        categoryColor = category.color
                    }
                },
                onFailure = { /* Use defaults */ }
            )

            TransactionItem(
                id = transaction.id,
                title = transaction.title,
                amount = transaction.amount,
                category = transaction.category.name,
                categoryIcon = categoryIcon,
                categoryColor = categoryColor,
                date = formatDate(transaction.transactionDate),
                time = formatTime(transaction.transactionDate),
                isIncome = transaction.type == TransactionType.INCOME,
                location = transaction.location
            )
        }

        uiState = uiState.copy(recentTransactions = transactionItems)
    }

    private fun processBudgetOverview(budgets: List<Budget>) {
        val totalBudget = budgets.sumOf { it.allocatedAmount }
        val totalSpent = budgets.sumOf { it.spentAmount }
        val remainingBudget = totalBudget - totalSpent
        val usagePercentage = if (totalBudget > 0) (totalSpent / totalBudget) * 100 else 0.0
        val categoriesOverBudget = budgets.count { it.spentAmount > it.allocatedAmount }

        uiState = uiState.copy(
            budgetOverview = BudgetOverview(
                totalBudget = totalBudget,
                totalSpent = totalSpent,
                remainingBudget = remainingBudget,
                usagePercentage = usagePercentage,
                categoriesOverBudget = categoriesOverBudget,
                isOverBudget = totalSpent > totalBudget
            )
        )
    }

    private fun processCategoryAnalytics(categories: List<Category>) {
        viewModelScope.launch {
            val (currentMonthStart, currentMonthEnd) = getCurrentMonthRange()

            transactionRepository.getTotalExpensesByCategory(
                uiState.currentUser?.id ?: "",
                currentMonthStart,
                currentMonthEnd
            ).fold(
                onSuccess = { categoryExpenses ->
                    val totalExpenses = categoryExpenses.values.sum()

                    val categorySpendingList = categories.filter {
                        categoryExpenses.containsKey(it.id)
                    }.map { category ->
                        val amount = categoryExpenses[category.id] ?: 0.0
                        val percentage = if (totalExpenses > 0) (amount / totalExpenses) * 100 else 0.0

                        CategorySpending(
                            categoryName = category.name,
                            categoryIcon = category.icon,
                            categoryColor = category.color,
                            totalAmount = amount,
                            transactionCount = 0, // Would need additional query
                            percentage = percentage
                        )
                    }.sortedByDescending { it.totalAmount }

                    uiState = uiState.copy(
                        topSpendingCategories = categorySpendingList.take(5),
                        categoryBreakdown = categorySpendingList
                    )
                },
                onFailure = { /* Handle silently */ }
            )
        }
    }

    private fun generateFinancialInsights() {
        val insights = mutableListOf<FinancialInsight>()

        // Budget insights
        uiState.budgetOverview?.let { budget ->
            when {
                budget.isOverBudget -> {
                    insights.add(
                        FinancialInsight(
                            title = "Budget Exceeded",
                            message = "You\'ve exceeded your monthly budget by ₹${formatAmount(budget.totalSpent - budget.totalBudget)}. Consider reviewing your expenses.",
                            type = InsightType.CRITICAL,
                            actionRequired = true
                        )
                    )
                }
                budget.usagePercentage > 90 -> {
                    insights.add(
                        FinancialInsight(
                            title = "Budget Alert",
                            message = "You\'ve used ${budget.usagePercentage.toInt()}% of your monthly budget. Only ₹${formatAmount(budget.remainingBudget)} remaining.",
                            type = InsightType.WARNING,
                            actionRequired = true
                        )
                    )
                }
                budget.usagePercentage < 50 -> {
                    insights.add(
                        FinancialInsight(
                            title = "Great Budgeting!",
                            message = "You\'re doing well with your budget. You still have ₹${formatAmount(budget.remainingBudget)} left for this month.",
                            type = InsightType.POSITIVE
                        )
                    )
                }
            }
        }

        // Savings insights
        when {
            uiState.savingsRate > 20 -> {
                insights.add(
                    FinancialInsight(
                        title = "Excellent Savings!",
                        message = "You\'re saving ${uiState.savingsRate.toInt()}% of your income. You\'re on track for financial success!",
                        type = InsightType.POSITIVE
                    )
                )
            }
            uiState.savingsRate > 10 -> {
                insights.add(
                    FinancialInsight(
                        title = "Good Savings Rate",
                        message = "You\'re saving ${uiState.savingsRate.toInt()}% of your income. Consider increasing it to 20% for better financial health.",
                        type = InsightType.INFO
                    )
                )
            }
            uiState.savingsRate < 5 -> {
                insights.add(
                    FinancialInsight(
                        title = "Low Savings Rate",
                        message = "You\'re only saving ${uiState.savingsRate.toInt()}% of your income. Try to save at least 10-20% for better financial security.",
                        type = InsightType.WARNING,
                        actionRequired = true
                    )
                )
            }
        }

        // Expense comparison insights
        uiState.expenseComparison?.let { comparison ->
            if (comparison.isIncrease && comparison.percentageChange > 20) {
                insights.add(
                    FinancialInsight(
                        title = "Spending Increase",
                        message = "Your expenses increased by ${comparison.percentageChange.toInt()}% compared to last month. Review your spending patterns.",
                        type = InsightType.WARNING,
                        actionRequired = true
                    )
                )
            } else if (!comparison.isIncrease && comparison.percentageChange > 10) {
                insights.add(
                    FinancialInsight(
                        title = "Reduced Spending",
                        message = "Great job! You reduced your expenses by ${comparison.percentageChange.toInt()}% compared to last month.",
                        type = InsightType.POSITIVE
                    )
                )
            }
        }

        // Category insights
        val topCategory = uiState.topSpendingCategories.firstOrNull()
        topCategory?.let {
            if (it.percentage > 40) {
                insights.add(
                    FinancialInsight(
                        title = "High Category Spending",
                        message = "${it.categoryName} accounts for ${it.percentage.toInt()}% of your expenses. Consider if this aligns with your priorities.",
                        type = InsightType.INFO
                    )
                )
            }
        }

        uiState = uiState.copy(financialInsights = insights)
    }

    private fun calculateAdditionalMetrics() {
        val daysInMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val daysRemaining = daysInMonth - currentDay

        // currentDay is always > 0, so the check is redundant
        val averageDaily = uiState.monthlyExpense / currentDay
        val projectedMonthly = averageDaily * daysInMonth

        uiState = uiState.copy(
            averageDailySpending = averageDaily,
            daysUntilMonthEnd = daysRemaining,
            projectedMonthlySpending = projectedMonthly
        )
    }

    // Helper functions
    private fun getCurrentMonthRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val monthStart = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val monthEnd = calendar.timeInMillis

        return Pair(monthStart, monthEnd)
    }

    private fun getLastMonthRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val lastMonthStart = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val lastMonthEnd = calendar.timeInMillis

        return Pair(lastMonthStart, lastMonthEnd)
    }

    private fun formatDate(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        return when {
            diff < 24 * 60 * 60 * 1000 -> "Today"
            diff < 2 * 24 * 60 * 60 * 1000 -> "Yesterday"
            else -> {
                val date = Date(timestamp)
                SimpleDateFormat("MMM dd", Locale.getDefault()).format(date)
            }
        }
    }

    private fun formatTime(timestamp: Long): String {
        val date = Date(timestamp)
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
    }

    private fun formatAmount(amount: Double): String {
        return String.format(Locale.getDefault(), "%,.0f", amount)
    }

    // Public interface methods
    fun refreshData() {
        loadDashboardData()
    }

    fun getFormattedBalance(): String {
        return "₹${formatAmount(uiState.totalBalance)}"
    }

    fun getFormattedMonthlyIncome(): String {
        return "₹${formatAmount(uiState.monthlyIncome)}"
    }

    fun getFormattedMonthlyExpense(): String {
        return "₹${formatAmount(uiState.monthlyExpense)}"
    }

    fun getSavingsPercentage(): Int {
        return uiState.savingsRate.toInt()
    }

    fun getBudgetUsagePercentage(): Int {
        return uiState.budgetOverview?.usagePercentage?.toInt() ?: 0
    }

    fun getAverageDailySpending(): String {
        return "₹${formatAmount(uiState.averageDailySpending)}"
    }

    fun getProjectedMonthlySpending(): String {
        return "₹${formatAmount(uiState.projectedMonthlySpending)}"
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}