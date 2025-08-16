// ExpenseListScreen.kt
package `in`.syncboard.planmate.presentation.ui.screens.expense

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.syncboard.planmate.presentation.ui.components.LoadingState
import `in`.syncboard.planmate.presentation.ui.components.PlanMateFAB
import `in`.syncboard.planmate.presentation.viewmodel.ExpenseViewModel
import `in`.syncboard.planmate.presentation.viewmodel.ExpenseItem
import `in`.syncboard.planmate.ui.theme.*
import java.util.Locale

/**
 * Expense List Screen with Modern UI
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddExpense: () -> Unit,
    onNavigateToAddReminder: () -> Unit = {},
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        SurfaceContainer.copy(alpha = 0.5f)
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                if (showSearchBar) {
                    SearchTopBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onBackClick = {
                            showSearchBar = false
                            searchQuery = ""
                        }
                    )
                } else {
                    ExpenseTopBar(
                        onNavigateBack = onNavigateBack,
                        onSearchClick = { showSearchBar = true }
                    )
                }
            },
            floatingActionButton = {
                PlanMateFAB(
                    onAddExpense = onNavigateToAddExpense,
                    onAddReminder = onNavigateToAddReminder
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Summary Cards
                item {
                    ExpenseSummaryCards(
                        totalExpenses = uiState.totalExpenses,
                        averageDaily = uiState.averageDaily,
                        searchQuery = searchQuery
                    )
                }

                // Results Header
                item {
                    ExpenseResultsHeader(
                        searchQuery = searchQuery,
                        resultsCount = uiState.filteredExpenses.size
                    )
                }

                // Expense List
                if (uiState.filteredExpenses.isEmpty()) {
                    item {
                        ExpenseEmptyState(
                            searchQuery = searchQuery,
                            onAddExpense = onNavigateToAddExpense
                        )
                    }
                } else {
                    val groupedExpenses = uiState.filteredExpenses.groupBy { expense ->
                        formatDateGroup(expense.date)
                    }

                    groupedExpenses.forEach { (dateGroup, expenses) ->
                        // Date Header
                        item {
                            ExpenseDateHeader(dateGroup = dateGroup)
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
                        ExpenseErrorCard(message = uiState.errorMessage)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpenseTopBar(
    onNavigateBack: () -> Unit,
    onSearchClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        // Background with gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Primary500, Secondary500)
                    )
                )
        )

        // Content
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Expenses",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Row {
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { /* Handle filter */ },
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        SearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = { },
            active = false,
            onActiveChange = { },
            placeholder = { Text("Search expenses...") },
            leadingIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {}
    }
}

@Composable
private fun ExpenseSummaryCards(
    totalExpenses: Double,
    averageDaily: Double,
    searchQuery: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Total Expenses Card
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Error400, Error600)
                        )
                    )
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "₹${String.format(Locale.getDefault(), "%,.0f", totalExpenses)}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Total Expenses",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = if (searchQuery.isBlank()) "This month" else "Filtered",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // Average Daily Card
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Primary400, Primary600)
                        )
                    )
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "₹${String.format(Locale.getDefault(), "%,.0f", averageDaily)}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Daily Average",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "Per day",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpenseResultsHeader(
    searchQuery: String,
    resultsCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (searchQuery.isBlank()) "All Expenses" else "Search Results",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Primary100
        ) {
            Text(
                text = "$resultsCount transactions",
                style = MaterialTheme.typography.bodyMedium,
                color = Primary700,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun ExpenseDateHeader(dateGroup: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Tertiary50
    ) {
        Text(
            text = dateGroup,
            style = MaterialTheme.typography.titleMedium,
            color = Tertiary700,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun ExpenseEmptyState(
    searchQuery: String,
    onAddExpense: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .blur(16.dp)
                        .background(
                            if (searchQuery.isBlank()) Primary500.copy(alpha = 0.3f)
                            else Secondary500.copy(alpha = 0.3f),
                            androidx.compose.foundation.shape.CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = if (searchQuery.isBlank()) listOf(Primary100, Primary50)
                                else listOf(Secondary100, Secondary50)
                            ),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                        .zIndex(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (searchQuery.isBlank()) Icons.Default.Receipt else Icons.Default.SearchOff,
                        contentDescription = "No results",
                        modifier = Modifier.size(40.dp),
                        tint = if (searchQuery.isBlank()) Primary600 else Secondary600
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = if (searchQuery.isBlank()) "No expenses yet" else "No expenses found",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (searchQuery.isBlank())
                    "Start tracking your expenses by adding your first transaction"
                else
                    "Try adjusting your search query",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            if (searchQuery.isBlank()) {
                Spacer(modifier = Modifier.height(24.dp))

                `in`.syncboard.planmate.presentation.ui.components.GradientButton(
                    text = "Add Expense",
                    onClick = onAddExpense,
                    icon = Icons.Default.Add,
                    modifier = Modifier.width(160.dp)
                )
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon with glow
            Box {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .blur(4.dp)
                        .background(
                            color = getCategoryColor(expense.category).copy(alpha = 0.3f),
                            shape = RoundedCornerShape(14.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    getCategoryColor(expense.category).copy(alpha = 0.2f),
                                    getCategoryColor(expense.category).copy(alpha = 0.1f)
                                )
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = getCategoryColor(expense.category).copy(alpha = 0.3f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .zIndex(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIcon(expense.category),
                        contentDescription = expense.title,
                        tint = getCategoryColor(expense.category),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Transaction Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = expense.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${expense.category} • ${expense.time}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (expense.location != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = expense.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Amount and Actions
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = if (expense.isIncome) "+₹${String.format(Locale.getDefault(), "%,.0f", expense.amount)}"
                    else "-₹${String.format(Locale.getDefault(), "%,.0f", expense.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (expense.isIncome) Success500 else Error500,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (expense.isIncome) Success50 else Error50
                ) {
                    Text(
                        text = if (expense.isIncome) "Income" else "Expense",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (expense.isIncome) Success700 else Error700,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(16.dp),
                        tint = Error500
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

@Composable
private fun ExpenseErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Error50)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = "Error",
                tint = Error500,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Error700
            )
        }
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