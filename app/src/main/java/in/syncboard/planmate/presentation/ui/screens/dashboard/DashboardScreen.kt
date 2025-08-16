package `in`.syncboard.planmate.presentation.ui.screens.dashboard

import androidx.compose.foundation.background
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
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.syncboard.planmate.presentation.ui.components.*
import `in`.syncboard.planmate.presentation.viewmodel.DashboardViewModel
import `in`.syncboard.planmate.ui.theme.*

/**
 * Dashboard Screen - Main screen after login
 * Shows user's financial overview, quick actions, and recent transactions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToBudget: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToAddExpense: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToReminders: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    // Show loading state while data loads
    if (uiState.isLoading) {
        LoadingState(message = "Loading your dashboard...")
        return
    }

    // Main Content
    Scaffold(
        topBar = {
            // Custom Header (not using TopAppBar for custom design)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = TopBarShape,
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                colors = listOf(Primary500, Secondary500)
                            ),
                            shape = TopBarShape
                        )
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Column {
                        // Top Row - Greeting and Notifications
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Good Morning,",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = uiState.userName,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Row {
                                IconButton(
                                    onClick = onNavigateToReminders
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Notifications",
                                        tint = Color.White
                                    )
                                }

                                IconButton(
                                    onClick = onNavigateToProfile
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Profile",
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Balance Card
                        BalanceCard(
                            totalBalance = viewModel.getFormattedBalance(),
                            monthlyIncome = viewModel.getFormattedMonthlyIncome(),
                            monthlyExpense = viewModel.getFormattedMonthlyExpense()
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Bottom Navigation
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = true,
                    onClick = { /* Already on home */ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Analytics, contentDescription = "Analytics") },
                    label = { Text("Analytics") },
                    selected = false,
                    onClick = onNavigateToExpenses
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                    label = { Text("Add") },
                    selected = false,
                    onClick = onNavigateToAddExpense
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Alerts") },
                    label = { Text("Alerts") },
                    selected = false,
                    onClick = onNavigateToReminders
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = false,
                    onClick = onNavigateToProfile
                )
            }
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

            // Quick Actions Section
            item {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionButton(
                        icon = Icons.Default.Add,
                        title = "Add Expense",
                        backgroundColor = Primary500,
                        onClick = onNavigateToAddExpense,
                        modifier = Modifier.weight(1f)
                    )

                    QuickActionButton(
                        icon = Icons.Default.AccountBalance,
                        title = "Set Budget",
                        backgroundColor = Tertiary500,
                        onClick = onNavigateToBudget,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionButton(
                        icon = Icons.Default.Analytics,
                        title = "View Reports",
                        backgroundColor = Secondary500,
                        onClick = onNavigateToExpenses,
                        modifier = Modifier.weight(1f)
                    )

                    QuickActionButton(
                        icon = Icons.Default.Notifications,
                        title = "Reminders",
                        backgroundColor = Warning500,
                        onClick = onNavigateToReminders,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Recent Transactions Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )

                    TextButton(
                        onClick = onNavigateToExpenses
                    ) {
                        Text(
                            text = "View All",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Primary500,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Transaction List
            items(uiState.recentTransactions.take(5)) { transaction ->
                TransactionItem(
                    title = transaction.title,
                    subtitle = "${transaction.category} • ${transaction.date}",
                    amount = "₹${String.format("%,.0f", transaction.amount)}",
                    isIncome = transaction.isIncome,
                    icon = when (transaction.category) {
                        "Food & Dining" -> Icons.Default.Restaurant
                        "Transportation" -> Icons.Default.DirectionsCar
                        "Shopping" -> Icons.Default.ShoppingBag
                        "Entertainment" -> Icons.Default.Movie
                        "Income" -> Icons.Default.AccountBalance
                        else -> Icons.Default.Receipt
                    },
                    categoryColor = when (transaction.category) {
                        "Food & Dining" -> FoodColor
                        "Transportation" -> TransportColor
                        "Shopping" -> ShoppingColor
                        "Entertainment" -> EntertainmentColor
                        "Income" -> IncomeGreen
                        else -> Primary500
                    }
                )
            }

            // Financial Insights Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CategoryCardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Tertiary50
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Insight",
                            tint = Tertiary700,
                            modifier = Modifier.size(32.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Great Progress!",
                                style = MaterialTheme.typography.titleMedium,
                                color = Tertiary700,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "You're saving ${viewModel.getSavingsPercentage()}% of your income this month. Keep it up!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Tertiary600
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}