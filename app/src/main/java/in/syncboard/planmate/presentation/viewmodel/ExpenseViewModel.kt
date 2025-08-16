// Path: app/src/main/java/in/syncboard/planmate/presentation/viewmodel/ExpenseViewModel.kt (Fixed)

package `in`.syncboard.planmate.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import `in`.syncboard.planmate.domain.repository.AuthRepository
import `in`.syncboard.planmate.domain.repository.TransactionRepository
import `in`.syncboard.planmate.domain.repository.CategoryRepository
import `in`.syncboard.planmate.domain.entity.*
import java.util.*
import javax.inject.Inject

/**
 * Expense Data Class for UI
 */
data class ExpenseItem(
    val id: String,
    val title: String,
    val amount: Double,
    val category: String,
    val date: Long,
    val time: String,
    val location: String? = null,
    val notes: String? = null,
    val paymentMethod: String,
    val isIncome: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * UI State for Expense Screens
 */
data class ExpenseUiState(
    val isLoading: Boolean = true,
    val expenses: List<ExpenseItem> = emptyList(),
    val filteredExpenses: List<ExpenseItem> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val totalExpenses: Double = 0.0,
    val averageDaily: Double = 0.0,
    val errorMessage: String? = null,
    val currentUserId: String = ""
)

/**
 * Add Expense UI State
 */
data class AddExpenseUiState(
    val amount: String = "",
    val description: String = "",
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val selectedDate: Long = System.currentTimeMillis(),
    val selectedTime: String = "",
    val location: String = "",
    val notes: String = "",
    val selectedPaymentMethod: String = "Card",
    val receiptPath: String? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isValidForm: Boolean = false
)

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    var uiState by mutableStateOf(ExpenseUiState())
        private set

    var addExpenseState by mutableStateOf(AddExpenseUiState())
        private set

    init {
        loadUserAndExpenses()
    }

    private fun loadUserAndExpenses() {
        viewModelScope.launch {
            authRepository.getCurrentUser().fold(
                onSuccess = { user ->
                    if (user != null) {
                        uiState = uiState.copy(currentUserId = user.id)
                        loadExpenses(user.id)
                        loadCategories(user.id)
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

    private suspend fun loadExpenses(userId: String) {
        transactionRepository.getTransactionsByUser(userId).fold(
            onSuccess = { transactions ->
                val expenseItems = transactions.map { transaction ->
                    ExpenseItem(
                        id = transaction.id,
                        title = transaction.title,
                        amount = transaction.amount,
                        category = transaction.category.name,
                        date = transaction.transactionDate,
                        time = formatTime(transaction.transactionDate),
                        location = transaction.location,
                        notes = transaction.description,
                        paymentMethod = "Card", // Would come from transaction
                        isIncome = transaction.type == TransactionType.INCOME
                    )
                }

                val totalExpenses = expenseItems.filter { !it.isIncome }.sumOf { it.amount }
                val averageDaily = if (expenseItems.isNotEmpty()) totalExpenses / 30 else 0.0

                uiState = uiState.copy(
                    isLoading = false,
                    expenses = expenseItems,
                    filteredExpenses = expenseItems,
                    totalExpenses = totalExpenses,
                    averageDaily = averageDaily
                )
            },
            onFailure = { exception ->
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Failed to load expenses"
                )
            }
        )
    }

    private suspend fun loadCategories(userId: String) {
        categoryRepository.getCategoriesByType(userId, TransactionType.EXPENSE).fold(
            onSuccess = { categories ->
                addExpenseState = addExpenseState.copy(
                    categories = categories,
                    selectedCategory = categories.firstOrNull()
                )
            },
            onFailure = { /* Handle error silently */ }
        )
    }

    fun searchExpenses(query: String) {
        uiState = uiState.copy(searchQuery = query)
        filterExpenses()
    }

    fun filterByCategory(category: String?) {
        uiState = uiState.copy(selectedCategory = category)
        filterExpenses()
    }

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

    // Add Expense Functions
    fun updateAmount(amount: String) {
        addExpenseState = addExpenseState.copy(amount = amount)
        validateForm()
    }

    fun updateDescription(description: String) {
        addExpenseState = addExpenseState.copy(description = description)
        validateForm()
    }

    fun updateCategory(category: Category) {
        addExpenseState = addExpenseState.copy(selectedCategory = category)
        validateForm()
    }

    fun updateDate(date: Long) {
        addExpenseState = addExpenseState.copy(selectedDate = date)
    }

    fun updateLocation(location: String) {
        addExpenseState = addExpenseState.copy(location = location)
    }

    fun updateNotes(notes: String) {
        addExpenseState = addExpenseState.copy(notes = notes)
    }

    fun updatePaymentMethod(paymentMethod: String) {
        addExpenseState = addExpenseState.copy(selectedPaymentMethod = paymentMethod)
    }

    private fun validateForm() {
        val isValid = addExpenseState.amount.isNotBlank() &&
                addExpenseState.description.isNotBlank() &&
                addExpenseState.amount.toDoubleOrNull() != null &&
                addExpenseState.amount.toDoubleOrNull()!! > 0 &&
                addExpenseState.selectedCategory != null

        addExpenseState = addExpenseState.copy(isValidForm = isValid)
    }

    fun saveExpense(onSuccess: () -> Unit) {
        if (!addExpenseState.isValidForm || uiState.currentUserId.isEmpty()) return

        viewModelScope.launch {
            addExpenseState = addExpenseState.copy(isSaving = true)

            try {
                val transaction = Transaction(
                    id = UUID.randomUUID().toString(),
                    userId = uiState.currentUserId,
                    accountId = "", // Would be implemented later
                    amount = addExpenseState.amount.toDouble(),
                    type = TransactionType.EXPENSE,
                    category = addExpenseState.selectedCategory!!,
                    title = addExpenseState.description,
                    description = addExpenseState.notes.ifBlank { null },
                    location = addExpenseState.location.ifBlank { null },
                    receiptUrl = addExpenseState.receiptPath,
                    tags = emptyList(),
                    transactionDate = addExpenseState.selectedDate,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                transactionRepository.addTransaction(transaction).fold(
                    onSuccess = {
                        addExpenseState = AddExpenseUiState() // Reset form
                        loadExpenses(uiState.currentUserId) // Refresh data
                        onSuccess()
                    },
                    onFailure = { exception ->
                        addExpenseState = addExpenseState.copy(
                            isSaving = false,
                            errorMessage = exception.message ?: "Failed to save expense"
                        )
                    }
                )
            } catch (e: Exception) {
                addExpenseState = addExpenseState.copy(
                    isSaving = false,
                    errorMessage = "Failed to save expense"
                )
            }
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(expenseId).fold(
                onSuccess = {
                    if (uiState.currentUserId.isNotEmpty()) {
                        loadExpenses(uiState.currentUserId)
                    }
                },
                onFailure = { exception ->
                    uiState = uiState.copy(
                        errorMessage = exception.message ?: "Failed to delete expense"
                    )
                }
            )
        }
    }

    fun resetAddExpenseForm() {
        addExpenseState = AddExpenseUiState()
        if (uiState.currentUserId.isNotEmpty()) {
            // Fixed: Wrap in viewModelScope.launch since loadCategories is suspend
            viewModelScope.launch {
                loadCategories(uiState.currentUserId)
            }
        }
    }

    private fun formatTime(timestamp: Long): String {
        val date = Date(timestamp)
        return java.text.SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
        addExpenseState = addExpenseState.copy(errorMessage = null)
    }

    fun refreshData() {
        if (uiState.currentUserId.isNotEmpty()) {
            viewModelScope.launch {
                loadExpenses(uiState.currentUserId)
            }
        }
    }
}