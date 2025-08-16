package `in`.syncboard.planmate.presentation.ui.screens.expense

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import `in`.syncboard.planmate.presentation.ui.components.TransactionItem
import `in`.syncboard.planmate.presentation.viewmodel.Transaction
import `in`.syncboard.planmate.ui.theme.*

/**
 * Expense List Screen
 * Shows all expenses with search and filter functionality
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddExpense: () -> Unit
) {
    // Search state
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }

    // Mock expense data
    val allExpenses = remember {
        listOf(
            Transaction("1", "Morning Coffee", 450.0, "Food & Dining", "Today, 9:30 AM", false),
            Transaction("2", "Uber Ride", 280.0, "Transportation", "Today, 8:15 AM", false),
            Transaction("3", "Grocery Shopping", 2340.0, "Shopping", "Yesterday, 6:45 PM", false),
            Transaction("4", "Movie Tickets", 800.0, "Entertainment", "Yesterday, 7:30 PM", false),
            Transaction("5", "Pharmacy", 650.0, "Health", "2 days ago, 2:15 PM", false),
            Transaction("6", "Restaurant Dinner", 1850.0, "Food & Dining", "2 days ago, 8:00 PM", false),
            Transaction("7", "Petrol", 3500.0, "Transportation", "3 days ago, 4:30 PM", false),
            Transaction("8", "Online Shopping", 4200.0, "Shopping", "3 days ago, 11:00 AM", false),
            Transaction("9", "Gym Membership", 2000.0, "Health", "4 days ago, 10:00 AM", false),
            Transaction("10", "Electricity Bill", 1800.0, "Bills & Utilities", "5 days ago, 3:00 PM", false),
            Transaction("11", "Spotify Subscription", 179.0, "Entertainment", "6 days ago, 12:00 PM", false),
            Transaction("12", "Book Purchase", 899.0, "Education", "1 week ago, 5:00 PM", false)
        )
    }

    // Filter expenses based on search query
    val filteredExpenses = remember(searchQuery, allExpenses) {
        if (searchQuery.isBlank()) {
            allExpenses
        } else {
            allExpenses.filter { expense ->
                expense.title.contains(searchQuery, ignoreCase = true) ||
                        expense.category.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Calculate totals
    val totalExpenses = filteredExpenses.sumOf { it.amount }
    val avgDaily = totalExpenses / 7 // Assuming 7 days

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
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                                text = "₹${String.format("%,.0f", totalExpenses)}",
                                style = MaterialTheme.typography.amountMedium,
                                color = Error700,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(
                                text = "${if (searchQuery.isBlank()) "This month" else "Filtered"}",
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
                                text = "₹${String.format("%,.0f", avgDaily)}",
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
                        text = "${filteredExpenses.size} transactions",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // No Results Message
            if (filteredExpenses.isEmpty()) {
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
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = "No results",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No expenses found",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Try adjusting your search query",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            } else {
                // Expense List
                items(filteredExpenses) { expense ->
                    TransactionItem(
                        title = expense.title,
                        subtitle = "${expense.category} • ${expense.date}",
                        amount = "₹${String.format("%,.0f", expense.amount)}",
                        isIncome = expense.isIncome,
                        icon = when (expense.category) {
                            "Food & Dining" -> Icons.Default.Restaurant
                            "Transportation" -> Icons.Default.DirectionsCar
                            "Shopping" -> Icons.Default.ShoppingBag
                            "Entertainment" -> Icons.Default.Movie
                            "Health" -> Icons.Default.LocalHospital
                            "Bills & Utilities" -> Icons.Default.Receipt
                            "Education" -> Icons.Default.School
                            else -> Icons.Default.Receipt
                        },
                        categoryColor = when (expense.category) {
                            "Food & Dining" -> FoodColor
                            "Transportation" -> TransportColor
                            "Shopping" -> ShoppingColor
                            "Entertainment" -> EntertainmentColor
                            "Health" -> HealthColor
                            "Bills & Utilities" -> BillsColor
                            "Education" -> EducationColor
                            else -> Primary500
                        },
                        onClick = { /* Handle expense detail view */ }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
            }
        }
    }
}