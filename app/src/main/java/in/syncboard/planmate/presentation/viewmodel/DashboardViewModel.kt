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
 * Data class for recent transactions
 */
data class Transaction(
    val id: String,
    val title: String,
    val amount: Double,
    val category: String,
    val date: String,
    val isIncome: Boolean = false
)

/**
 * UI State for Dashboard Screen
 */
data class DashboardUiState(
    val isLoading: Boolean = true,
    val userName: String = "John Doe",
    val totalBalance: Double = 45672.0,
    val monthlyIncome: Double = 85000.0,
    val monthlyExpense: Double = 42580.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val errorMessage: String? = null
)

/**
 * Dashboard ViewModel
 * Manages dashboard data and user financial overview
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    // Future: Inject repositories here
    // private val userRepository: UserRepository,
    // private val transactionRepository: TransactionRepository
) : ViewModel() {

    var uiState by mutableStateOf(DashboardUiState())
        private set

    init {
        loadDashboardData()
    }

    /**
     * Load dashboard data from local database
     * Currently using mock data, will be replaced with real data later
     */
    private fun loadDashboardData() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            // Simulate loading delay
            delay(1500)

            // Mock recent transactions data
            val mockTransactions = listOf(
                Transaction(
                    id = "1",
                    title = "Starbucks Coffee",
                    amount = 450.0,
                    category = "Food & Dining",
                    date = "2h ago",
                    isIncome = false
                ),
                Transaction(
                    id = "2",
                    title = "Salary Credit",
                    amount = 85000.0,
                    category = "Income",
                    date = "1d ago",
                    isIncome = true
                ),
                Transaction(
                    id = "3",
                    title = "Grocery Shopping",
                    amount = 2340.0,
                    category = "Shopping",
                    date = "2d ago",
                    isIncome = false
                ),
                Transaction(
                    id = "4",
                    title = "Movie Tickets",
                    amount = 800.0,
                    category = "Entertainment",
                    date = "3d ago",
                    isIncome = false
                ),
                Transaction(
                    id = "5",
                    title = "Freelance Payment",
                    amount = 15000.0,
                    category = "Income",
                    date = "4d ago",
                    isIncome = true
                )
            )

            uiState = uiState.copy(
                isLoading = false,
                recentTransactions = mockTransactions
            )
        }
    }

    /**
     * Refresh dashboard data
     * Called when user pulls to refresh
     */
    fun refreshData() {
        loadDashboardData()
    }

    /**
     * Get formatted balance string
     */
    fun getFormattedBalance(): String {
        return "₹${String.format("%,.0f", uiState.totalBalance)}"
    }

    /**
     * Get formatted monthly income
     */
    fun getFormattedMonthlyIncome(): String {
        return "+₹${String.format("%,.0f", uiState.monthlyIncome)}"
    }

    /**
     * Get formatted monthly expense
     */
    fun getFormattedMonthlyExpense(): String {
        return "₹${String.format("%,.0f", uiState.monthlyExpense)}"
    }

    /**
     * Get savings percentage
     */
    fun getSavingsPercentage(): Int {
        val savings = uiState.monthlyIncome - uiState.monthlyExpense
        return ((savings / uiState.monthlyIncome) * 100).toInt()
    }

    /**
     * Clear any error messages
     */
    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}