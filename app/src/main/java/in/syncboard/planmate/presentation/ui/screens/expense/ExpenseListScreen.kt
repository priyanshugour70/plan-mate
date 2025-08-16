// Path: app/src/main/java/in/syncboard/planmate/presentation/ui/screens/expense/ExpenseListScreen.kt

package `in`.syncboard.planmate.presentation.ui.screens.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.syncboard.planmate.presentation.ui.components.LoadingState
import `in`.syncboard.planmate.presentation.viewmodel.ExpenseViewModel
import `in`.syncboard.planmate.presentation.viewmodel.ExpenseItem
import `in`.syncboard.planmate.ui.theme.*
import java.util.Locale

/**
 * Expense List Screen - Updated to work with real data
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddExpense: () -> Unit,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }

    // Update search query in viewmodel when local state changes
    LaunchedEffect(searchQuery) {
        viewModel.searchExpenses(searchQuery)
    }

    // Show loading state
    if (uiState.isLoading) {
        LoadingState(message = "Loading expenses...")
        return
    }

    Scaffold(
        topBar = {
            if (showSearchBar) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { },
                    active = false,
                    onActiveChange = { },
                    placeholder = { Text("Search expenses...") },
                    leadingIcon = {
                        IconButton(onClick = {
                            showSearchBar = false
                            searchQuery = ""
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {}
            } else {
                TopAppBar(
                    title = {
                        Text(
                            "Expenses",
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showSearchBar = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = { /* Handle filter */ }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter")
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddExpense,
                containerColor = Primary500,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Summary Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Total Expenses Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = CategoryCardShape,
                        colors = CardDefaults.cardColors(
                            containerColor = Error50
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Total Expenses",
                                style = MaterialTheme.typography.bodySmall,
                                color = Error700,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "₹${String.format(Locale.getDefault(), "%,.0f", uiState.totalExpenses)}",
                                style = MaterialTheme.typography.amountMedium,
                                color = Error700,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(
                                text = if (searchQuery.isBlank()) "This month" else "Filtered",
                                style = MaterialTheme.typography.bodySmall,
                                color = Error600
                            )
                        }
                    }

                    // Average Daily Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = CategoryCardShape,
                        colors = CardDefaults.cardColors(
                            containerColor = Primary50
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Daily Average",
                                style = MaterialTheme.typography.bodySmall,
                                color = Primary700,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "₹${String.format(Locale.getDefault(), "%,.0f", uiState.averageDaily)}",
                                style = MaterialTheme.typography.amountMedium,
                                color = Primary700,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(
                                text = "Per day",
                                style = MaterialTheme.typography.bodySmall,
                                color = Primary600
                            )
                        }
                    }
                }
            }

            // Results Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (searchQuery.isBlank()) "All Expenses" else "Search Results",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "${uiState.filteredExpenses.size} transactions",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // No Results Message
            if (uiState.filteredExpenses.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = CategoryCardShape,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = if (searchQuery.isBlank()) Icons.Default.Receipt else Icons.Default.SearchOff,
                                contentDescription = "No results",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (searchQuery.isBlank()) "No expenses yet" else "No expenses found",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (searchQuery.isBlank())
                                    "Start tracking your expenses by adding your first transaction"
                                else
                                    "Try adjusting your search query",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            } else {
                // Expense List - Group by date
                val groupedExpenses = uiState.filteredExpenses.groupBy { expense ->
                    formatDateGroup(expense.date)
                }

                groupedExpenses.forEach { (dateGroup, expenses) ->
                    // Date Header
                    item {
                        Text(
                            text = dateGroup,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // Expenses for this date
                    items(expenses) { expense ->
                        ExpenseTransactionItem(
                            expense = expense,
                            onClick = { /* Handle expense detail view */ },
                            onDeleteClick = { viewModel.deleteExpense(expense.id) }
                        )
                    }
                }
            }

            // Error Message
            if (uiState.errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Error50
                        )
                    ) {
                        Text(
                            text = uiState.errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Error700,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
            }
        }
    }
}

/**
 * Enhanced Transaction Item for Expenses
 */
@Composable
private fun ExpenseTransactionItem(
    expense: ExpenseItem,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = NotificationShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = getCategoryColor(expense.category).copy(alpha = 0.1f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(expense.category),
                    contentDescription = expense.title,
                    tint = getCategoryColor(expense.category),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Transaction Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = expense.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${expense.category} • ${expense.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (expense.location != null) {
                    Text(
                        text = expense.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            // Amount and Actions
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = if (expense.isIncome) "+₹${String.format(Locale.getDefault(), "%,.0f", expense.amount)}"
                    else "-₹${String.format(Locale.getDefault(), "%,.0f", expense.amount)}",
                    style = MaterialTheme.typography.amountSmall,
                    color = if (expense.isIncome) IncomeGreen else ExpenseRed,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Expense") },
            text = { Text("Are you sure you want to delete this expense? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = Error500)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Helper functions
private fun formatDateGroup(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 24 * 60 * 60 * 1000 -> "Today"
        diff < 2 * 24 * 60 * 60 * 1000 -> "Yesterday"
        diff < 7 * 24 * 60 * 60 * 1000 -> {
            val date = java.util.Date(timestamp)
            java.text.SimpleDateFormat("EEEE", java.util.Locale.getDefault()).format(date)
        }
        else -> {
            val date = java.util.Date(timestamp)
            java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(date)
        }
    }
}

private fun getCategoryIcon(category: String): ImageVector {
    return when (category) {
        "Food & Dining" -> Icons.Default.Restaurant
        "Transportation" -> Icons.Default.DirectionsCar
        "Shopping" -> Icons.Default.ShoppingBag
        "Entertainment" -> Icons.Default.Movie
        "Health" -> Icons.Default.LocalHospital
        "Bills & Utilities" -> Icons.Default.Receipt
        "Education" -> Icons.Default.School
        "Travel" -> Icons.Default.Flight
        else -> Icons.Default.Receipt
    }
}

private fun getCategoryColor(category: String): Color {
    return when (category) {
        "Food & Dining" -> FoodColor
        "Transportation" -> TransportColor
        "Shopping" -> ShoppingColor
        "Entertainment" -> EntertainmentColor
        "Health" -> HealthColor
        "Bills & Utilities" -> BillsColor
        "Education" -> EducationColor
        "Travel" -> TravelColor
        else -> Primary500
    }
}
