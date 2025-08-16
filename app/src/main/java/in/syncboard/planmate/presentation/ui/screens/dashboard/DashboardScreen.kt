// Path: app/src/main/java/in/syncboard/planmate/presentation/ui/screens/dashboard/DashboardScreen.kt
package `in`.syncboard.planmate.presentation.ui.screens.dashboard

import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.syncboard.planmate.presentation.ui.components.*
import `in`.syncboard.planmate.presentation.viewmodel.DashboardViewModel
import `in`.syncboard.planmate.presentation.viewmodel.FinancialInsight
import `in`.syncboard.planmate.presentation.viewmodel.InsightType
import `in`.syncboard.planmate.presentation.viewmodel.CategorySpending
import `in`.syncboard.planmate.presentation.viewmodel.MonthlyComparison
import `in`.syncboard.planmate.ui.theme.*

/**
 * Enhanced Dashboard Screen - Comprehensive financial overview
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
        LoadingState(message = "Loading your financial overview...")
        return
    }

    Scaffold(
        topBar = {
            // Enhanced Header with gradients and animations
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = TopBarShape,
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Primary500, Secondary500, Tertiary500)
                            ),
                            shape = TopBarShape
                        )
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Column {
                        // Greeting and User Info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = getGreeting(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Text(
                                    text = uiState.userName,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Here's your financial summary",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Notification indicator with badge
                                Box {
                                    IconButton(onClick = onNavigateToReminders) {
                                        Icon(
                                            imageVector = Icons.Default.Notifications,
                                            contentDescription = "Notifications",
                                            tint = Color.White
                                        )
                                    }
                                    if (uiState.financialInsights.any { it.actionRequired }) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(Error500, CircleShape)
                                                .offset(x = 4.dp, y = 4.dp)
                                        )
                                    }
                                }

                                IconButton(onClick = onNavigateToProfile) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Profile",
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Enhanced Balance Overview
                        EnhancedBalanceCard(
                            totalBalance = viewModel.getFormattedBalance(),
                            monthlyIncome = viewModel.getFormattedMonthlyIncome(),
                            monthlyExpense = viewModel.getFormattedMonthlyExpense(),
                            savingsRate = uiState.savingsRate,
                            budgetUsage = viewModel.getBudgetUsagePercentage(),
                            incomeComparison = uiState.incomeComparison,
                            expenseComparison = uiState.expenseComparison
                        )
                    }
                }
            }
        },
        bottomBar = {
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
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Primary500, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    label = { Text("Add") },
                    selected = false,
                    onClick = onNavigateToAddExpense
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AccountBalance, contentDescription = "Budget") },
                    label = { Text("Budget") },
                    selected = false,
                    onClick = onNavigateToBudget
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
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Financial Insights Section
            if (uiState.financialInsights.isNotEmpty()) {
                item {
                    Text(
                        text = "Financial Insights",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(uiState.financialInsights) { insight ->
                            InsightCard(insight = insight)
                        }
                    }
                }
            }

            // Quick Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickStatCard(
                        title = "Daily Avg",
                        value = viewModel.getAverageDailySpending(),
                        subtitle = "This month",
                        icon = Icons.Default.TrendingUp,
                        color = Primary500,
                        modifier = Modifier.weight(1f)
                    )
                    QuickStatCard(
                        title = "Days Left",
                        value = "${uiState.daysUntilMonthEnd}",
                        subtitle = "In month",
                        icon = Icons.Default.CalendarMonth,
                        color = Secondary500,
                        modifier = Modifier.weight(1f)
                    )
                    QuickStatCard(
                        title = "Projected",
                        value = viewModel.getProjectedMonthlySpending(),
                        subtitle = "Month total",
                        icon = Icons.Default.Insights,
                        color = Tertiary500,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Budget Overview Section
            if (uiState.budgetOverview != null) {
                item {
                    Text(
                        text = "Budget Overview",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                item {
                    BudgetOverviewCard(
                        budgetOverview = uiState.budgetOverview!!,
                        onViewDetails = onNavigateToBudget
                    )
                }
            }

            // Category Analytics Section
            if (uiState.topSpendingCategories.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Spending by Category",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                        TextButton(onClick = onNavigateToExpenses) {
                            Text("View All")
                        }
                    }
                }

                item {
                    CategorySpendingSection(
                        categories = uiState.topSpendingCategories,
                        totalExpense = uiState.monthlyExpense
                    )
                }
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
                        subtitle = "Record spending",
                        backgroundColor = Primary500,
                        onClick = onNavigateToAddExpense,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionButton(
                        icon = Icons.Default.AccountBalance,
                        title = "Manage Budget",
                        subtitle = "Set limits",
                        backgroundColor = Tertiary500,
                        onClick = onNavigateToBudget,
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
                    Column {
                        Text(
                            text = "Recent Transactions",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${uiState.totalTransactionsThisMonth} transactions this month",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = onNavigateToExpenses) {
                        Text("View All")
                    }
                }
            }

            // Transaction List
            if (uiState.recentTransactions.isEmpty()) {
                item {
                    EmptyTransactionsCard(onAddExpense = onNavigateToAddExpense)
                }
            } else {
                items(uiState.recentTransactions.take(5)) { transaction ->
                    EnhancedTransactionItem(transaction = transaction)
                }
            }

            // Error Message
            if (uiState.errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Error50)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = "Error",
                                tint = Error500
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = uiState.errorMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Error700
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun EnhancedBalanceCard(
    totalBalance: String,
    monthlyIncome: String,
    monthlyExpense: String,
    savingsRate: Double,
    budgetUsage: Int,
    incomeComparison: MonthlyComparison?,
    expenseComparison: MonthlyComparison?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CardLargeShape,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Current Balance
            Text(
                text = "Current Balance",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = totalBalance,
                style = MaterialTheme.typography.displaySmall,
                color = if (totalBalance.contains("-")) Error600 else IncomeGreen,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Income and Expense Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Income Column
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.TrendingUp,
                            contentDescription = "Income",
                            tint = IncomeGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Income",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = monthlyIncome,
                        style = MaterialTheme.typography.amountMedium,
                        color = IncomeGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                    incomeComparison?.let { comparison ->
                        Text(
                            text = "${if (comparison.isIncrease) "+" else "-"}${comparison.percentageChange.toInt()}% vs last month",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (comparison.isIncrease) IncomeGreen else Error500
                        )
                    }
                }

                // Expense Column
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.TrendingDown,
                            contentDescription = "Expense",
                            tint = ExpenseRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Expenses",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = monthlyExpense,
                        style = MaterialTheme.typography.amountMedium,
                        color = ExpenseRed,
                        fontWeight = FontWeight.SemiBold
                    )
                    expenseComparison?.let { comparison ->
                        Text(
                            text = "${if (comparison.isIncrease) "+" else "-"}${comparison.percentageChange.toInt()}% vs last month",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (comparison.isIncrease) Error500 else IncomeGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Savings Rate
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Savings Rate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${savingsRate.toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = when {
                            savingsRate > 20 -> IncomeGreen
                            savingsRate > 10 -> Warning500
                            else -> Error500
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Budget Usage
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Budget Used",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$budgetUsage%",
                        style = MaterialTheme.typography.titleMedium,
                        color = when {
                            budgetUsage > 90 -> Error500
                            budgetUsage > 75 -> Warning500
                            else -> IncomeGreen
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun InsightCard(insight: FinancialInsight) {
    val backgroundColor = when (insight.type) {
        InsightType.POSITIVE -> Tertiary50
        InsightType.WARNING -> Warning50
        InsightType.CRITICAL -> Error50
        InsightType.INFO -> Primary50
    }

    val iconColor = when (insight.type) {
        InsightType.POSITIVE -> Tertiary600
        InsightType.WARNING -> Warning600
        InsightType.CRITICAL -> Error600
        InsightType.INFO -> Primary600
    }

    val icon = when (insight.type) {
        InsightType.POSITIVE -> Icons.Default.CheckCircle
        InsightType.WARNING -> Icons.Default.Warning
        InsightType.CRITICAL -> Icons.Default.Error
        InsightType.INFO -> Icons.Default.Info
    }

    Card(
        modifier = Modifier.width(280.dp),
        shape = CategoryCardShape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = insight.title,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = insight.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = iconColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = insight.message,
                style = MaterialTheme.typography.bodySmall,
                color = iconColor.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun QuickStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = CategoryCardShape,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = color,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = color.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun BudgetOverviewCard(
    budgetOverview: `in`.syncboard.planmate.presentation.viewmodel.BudgetOverview,
    onViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CategoryCardShape,
        colors = CardDefaults.cardColors(
            containerColor = if (budgetOverview.isOverBudget) Error50 else Primary50
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Monthly Budget",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (budgetOverview.isOverBudget) Error700 else Primary700,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "₹${String.format("%,.0f", budgetOverview.totalSpent)} of ₹${String.format("%,.0f", budgetOverview.totalBudget)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (budgetOverview.isOverBudget) Error600 else Primary600
                    )
                }
                TextButton(onClick = onViewDetails) {
                    Text("View Details")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            val progressColor = when {
                budgetOverview.usagePercentage > 100 -> Error500
                budgetOverview.usagePercentage > 90 -> Warning500
                budgetOverview.usagePercentage > 75 -> Warning400
                else -> Primary500
            }

            LinearProgressIndicator(
                progress = (budgetOverview.usagePercentage / 100).toFloat().coerceAtMost(1f),
                modifier = Modifier.fillMaxWidth(),
                color = progressColor,
                trackColor = progressColor.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${budgetOverview.usagePercentage.toInt()}% used",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (budgetOverview.isOverBudget) Error600 else Primary600
                )
                Text(
                    text = if (budgetOverview.isOverBudget)
                        "Over by ₹${String.format("%,.0f", budgetOverview.totalSpent - budgetOverview.totalBudget)}"
                    else
                        "₹${String.format("%,.0f", budgetOverview.remainingBudget)} remaining",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (budgetOverview.isOverBudget) Error600 else Primary600
                )
            }
        }
    }
}

@Composable
private fun CategorySpendingSection(
    categories: List<CategorySpending>,
    totalExpense: Double
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            CategorySpendingItem(
                category = category,
                totalExpense = totalExpense
            )
        }
    }
}

@Composable
private fun CategorySpendingItem(
    category: CategorySpending,
    totalExpense: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CategoryCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = parseColor(category.categoryColor).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.categoryIcon,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.categoryName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${category.percentage.toInt()}% of total expenses",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "₹${String.format("%,.0f", category.totalAmount)}",
                style = MaterialTheme.typography.titleMedium,
                color = ExpenseRed,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun EnhancedTransactionItem(
    transaction: `in`.syncboard.planmate.presentation.viewmodel.TransactionItem
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = NotificationShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = parseColor(transaction.categoryColor).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = transaction.categoryIcon,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${transaction.category} • ${transaction.date} ${transaction.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                transaction.location?.let { location ->
                    Text(
                        text = location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            Text(
                text = if (transaction.isIncome) "+₹${String.format("%,.0f", transaction.amount)}"
                else "-₹${String.format("%,.0f", transaction.amount)}",
                style = MaterialTheme.typography.titleMedium,
                color = if (transaction.isIncome) IncomeGreen else ExpenseRed,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun EmptyTransactionsCard(onAddExpense: () -> Unit) {
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
                imageVector = Icons.Default.Receipt,
                contentDescription = "No transactions",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No transactions yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Start tracking your expenses and income",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onAddExpense) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Transaction")
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = CategoryCardShape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor.copy(alpha = 0.1f)),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(backgroundColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = backgroundColor,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = backgroundColor.copy(alpha = 0.7f)
            )
        }
    }
}

private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
}

private fun parseColor(colorString: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorString))
    } catch (e: Exception) {
        Primary500
    }
}