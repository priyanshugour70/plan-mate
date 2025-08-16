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
 * Expense Data Class (Enhanced Transaction)
 */
data class Expense(
    val id: String,
    val title: String,
    val amount: Double,
    val category: String,
    val date: Long,
    val time: String,
    val location: String? = null,
    val notes: String? = null,
    val paymentMethod: String,
    val receiptPath: String? = null,
    val isIncome: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * UI State for Expense Screens
 */
data class ExpenseUiState(
    val isLoading: Boolean = true,
    val expenses: List<Expense> = emptyList(),
    val filteredExpenses: List<Expense> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val totalExpenses: Double = 0.0,
    val averageDaily: Double = 0.0,
    val errorMessage: String? = null,
    val isSaving: Boolean = false
)

/**
 * Add Expense UI State
 */
data class AddExpenseUiState(
    val amount: String = "",
    val description: String = "",
    val selectedCategory: String = AppConstants.DEFAULT_EXPENSE_CATEGORIES.first(),
    val selectedDate: Long = System.currentTimeMillis(),
    val selectedTime: String = "",
    val location: String = "",
    val notes: String = "",
    val selectedPaymentMethod: String = AppConstants.DEFAULT_PAYMENT_METHODS.first(),
    val receiptPath: String? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isValidForm: Boolean = false
)

/**
 * Expense ViewModel
 * Manages expense data, adding new expenses, and filtering
 */
@HiltViewModel
class ExpenseViewModel @Inject constructor(
    // Future: Inject repositories here
    // private val expenseRepository: ExpenseRepository
) : ViewModel() {

    var uiState by mutableStateOf(ExpenseUiState())
        private set

    var addExpenseState by mutableStateOf(AddExpenseUiState())
        private set

    init {
        loadExpenses()
    }

    /**
     * Load all expenses from local storage
     */
    private fun loadExpenses() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            // Simulate loading delay
            delay(1000)

            // Mock expense data
            val expenses = listOf(
                Expense("1", "Morning Coffee", 450.0, "Food & Dining", System.currentTimeMillis(), "9:30 AM", "Starbucks", null, "Card", null),
                Expense("2", "Uber Ride", 280.0, "Transportation", System.currentTimeMillis() - 3600000, "8:15 AM", "Home to Office", null, "UPI", null),
                Expense("3", "Grocery Shopping", 2340.0, "Shopping", System.currentTimeMillis() - 86400000, "6:45 PM", "BigBasket", "Weekly groceries", "Card", null),
                Expense("4", "Movie Tickets", 800.0, "Entertainment", System.currentTimeMillis() - 86400000, "7:30 PM", "PVR Cinemas", "Avengers movie", "Card", null),
                Expense("5", "Pharmacy", 650.0, "Health", System.currentTimeMillis() - 172800000, "2:15 PM", "Apollo Pharmacy", "Monthly medicines", "Cash", null),
                Expense("6", "Restaurant Dinner", 1850.0, "Food & Dining", System.currentTimeMillis() - 172800000, "8:00 PM", "The Spice Route", "Dinner with friends", "Card", null),
                Expense("7", "Petrol", 3500.0, "Transportation", System.currentTimeMillis() - 259200000, "4:30 PM", "HP Petrol Pump", "Full tank", "Card", null),
                Expense("8", "Online Shopping", 4200.0, "Shopping", System.currentTimeMillis() - 259200000, "11:00 AM", "Amazon", "Electronics purchase", "Card", null),
                Expense("9", "Salary Credit", 85000.0, "Income", System.currentTimeMillis() - 345600000, "12:00 PM", "Company", "Monthly salary", "Bank Transfer", null, true),
                Expense("10", "Gym Membership", 2000.0, "Health", System.currentTimeMillis() - 345600000, "10:00 AM", "Fitness First", "Monthly membership", "UPI", null)
            )

            val totalExpenses = expenses.filter { !it.isIncome }.sumOf { it.amount }
            val averageDaily = totalExpenses / 30 // Assuming 30 days

            uiState = uiState.copy(
                isLoading = false,
                expenses = expenses,
                filteredExpenses = expenses,
                totalExpenses = totalExpenses,
                averageDaily = averageDaily
            )
        }
    }

    /**
     * Search expenses by title or category
     */
    fun searchExpenses(query: String) {
        uiState = uiState.copy(searchQuery = query)
        filterExpenses()
    }

    /**
     * Filter expenses by category
     */
    fun filterByCategory(category: String?) {
        uiState = uiState.copy(selectedCategory = category)
        filterExpenses()
    }

    /**
     * Apply filters to expense list
     */
    private fun filterExpenses() {
        val filtered = uiState.expenses.filter { expense ->
            val matchesSearch = if (uiState.searchQuery.isBlank()) {
                true
            } else {
                expense.title.contains(uiState.searchQuery, ignoreCase = true) ||
                        expense.category.contains(uiState.searchQuery, ignoreCase = true)
            }

            val matchesCategory = if (uiState.selectedCategory == null) {
                true
            } else {
                expense.category == uiState.selectedCategory
            }

            matchesSearch && matchesCategory
        }

        val totalFiltered = filtered.filter { !it.isIncome }.sumOf { it.amount }
        val avgFiltered = if (filtered.isNotEmpty()) totalFiltered / filtered.size else 0.0

        uiState = uiState.copy(
            filteredExpenses = filtered,
            totalExpenses = totalFiltered,
            averageDaily = avgFiltered
        )
    }

    /**
     * Clear all filters
     */
    fun clearFilters() {
        uiState = uiState.copy(
            searchQuery = "",
            selectedCategory = null,
            filteredExpenses = uiState.expenses
        )
        filterExpenses()
    }

    // ========== Add Expense Functions ==========

    /**
     * Update amount in add expense form
     */
    fun updateAmount(amount: String) {
        addExpenseState = addExpenseState.copy(amount = amount)
        validateForm()
    }

    /**
     * Update description in add expense form
     */
    fun updateDescription(description: String) {
        addExpenseState = addExpenseState.copy(description = description)
        validateForm()
    }

    /**
     * Update selected category
     */
    fun updateCategory(category: String) {
        addExpenseState = addExpenseState.copy(selectedCategory = category)
    }

    /**
     * Update selected date
     */
    fun updateDate(date: Long) {
        addExpenseState = addExpenseState.copy(selectedDate = date)
    }

    /**
     * Update selected time
     */
    fun updateTime(time: String) {
        addExpenseState = addExpenseState.copy(selectedTime = time)
    }

    /**
     * Update location
     */
    fun updateLocation(location: String) {
        addExpenseState = addExpenseState.copy(location = location)
    }

    /**
     * Update notes
     */
    fun updateNotes(notes: String) {
        addExpenseState = addExpenseState.copy(notes = notes)
    }

    /**
     * Update payment method
     */
    fun updatePaymentMethod(paymentMethod: String) {
        addExpenseState = addExpenseState.copy(selectedPaymentMethod = paymentMethod)
    }

    /**
     * Validate add expense form
     */
    private fun validateForm() {
        val isValid = addExpenseState.amount.isNotBlank() &&
                addExpenseState.description.isNotBlank() &&
                addExpenseState.amount.toDoubleOrNull() != null &&
                addExpenseState.amount.toDoubleOrNull()!! > 0

        addExpenseState = addExpenseState.copy(isValidForm = isValid)
    }

    /**
     * Save new expense
     */
    fun saveExpense() {
        if (!addExpenseState.isValidForm) return

        viewModelScope.launch {
            addExpenseState = addExpenseState.copy(isSaving = true)

            // Simulate saving delay
            delay(1500)

            try {
                val newExpense = Expense(
                    id = System.currentTimeMillis().toString(),
                    title = addExpenseState.description,
                    amount = addExpenseState.amount.toDouble(),
                    category = addExpenseState.selectedCategory,
                    date = addExpenseState.selectedDate,
                    time = addExpenseState.selectedTime,
                    location = addExpenseState.location.ifBlank { null },
                    notes = addExpenseState.notes.ifBlank { null },
                    paymentMethod = addExpenseState.selectedPaymentMethod,
                    receiptPath = addExpenseState.receiptPath
                )

                // Add to existing list
                val updatedExpenses = listOf(newExpense) + uiState.expenses
                val totalExpenses = updatedExpenses.filter { !it.isIncome }.sumOf { it.amount }
                val averageDaily = totalExpenses / 30

                uiState = uiState.copy(
                    expenses = updatedExpenses,
                    filteredExpenses = updatedExpenses,
                    totalExpenses = totalExpenses,
                    averageDaily = averageDaily
                )

                // Reset form
                resetAddExpenseForm()

            } catch (e: Exception) {
                addExpenseState = addExpenseState.copy(
                    isSaving = false,
                    errorMessage = "Failed to save expense. Please try again."
                )
            }
        }
    }

    /**
     * Reset add expense form
     */
    fun resetAddExpenseForm() {
        addExpenseState = AddExpenseUiState()
    }

    /**
     * Delete expense
     */
    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            val updatedExpenses = uiState.expenses.filter { it.id != expenseId }
            val totalExpenses = updatedExpenses.filter { !it.isIncome }.sumOf { it.amount }
            val averageDaily = if (updatedExpenses.isNotEmpty()) totalExpenses / updatedExpenses.size else 0.0

            uiState = uiState.copy(
                expenses = updatedExpenses,
                filteredExpenses = updatedExpenses,
                totalExpenses = totalExpenses,
                averageDaily = averageDaily
            )
        }
    }

    /**
     * Format amount for display
     */
    fun formatAmount(amount: Double): String {
        return "₹${String.format("%,.0f", amount)}"
    }

    /**
     * Get expenses grouped by date
     */
    fun getExpensesByDate(): Map<String, List<Expense>> {
        return uiState.filteredExpenses.groupBy { expense ->
            when {
                isToday(expense.date) -> "Today"
                isYesterday(expense.date) -> "Yesterday"
                isThisWeek(expense.date) -> formatDate(expense.date)
                else -> formatDate(expense.date)
            }
        }
    }

    /**
     * Helper functions for date checking
     */
    private fun isToday(timestamp: Long): Boolean {
        val today = java.util.Calendar.getInstance()
        val date = java.util.Calendar.getInstance().apply { timeInMillis = timestamp }
        return today.get(java.util.Calendar.YEAR) == date.get(java.util.Calendar.YEAR) &&
                today.get(java.util.Calendar.DAY_OF_YEAR) == date.get(java.util.Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(timestamp: Long): Boolean {
        val yesterday = java.util.Calendar.getInstance().apply { add(java.util.Calendar.DAY_OF_YEAR, -1) }
        val date = java.util.Calendar.getInstance().apply { timeInMillis = timestamp }
        return yesterday.get(java.util.Calendar.YEAR) == date.get(java.util.Calendar.YEAR) &&
                yesterday.get(java.util.Calendar.DAY_OF_YEAR) == date.get(java.util.Calendar.DAY_OF_YEAR)
    }

    private fun isThisWeek(timestamp: Long): Boolean {
        val today = java.util.Calendar.getInstance()
        val date = java.util.Calendar.getInstance().apply { timeInMillis = timestamp }
        return today.get(java.util.Calendar.YEAR) == date.get(java.util.Calendar.YEAR) &&
                today.get(java.util.Calendar.WEEK_OF_YEAR) == date.get(java.util.Calendar.WEEK_OF_YEAR)
    }

    private fun formatDate(timestamp: Long): String {
        val formatter = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        return formatter.format(java.util.Date(timestamp))
    }

    /**
     * Refresh expense data
     */
    fun refreshData() {
        loadExpenses()
    }

    /**
     * Clear error messages
     */
    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
        addExpenseState = addExpenseState.copy(errorMessage = null)
    }
}