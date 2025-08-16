// Path: app/src/main/java/in/syncboard/planmate/presentation/ui/screens/dashboard/DashboardScreen.kt
package `in`.syncboard.planmate.presentation.ui.screens.dashboard

import android.icu.util.Calendar
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import kotlinx.coroutines.delay
import `in`.syncboard.planmate.presentation.ui.components.*
import `in`.syncboard.planmate.presentation.viewmodel.DashboardViewModel
import `in`.syncboard.planmate.presentation.viewmodel.FinancialInsight
import `in`.syncboard.planmate.presentation.viewmodel.InsightType
import `in`.syncboard.planmate.presentation.viewmodel.CategorySpending
import `in`.syncboard.planmate.presentation.viewmodel.MonthlyComparison
import `in`.syncboard.planmate.ui.theme.*

/**
 * Enhanced Dashboard Screen - Fully scrollable with animations and carousels
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToBudget: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToAddExpense: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToReminders: () -> Unit,
    onNavigateToCategories: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    // Animation states
    var balanceVisible by remember { mutableStateOf(false) }
    val balanceAlpha by animateFloatAsState(
        targetValue = if (balanceVisible) 1f else 0f,
        animationSpec = tween(1000),
        label = "balance_alpha"
    )

    // Launch animations
    LaunchedEffect(Unit) {
        delay(300)
        balanceVisible = true
    }

    // Show loading state while data loads
    if (uiState.isLoading) {
        LoadingState(message = "Loading your financial overview...")
        return
    }

    Scaffold(
        bottomBar = {
            EnhancedNavigationBar(
                onNavigateToCategories = onNavigateToCategories,
                onNavigateToExpenses = onNavigateToExpenses,
                onNavigateToAddExpense = onNavigateToAddExpense,
                onNavigateToBudget = onNavigateToBudget,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Section (Now Scrollable!)
            item {
                AnimatedHeaderSection(
                    userName = uiState.userName,
                    balanceAlpha = balanceAlpha,
                    totalBalance = viewModel.getFormattedBalance(),
                    monthlyIncome = viewModel.getFormattedMonthlyIncome(),
                    monthlyExpense = viewModel.getFormattedMonthlyExpense(),
                    savingsRate = uiState.savingsRate,
                    budgetUsage = viewModel.getBudgetUsagePercentage(),
                    incomeComparison = uiState.incomeComparison,
                    expenseComparison = uiState.expenseComparison,
                    onNavigateToProfile = onNavigateToProfile,
                    onNavigateToReminders = onNavigateToReminders
                )
            }

            // Quick Stats Carousel
            item {
                AnimatedQuickStatsCarousel(
                    averageDaily = viewModel.getAverageDailySpending(),
                    daysLeft = uiState.daysUntilMonthEnd,
                    projected = viewModel.getProjectedMonthlySpending(),
                    savingsRate = uiState.savingsRate.toInt()
                )
            }

            // Financial Insights Carousel
            if (uiState.financialInsights.isNotEmpty()) {
                item {
                    Section(title = "Financial Insights") {
                        AnimatedInsightsCarousel(insights = uiState.financialInsights)
                    }
                }
            }

            // Budget Overview with Animation
            if (uiState.budgetOverview != null) {
                item {
                    Section(title = "Budget Overview") {
                        AnimatedBudgetCard(
                            budgetOverview = uiState.budgetOverview!!,
                            onViewDetails = onNavigateToBudget
                        )
                    }
                }
            }

            // Category Spending Carousel
            if (uiState.topSpendingCategories.isNotEmpty()) {
                item {
                    Section(
                        title = "Top Spending Categories",
                        action = "View All" to onNavigateToExpenses
                    ) {
                        AnimatedCategoryCarousel(
                            categories = uiState.topSpendingCategories,
                            totalExpense = uiState.monthlyExpense
                        )
                    }
                }
            }

            // Quick Actions Grid
            item {
                Section(title = "Quick Actions") {
                    AnimatedQuickActionsGrid(
                        onNavigateToAddExpense = onNavigateToAddExpense,
                        onNavigateToBudget = onNavigateToBudget,
                        onNavigateToCategories = onNavigateToCategories,
                        onNavigateToExpenses = onNavigateToExpenses
                    )
                }
            }

            // Recent Transactions
            item {
                Section(
                    title = "Recent Transactions",
                    subtitle = "${uiState.totalTransactionsThisMonth} transactions this month",
                    action = "View All" to onNavigateToExpenses
                ) {
                    if (uiState.recentTransactions.isEmpty()) {
                        EmptyTransactionsCard(onAddExpense = onNavigateToAddExpense)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            uiState.recentTransactions.take(5).forEachIndexed { index, transaction ->
                                AnimatedTransactionItem(
                                    transaction = transaction,
                                    index = index
                                )
                            }
                        }
                    }
                }
            }

            // Error handling
            if (uiState.errorMessage != null) {
                item {
                    ErrorCard(message = uiState.errorMessage)
                }
            }

            // Bottom spacing
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun AnimatedHeaderSection(
    userName: String,
    balanceAlpha: Float,
    totalBalance: String,
    monthlyIncome: String,
    monthlyExpense: String,
    savingsRate: Double,
    budgetUsage: Int,
    incomeComparison: MonthlyComparison?,
    expenseComparison: MonthlyComparison?,
    onNavigateToProfile: () -> Unit,
    onNavigateToReminders: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = CardLargeShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Primary500, Secondary500, Tertiary500)
                    ),
                    shape = CardLargeShape
                )
                .padding(20.dp)
        ) {
            Column {
                // Header Row
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
                            text = userName,
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
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        NotificationButton(onClick = onNavigateToReminders)
                        ProfileButton(onClick = onNavigateToProfile)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Balance Section with Animation
                androidx.compose.animation.AnimatedVisibility(
                    visible = balanceAlpha > 0f,
                    enter = slideInVertically() + fadeIn()
                ) {
                    EnhancedBalanceCard(
                        totalBalance = totalBalance,
                        monthlyIncome = monthlyIncome,
                        monthlyExpense = monthlyExpense,
                        savingsRate = savingsRate,
                        budgetUsage = budgetUsage,
                        incomeComparison = incomeComparison,
                        expenseComparison = expenseComparison
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedQuickStatsCarousel(
    averageDaily: String,
    daysLeft: Int,
    projected: String,
    savingsRate: Int
) {
    Section(title = "Quick Stats") {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items( // This is the items call for the LazyRow
                listOf(
                    QuickStatData("Daily Avg", averageDaily, "This month", Icons.Default.TrendingUp, Primary500),
                    QuickStatData("Days Left", "$daysLeft", "In month", Icons.Default.CalendarMonth, Secondary500),
                    QuickStatData("Projected", projected, "Month total", Icons.Default.Insights, Tertiary500),
                    QuickStatData("Savings", "$savingsRate%", "Of income", Icons.Default.Savings, IncomeGreen)
                )
            ) { stat -> // Corrected: stat is now QuickStatData
                // You might need to pass an index if AnimatedQuickStatCard requires it.
                // If it does, see Option 2.
                AnimatedQuickStatCard(stat = stat, index = 0) // Example if index is needed but not critical
            }
        }
    }
}

@Composable
private fun AnimatedInsightsCarousel(insights: List<FinancialInsight>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(insights.size) { index ->
            AnimatedInsightCard(insight = insights[index], index = index)
        }
    }
}

@Composable
private fun AnimatedCategoryCarousel(
    categories: List<CategorySpending>,
    totalExpense: Double
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(categories.size) { index ->
            AnimatedCategoryCard(
                category = categories[index],
                index = index
            )
        }
    }
}

@Composable
private fun AnimatedQuickActionsGrid(
    onNavigateToAddExpense: () -> Unit,
    onNavigateToBudget: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToExpenses: () -> Unit
) {
    val actions = listOf(
        QuickActionData("Add Expense", "Record spending", Icons.Default.Add, Primary500, onNavigateToAddExpense),
        QuickActionData("Manage Budget", "Set limits", Icons.Default.AccountBalance, Tertiary500, onNavigateToBudget),
        QuickActionData("Categories", "Organize expenses", Icons.Default.Category, Secondary500, onNavigateToCategories),
        QuickActionData("Analytics", "View reports", Icons.Default.Analytics, Warning500, onNavigateToExpenses)
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        actions.chunked(2).forEachIndexed { rowIndex, rowActions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowActions.forEachIndexed { colIndex, action ->
                    AnimatedQuickActionCard(
                        action = action,
                        index = rowIndex * 2 + colIndex,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// Data Classes
private data class QuickStatData(
    val title: String,
    val value: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color
)

private data class QuickActionData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

// Animated Components
@Composable
private fun AnimatedQuickStatCard(stat: QuickStatData, index: Int) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100L * index)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(300, delayMillis = index * 50)
        ) + fadeIn()
    ) {
        QuickStatCard(
            title = stat.title,
            value = stat.value,
            subtitle = stat.subtitle,
            icon = stat.icon,
            color = stat.color,
            modifier = Modifier.width(140.dp)
        )
    }
}

@Composable
private fun AnimatedInsightCard(insight: FinancialInsight, index: Int) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200L * index)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(400, delayMillis = index * 100)
        ) + fadeIn()
    ) {
        InsightCard(insight = insight)
    }
}

@Composable
private fun AnimatedCategoryCard(category: CategorySpending, index: Int) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(150L * index)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            animationSpec = tween(300, delayMillis = index * 75)
        ) + fadeIn()
    ) {
        Card(
            modifier = Modifier.width(160.dp),
            shape = CategoryCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = category.categoryIcon,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = category.categoryName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "₹${String.format("%,.0f", category.totalAmount)}",
                    style = MaterialTheme.typography.amountSmall,
                    color = ExpenseRed,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${category.percentage.toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AnimatedQuickActionCard(
    action: QuickActionData,
    index: Int,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(250L * index)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            animationSpec = tween(400, delayMillis = index * 100)
        ) + fadeIn()
    ) {
        QuickActionButton(
            icon = action.icon,
            title = action.title,
            subtitle = action.subtitle,
            backgroundColor = action.color,
            onClick = action.onClick,
            modifier = modifier
        )
    }
}

@Composable
private fun AnimatedTransactionItem(
    transaction: `in`.syncboard.planmate.presentation.viewmodel.TransactionItem,
    index: Int
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100L * index)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(300, delayMillis = index * 50)
        ) + fadeIn()
    ) {
        EnhancedTransactionItem(transaction = transaction)
    }
}

@Composable
private fun AnimatedBudgetCard(
    budgetOverview: `in`.syncboard.planmate.presentation.viewmodel.BudgetOverview,
    onViewDetails: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically() + fadeIn()
    ) {
        BudgetOverviewCard(
            budgetOverview = budgetOverview,
            onViewDetails = onViewDetails
        )
    }
}

// Enhanced Navigation Bar
@Composable
private fun EnhancedNavigationBar(
    onNavigateToCategories: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToAddExpense: () -> Unit,
    onNavigateToBudget: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
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
            icon = { Icon(Icons.Default.Category, contentDescription = "Categories") },
            label = { Text("Categories") },
            selected = false,
            onClick = onNavigateToCategories
        )
        NavigationBarItem(
            icon = {
                FloatingActionButton(
                    onClick = onNavigateToAddExpense,
                    modifier = Modifier.size(40.dp),
                    containerColor = Primary500,
                    contentColor = Color.White
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
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

// Helper Components
@Composable
private fun Section(
    title: String,
    subtitle: String? = null,
    action: Pair<String, () -> Unit>? = null,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            action?.let { (text, onClick) ->
                TextButton(onClick = onClick) {
                    Text(text)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun NotificationButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Box {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = Color.White
            )
            // Notification badge
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Error500, CircleShape)
                    .offset(x = 6.dp, y = (-2).dp)
            )
        }
    }
}

@Composable
private fun ProfileButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile",
            tint = Color.White
        )
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
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
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Error700
            )
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