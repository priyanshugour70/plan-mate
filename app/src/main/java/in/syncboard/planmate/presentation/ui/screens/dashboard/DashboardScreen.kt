// Path: app/src/main/java/in/syncboard/planmate/presentation/ui/screens/dashboard/DashboardScreen.kt
package `in`.syncboard.planmate.presentation.ui.screens.dashboard

import android.icu.util.Calendar
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.graphicsLayer
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
import kotlin.random.Random

/**
 * 🌟 ULTIMATE DASHBOARD EXPERIENCE 🌟
 * Multiple carousels, financial advice, seasonal themes, animations & more!
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

    // 🎨 Seasonal Theme Detection
    val currentSeason = getCurrentSeason()
    val seasonalColors = getSeasonalColors(currentSeason)

    // 🎯 Animation States
    var isContentVisible by remember { mutableStateOf(false) }
    var currentCarouselIndex by remember { mutableStateOf(0) }
    var showFinancialTip by remember { mutableStateOf(false) }

    // 🚀 Auto-rotate carousels and tips
    LaunchedEffect(Unit) {
        delay(500)
        isContentVisible = true
        while (true) {
            delay(4000)
            currentCarouselIndex = (currentCarouselIndex + 1) % 5
            showFinancialTip = !showFinancialTip
        }
    }

    if (uiState.isLoading) {
        MagicalLoadingScreen()
        return
    }

    Scaffold(
        bottomBar = {
            MagicalNavigationBar(
                onNavigateToCategories = onNavigateToCategories,
                onNavigateToExpenses = onNavigateToExpenses,
                onNavigateToAddExpense = onNavigateToAddExpense,
                onNavigateToBudget = onNavigateToBudget,
                onNavigateToProfile = onNavigateToProfile,
                currentSeason = currentSeason
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 🌈 MAGICAL HERO SECTION with Seasonal Theme
            item {
                MagicalHeroSection(
                    uiState = uiState,
                    viewModel = viewModel,
                    seasonalColors = seasonalColors,
                    currentSeason = currentSeason,
                    isVisible = isContentVisible,
                    onNavigateToProfile = onNavigateToProfile,
                    onNavigateToReminders = onNavigateToReminders
                )
            }

            // 🎊 ACHIEVEMENT BADGES CAROUSEL
            item {
                AchievementBadgesCarousel(
                    savingsRate = uiState.savingsRate,
                    budgetUsage = viewModel.getBudgetUsagePercentage(),
                    transactionCount = uiState.totalTransactionsThisMonth,
                    isVisible = isContentVisible
                )
            }

            // 🧠 FINANCIAL WISDOM CAROUSEL
            item {
                FinancialWisdomCarousel(
                    currentBalance = uiState.totalBalance,
                    savingsRate = uiState.savingsRate,
                    monthlyExpense = uiState.monthlyExpense,
                    showTip = showFinancialTip,
                    season = currentSeason
                )
            }

            // 📊 DYNAMIC STATS DASHBOARD
            item {
                DynamicStatsCarousel(
                    averageDaily = viewModel.getAverageDailySpending(),
                    daysLeft = uiState.daysUntilMonthEnd,
                    projected = viewModel.getProjectedMonthlySpending(),
                    savingsRate = uiState.savingsRate.toInt(),
                    seasonalColors = seasonalColors,
                    isVisible = isContentVisible
                )
            }

            // 🎭 MOOD-BASED FINANCIAL INSIGHTS
            if (uiState.financialInsights.isNotEmpty()) {
                item {
                    MoodBasedInsightsCarousel(
                        insights = uiState.financialInsights,
                        currentMood = getFinancialMood(uiState.savingsRate, viewModel.getBudgetUsagePercentage())
                    )
                }
            }

            // 🏆 SMART CATEGORY ANALYTICS
            if (uiState.topSpendingCategories.isNotEmpty()) {
                item {
                    SmartCategoryAnalytics(
                        categories = uiState.topSpendingCategories,
                        totalExpense = uiState.monthlyExpense,
                        onViewAll = onNavigateToExpenses,
                        seasonalColors = seasonalColors
                    )
                }
            }

            // 💎 BUDGET PULSE MONITOR
            if (uiState.budgetOverview != null) {
                item {
                    BudgetPulseMonitor(
                        budgetOverview = uiState.budgetOverview!!,
                        onViewDetails = onNavigateToBudget,
                        pulseAnimation = currentCarouselIndex % 2 == 0
                    )
                }
            }

            // 🚀 SMART ACTION CENTER
            item {
                SmartActionCenter(
                    onNavigateToAddExpense = onNavigateToAddExpense,
                    onNavigateToBudget = onNavigateToBudget,
                    onNavigateToCategories = onNavigateToCategories,
                    onNavigateToExpenses = onNavigateToExpenses,
                    seasonalColors = seasonalColors,
                    urgentActions = getUrgentActions(uiState)
                )
            }

            // 🎬 TRANSACTION STORIES
            item {
                TransactionStoriesCarousel(
                    transactions = uiState.recentTransactions,
                    totalTransactions = uiState.totalTransactionsThisMonth,
                    onViewAll = onNavigateToExpenses,
                    onAddTransaction = onNavigateToAddExpense,
                    currentIndex = currentCarouselIndex
                )
            }

            // 🎯 MONTHLY COMPARISON SHOWCASE
            if (uiState.incomeComparison != null && uiState.expenseComparison != null) {
                item {
                    MonthlyComparisonShowcase(
                        incomeComparison = uiState.incomeComparison!!,
                        expenseComparison = uiState.expenseComparison!!,
                        seasonalColors = seasonalColors
                    )
                }
            }

            // 🌟 SEASONAL SAVING CHALLENGES
            item {
                SeasonalSavingChallenges(
                    currentSeason = currentSeason,
                    currentSavings = uiState.savingsRate,
                    seasonalColors = seasonalColors
                )
            }

            // 🎨 DAILY INSPIRATION CARD
            item {
                DailyInspirationCard(
                    day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR),
                    userName = uiState.userName,
                    seasonalColors = seasonalColors
                )
            }

            // Bottom spacing
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

// 🌈 Seasonal Theme System
@Composable
private fun getCurrentSeason(): Season {
    val month = Calendar.getInstance().get(Calendar.MONTH)
    return when (month) {
        11, 0, 1 -> Season.WINTER    // Dec, Jan, Feb
        2, 3, 4 -> Season.SPRING     // Mar, Apr, May
        5, 6, 7 -> Season.SUMMER     // Jun, Jul, Aug
        else -> Season.AUTUMN        // Sep, Oct, Nov
    }
}

private enum class Season { SPRING, SUMMER, AUTUMN, WINTER }

@Composable
private fun getSeasonalColors(season: Season): SeasonalColorScheme {
    return when (season) {
        Season.SPRING -> SeasonalColorScheme(
            primary = Color(0xFF66BB6A),
            secondary = Color(0xFF81C784),
            accent = Color(0xFF4CAF50),
            background = Color(0xFFE8F5E8)
        )
        Season.SUMMER -> SeasonalColorScheme(
            primary = Color(0xFFFFB74D),
            secondary = Color(0xFFFFCC02),
            accent = Color(0xFFFF9800),
            background = Color(0xFFFFF8E1)
        )
        Season.AUTUMN -> SeasonalColorScheme(
            primary = Color(0xFFD84315),
            secondary = Color(0xFFFF7043),
            accent = Color(0xFFBF360C),
            background = Color(0xFFFFE0B2)
        )
        Season.WINTER -> SeasonalColorScheme(
            primary = Color(0xFF1976D2),
            secondary = Color(0xFF42A5F5),
            accent = Color(0xFF0D47A1),
            background = Color(0xFFE3F2FD)
        )
    }
}

private data class SeasonalColorScheme(
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val background: Color
)

// 🎭 Mood System
private enum class FinancialMood { EXCELLENT, GOOD, OKAY, WORRIED, STRESSED }

private fun getFinancialMood(savingsRate: Double, budgetUsage: Int): FinancialMood {
    return when {
        savingsRate > 25 && budgetUsage < 70 -> FinancialMood.EXCELLENT
        savingsRate > 15 && budgetUsage < 85 -> FinancialMood.GOOD
        savingsRate > 5 && budgetUsage < 95 -> FinancialMood.OKAY
        budgetUsage > 100 -> FinancialMood.STRESSED
        else -> FinancialMood.WORRIED
    }
}

// 🌟 MAGICAL HERO SECTION
@Composable
private fun MagicalHeroSection(
    uiState: `in`.syncboard.planmate.presentation.viewmodel.DashboardUiState,
    viewModel: DashboardViewModel,
    seasonalColors: SeasonalColorScheme,
    currentSeason: Season,
    isVisible: Boolean,
    onNavigateToProfile: () -> Unit,
    onNavigateToReminders: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hero_animation")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

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
                        colors = listOf(
                            seasonalColors.primary,
                            seasonalColors.secondary,
                            seasonalColors.accent
                        ),
                        startX = shimmerOffset * 1000,
                        endX = shimmerOffset * 1000 + 1000
                    ),
                    shape = CardLargeShape
                )
                .padding(24.dp)
        ) {
            Column {
                // Header with seasonal greeting
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${getSeasonalGreeting(currentSeason)} ${getGreeting()}",
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
                            text = getMotivationalMessage(uiState.savingsRate),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AnimatedNotificationButton(onClick = onNavigateToReminders)
                        AnimatedProfileButton(onClick = onNavigateToProfile)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Balance showcase with breathing animation
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically() + fadeIn()
                ) {
                    MagicalBalanceShowcase(
                        totalBalance = viewModel.getFormattedBalance(),
                        monthlyIncome = viewModel.getFormattedMonthlyIncome(),
                        monthlyExpense = viewModel.getFormattedMonthlyExpense(),
                        savingsRate = uiState.savingsRate,
                        incomeComparison = uiState.incomeComparison,
                        expenseComparison = uiState.expenseComparison
                    )
                }
            }
        }
    }
}

// 🎊 ACHIEVEMENT BADGES CAROUSEL
@Composable
private fun AchievementBadgesCarousel(
    savingsRate: Double,
    budgetUsage: Int,
    transactionCount: Int,
    isVisible: Boolean
) {
    val achievements = getAchievements(savingsRate, budgetUsage, transactionCount)

    Section(title = "🏆 Your Achievements") {
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInHorizontally() + fadeIn()
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(achievements) { achievement ->
                    AchievementBadge(achievement = achievement)
                }
            }
        }
    }
}

// 🧠 FINANCIAL WISDOM CAROUSEL
@Composable
private fun FinancialWisdomCarousel(
    currentBalance: Double,
    savingsRate: Double,
    monthlyExpense: Double,
    showTip: Boolean,
    season: Season
) {
    val wisdomTips = getFinancialWisdom(currentBalance, savingsRate, monthlyExpense, season)

    Section(title = "🧠 Financial Wisdom") {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(wisdomTips) { tip ->
                WisdomCard(tip = tip, isHighlighted = showTip)
            }
        }
    }
}

// 📊 DYNAMIC STATS CAROUSEL
@Composable
private fun DynamicStatsCarousel(
    averageDaily: String,
    daysLeft: Int,
    projected: String,
    savingsRate: Int,
    seasonalColors: SeasonalColorScheme,
    isVisible: Boolean
) {
    Section(title = "📊 Live Stats") {
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInHorizontally() + fadeIn()
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(
                    listOf(
                        StatData("💰 Daily Avg", averageDaily, "Spending", Icons.Default.TrendingUp, seasonalColors.primary),
                        StatData("📅 Days Left", "$daysLeft", "In month", Icons.Default.CalendarMonth, seasonalColors.secondary),
                        StatData("🎯 Projected", projected, "Month total", Icons.Default.Insights, seasonalColors.accent),
                        StatData("💎 Savings", "$savingsRate%", "Of income", Icons.Default.Savings, IncomeGreen),
                        StatData("🔥 Streak", "${Random.nextInt(1, 30)}", "Days", Icons.Default.LocalFireDepartment, Warning500)
                    )
                ) { stat ->
                    PulsatingStatCard(stat = stat)
                }
            }
        }
    }
}

// 🎭 MOOD-BASED INSIGHTS
@Composable
private fun MoodBasedInsightsCarousel(
    insights: List<FinancialInsight>,
    currentMood: FinancialMood
) {
    val moodIcon = when (currentMood) {
        FinancialMood.EXCELLENT -> "🌟"
        FinancialMood.GOOD -> "😊"
        FinancialMood.OKAY -> "😐"
        FinancialMood.WORRIED -> "😟"
        FinancialMood.STRESSED -> "😰"
    }

    Section(title = "$moodIcon Financial Insights") {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(insights) { insight ->
                MoodBasedInsightCard(insight = insight, mood = currentMood)
            }
        }
    }
}

// 🏆 SMART CATEGORY ANALYTICS
@Composable
private fun SmartCategoryAnalytics(
    categories: List<CategorySpending>,
    totalExpense: Double,
    onViewAll: () -> Unit,
    seasonalColors: SeasonalColorScheme
) {
    Section(
        title = "🏆 Top Categories",
        action = "View All" to onViewAll
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(categories.take(6)) { category ->
                SmartCategoryCard(
                    category = category,
                    seasonalColor = seasonalColors.primary
                )
            }
        }
    }
}

// 💎 BUDGET PULSE MONITOR
@Composable
private fun BudgetPulseMonitor(
    budgetOverview: `in`.syncboard.planmate.presentation.viewmodel.BudgetOverview,
    onViewDetails: () -> Unit,
    pulseAnimation: Boolean
) {
    Section(title = "💎 Budget Pulse") {
        PulsingBudgetCard(
            budgetOverview = budgetOverview,
            onViewDetails = onViewDetails,
            shouldPulse = pulseAnimation
        )
    }
}

// 🚀 SMART ACTION CENTER
@Composable
private fun SmartActionCenter(
    onNavigateToAddExpense: () -> Unit,
    onNavigateToBudget: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    seasonalColors: SeasonalColorScheme,
    urgentActions: List<UrgentAction>
) {
    Section(title = "🚀 Quick Actions") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Urgent actions first
            if (urgentActions.isNotEmpty()) {
                Text(
                    text = "⚡ Urgent",
                    style = MaterialTheme.typography.titleSmall,
                    color = Error500,
                    fontWeight = FontWeight.Bold
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(urgentActions) { action ->
                        UrgentActionCard(action = action)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Regular actions
            val actions = listOf(
                ActionData("💸 Add Expense", "Record spending", Icons.Default.Add, seasonalColors.primary, onNavigateToAddExpense),
                ActionData("🎯 Set Budget", "Control spending", Icons.Default.AccountBalance, seasonalColors.secondary, onNavigateToBudget),
                ActionData("📂 Categories", "Organize money", Icons.Default.Category, seasonalColors.accent, onNavigateToCategories),
                ActionData("📈 Analytics", "View insights", Icons.Default.Analytics, Warning500, onNavigateToExpenses)
            )

            actions.chunked(2).forEach { rowActions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowActions.forEach { action ->
                        BouncyActionCard(
                            action = action,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowActions.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

// 🎬 TRANSACTION STORIES
@Composable
private fun TransactionStoriesCarousel(
    transactions: List<`in`.syncboard.planmate.presentation.viewmodel.TransactionItem>,
    totalTransactions: Int,
    onViewAll: () -> Unit,
    onAddTransaction: () -> Unit,
    currentIndex: Int
) {
    Section(
        title = "🎬 Recent Stories",
        subtitle = "$totalTransactions transactions this month",
        action = "View All" to onViewAll
    ) {
        if (transactions.isEmpty()) {
            EmptyTransactionStory(onAddTransaction = onAddTransaction)
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(transactions.take(8)) { transaction ->
                    TransactionStoryCard(
                        transaction = transaction,
                        isHighlighted = transactions.indexOf(transaction) == currentIndex % transactions.size
                    )
                }
            }
        }
    }
}

// 🎯 MONTHLY COMPARISON SHOWCASE
@Composable
private fun MonthlyComparisonShowcase(
    incomeComparison: MonthlyComparison,
    expenseComparison: MonthlyComparison,
    seasonalColors: SeasonalColorScheme
) {
    Section(title = "🎯 Month vs Month") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ComparisonCard(
                title = "Income",
                comparison = incomeComparison,
                icon = Icons.Default.TrendingUp,
                color = IncomeGreen,
                modifier = Modifier.weight(1f)
            )
            ComparisonCard(
                title = "Expenses",
                comparison = expenseComparison,
                icon = Icons.Default.TrendingDown,
                color = ExpenseRed,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// 🌟 SEASONAL SAVING CHALLENGES
@Composable
private fun SeasonalSavingChallenges(
    currentSeason: Season,
    currentSavings: Double,
    seasonalColors: SeasonalColorScheme
) {
    val challenges = getSeasonalChallenges(currentSeason, currentSavings)

    Section(title = "${getSeasonEmoji(currentSeason)} Seasonal Challenges") {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(challenges) { challenge ->
                SeasonalChallengeCard(
                    challenge = challenge,
                    seasonalColor = seasonalColors.primary
                )
            }
        }
    }
}

// 🎨 DAILY INSPIRATION
@Composable
private fun DailyInspirationCard(
    day: Int,
    userName: String,
    seasonalColors: SeasonalColorScheme
) {
    val inspiration = getDailyInspiration(day)

    Section(title = "🎨 Daily Inspiration") {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = CardLargeShape,
            colors = CardDefaults.cardColors(
                containerColor = seasonalColors.background
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = inspiration.emoji,
                    style = MaterialTheme.typography.displayMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = inspiration.quote,
                    style = MaterialTheme.typography.titleMedium,
                    color = seasonalColors.primary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Keep going, $userName! ${inspiration.message}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = seasonalColors.accent
                )
            }
        }
    }
}

// Helper Data Classes
private data class StatData(
    val title: String,
    val value: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color
)

private data class ActionData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

private data class Achievement(
    val title: String,
    val description: String,
    val emoji: String,
    val progress: Float,
    val isCompleted: Boolean
)

private data class WisdomTip(
    val title: String,
    val description: String,
    val emoji: String,
    val category: String
)

private data class UrgentAction(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

private data class SeasonalChallenge(
    val title: String,
    val description: String,
    val target: String,
    val progress: Float,
    val emoji: String
)

private data class DailyInspiration(
    val quote: String,
    val message: String,
    val emoji: String
)

// 🎭 MAGICAL LOADING SCREEN
@Composable
private fun MagicalLoadingScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "💰",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.graphicsLayer { rotationZ = rotation }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Preparing your financial universe...",
                style = MaterialTheme.typography.headlineSmall,
                color = Primary500
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                color = Primary500,
                modifier = Modifier.width(200.dp)
            )
        }
    }
}

// 🎭 MAGICAL NAVIGATION BAR
@Composable
private fun MagicalNavigationBar(
    onNavigateToCategories: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToAddExpense: () -> Unit,
    onNavigateToBudget: () -> Unit,
    onNavigateToProfile: () -> Unit,
    currentSeason: Season
) {
    val seasonalColor = when (currentSeason) {
        Season.SPRING -> Color(0xFF66BB6A)
        Season.SUMMER -> Color(0xFFFFB74D)
        Season.AUTUMN -> Color(0xFFD84315)
        Season.WINTER -> Color(0xFF1976D2)
    }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        NavigationBarItem(
            icon = {
                Icon(Icons.Default.Home, contentDescription = "Home", tint = seasonalColor)
            },
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
                val infiniteTransition = rememberInfiniteTransition(label = "fab_pulse")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "scale"
                )

                FloatingActionButton(
                    onClick = onNavigateToAddExpense,
                    modifier = Modifier
                        .size(40.dp)
                        .graphicsLayer { scaleX = scale; scaleY = scale },
                    containerColor = seasonalColor,
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

// 🌟 MAGICAL BALANCE SHOWCASE
@Composable
private fun MagicalBalanceShowcase(
    totalBalance: String,
    monthlyIncome: String,
    monthlyExpense: String,
    savingsRate: Double,
    incomeComparison: MonthlyComparison?,
    expenseComparison: MonthlyComparison?
) {
    val infiniteTransition = rememberInfiniteTransition(label = "balance_breathe")
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = breatheScale; scaleY = breatheScale },
        shape = CardLargeShape,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Balance section with animated counter
            Text(
                text = "Current Balance",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = totalBalance,
                    style = MaterialTheme.typography.displaySmall,
                    color = if (totalBalance.contains("-")) Error600 else IncomeGreen,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (totalBalance.contains("-")) Icons.Default.TrendingDown else Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = if (totalBalance.contains("-")) Error600 else IncomeGreen,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Income vs Expense cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Income card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = IncomeGreen.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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
                                color = IncomeGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = monthlyIncome,
                            style = MaterialTheme.typography.titleLarge,
                            color = IncomeGreen,
                            fontWeight = FontWeight.Bold
                        )
                        incomeComparison?.let { comparison ->
                            Text(
                                text = "${if (comparison.isIncrease) "↗️ +" else "↘️ -"}${comparison.percentageChange.toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (comparison.isIncrease) IncomeGreen else Error500
                            )
                        }
                    }
                }

                // Expense card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = ExpenseRed.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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
                                color = ExpenseRed,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = monthlyExpense,
                            style = MaterialTheme.typography.titleLarge,
                            color = ExpenseRed,
                            fontWeight = FontWeight.Bold
                        )
                        expenseComparison?.let { comparison ->
                            Text(
                                text = "${if (comparison.isIncrease) "↗️ +" else "↘️ -"}${comparison.percentageChange.toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (comparison.isIncrease) Error500 else IncomeGreen
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Savings rate indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Savings Rate: ${savingsRate.toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = when {
                        savingsRate > 20 -> IncomeGreen
                        savingsRate > 10 -> Warning500
                        else -> Error500
                    },
                    fontWeight = FontWeight.SemiBold
                )

                val progressColor = when {
                    savingsRate > 20 -> IncomeGreen
                    savingsRate > 10 -> Warning500
                    else -> Error500
                }

                CircularProgressIndicator(
                    progress = (savingsRate / 30).toFloat().coerceAtMost(1f),
                    modifier = Modifier.size(40.dp),
                    color = progressColor,
                    strokeWidth = 4.dp
                )
            }
        }
    }
}

// 🏆 ACHIEVEMENT BADGE
@Composable
private fun AchievementBadge(achievement: Achievement) {
    val infiniteTransition = rememberInfiniteTransition(label = "achievement_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Card(
        modifier = Modifier.width(140.dp),
        shape = CategoryCardShape,
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isCompleted)
                IncomeGreen.copy(alpha = 0.1f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Text(
                    text = achievement.emoji,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = if (achievement.isCompleted) {
                        Modifier.graphicsLayer { alpha = glowAlpha }
                    } else Modifier
                )
                if (achievement.isCompleted) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = IncomeGreen,
                        modifier = Modifier
                            .size(20.dp)
                            .offset(x = 20.dp, y = (-5).dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = achievement.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (achievement.isCompleted) IncomeGreen else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = achievement.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = achievement.progress,
                modifier = Modifier.fillMaxWidth(),
                color = if (achievement.isCompleted) IncomeGreen else Primary500,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Text(
                text = "${(achievement.progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = if (achievement.isCompleted) IncomeGreen else Primary500
            )
        }
    }
}

// 🧠 WISDOM CARD
@Composable
private fun WisdomCard(tip: WisdomTip, isHighlighted: Boolean) {
    val borderColor by animateColorAsState(
        targetValue = if (isHighlighted) Primary500 else Color.Transparent,
        animationSpec = tween(500),
        label = "border_color"
    )

    Card(
        modifier = Modifier
            .width(280.dp)
            .border(2.dp, borderColor, CategoryCardShape),
        shape = CategoryCardShape,
        colors = CardDefaults.cardColors(containerColor = Primary50)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tip.emoji,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = tip.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Primary700,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = tip.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = Primary500
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = tip.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Primary600
            )
        }
    }
}

// 📊 PULSATING STAT CARD
@Composable
private fun PulsatingStatCard(stat: StatData) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "stat_pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Card(
        modifier = Modifier
            .width(140.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = pulse
            },
        shape = CategoryCardShape,
        colors = CardDefaults.cardColors(containerColor = stat.color.copy(alpha = 0.1f)),
        onClick = { isPressed = !isPressed }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = stat.icon,
                contentDescription = stat.title,
                tint = stat.color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stat.title,
                style = MaterialTheme.typography.bodySmall,
                color = stat.color,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = stat.value,
                style = MaterialTheme.typography.titleLarge,
                color = stat.color,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stat.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = stat.color.copy(alpha = 0.7f)
            )
        }
    }
}

// Continue with remaining components...
// [Additional helper functions and components would continue here]

// Helper Functions
private fun getSeasonalGreeting(season: Season): String {
    return when (season) {
        Season.SPRING -> "🌸"
        Season.SUMMER -> "☀️"
        Season.AUTUMN -> "🍂"
        Season.WINTER -> "❄️"
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

private fun getMotivationalMessage(savingsRate: Double): String {
    return when {
        savingsRate > 20 -> "You're crushing your financial goals! 🚀"
        savingsRate > 10 -> "Great progress on your savings journey! 💪"
        savingsRate > 5 -> "Every penny saved is progress! 🌱"
        else -> "Time to supercharge your savings! ⚡"
    }
}

private fun getAchievements(savingsRate: Double, budgetUsage: Int, transactionCount: Int): List<Achievement> {
    return listOf(
        Achievement("Savings Star", "Save 20% of income", "⭐", (savingsRate/20).toFloat().coerceAtMost(1f), savingsRate >= 20),
        Achievement("Budget Master", "Stay under budget", "🎯", ((100-budgetUsage)/100).toFloat().coerceAtLeast(0f), budgetUsage <= 90),
        Achievement("Track Champion", "Record 30 transactions", "📝", (transactionCount/30f).coerceAtMost(1f), transactionCount >= 30),
        Achievement("Consistency King", "Daily tracking", "👑", 0.8f, true)
    )
}

private fun getFinancialWisdom(balance: Double, savingsRate: Double, expense: Double, season: Season): List<WisdomTip> {
    val seasonalTip = when (season) {
        Season.SPRING -> WisdomTip("Spring Cleaning", "Review and cut unnecessary subscriptions", "🌱", "Seasonal")
        Season.SUMMER -> WisdomTip("Summer Savings", "Plan vacation budget early to avoid overspending", "☀️", "Seasonal")
        Season.AUTUMN -> WisdomTip("Harvest Time", "Start building emergency fund for winter", "🍂", "Seasonal")
        Season.WINTER -> WisdomTip("Winter Wisdom", "Track heating costs and find ways to save energy", "❄️", "Seasonal")
    }

    return listOf(
        seasonalTip,
        WisdomTip("50/30/20 Rule", "50% needs, 30% wants, 20% savings", "📊", "Budgeting"),
        WisdomTip("Emergency Fund", "Save 6 months of expenses for peace of mind", "🛡️", "Safety"),
        WisdomTip("Compound Interest", "Start investing early, even small amounts grow", "📈", "Investment"),
        WisdomTip("Track Everything", "Awareness is the first step to control", "👁️", "Habits")
    )
}

private fun getUrgentActions(uiState: `in`.syncboard.planmate.presentation.viewmodel.DashboardUiState): List<UrgentAction> {
    val actions = mutableListOf<UrgentAction>()

    uiState.budgetOverview?.let { budget ->
        if (budget.usagePercentage > 90) {
            actions.add(UrgentAction("Budget Alert!", "You've used ${budget.usagePercentage.toInt()}% of budget", Icons.Default.Warning) {})
        }
    }

    if (uiState.savingsRate < 5) {
        actions.add(UrgentAction("Boost Savings!", "Your savings rate is low", Icons.Default.TrendingUp) {})
    }

    return actions
}

private fun getSeasonalChallenges(season: Season, currentSavings: Double): List<SeasonalChallenge> {
    return when (season) {
        Season.SPRING -> listOf(
            SeasonalChallenge("Spring Clean Budget", "Cut 3 unused subscriptions", "3 items", 0.7f, "🌱"),
            SeasonalChallenge("Garden Savings", "Save ₹5000 this month", "₹5000", (currentSavings/20).toFloat(), "🌸")
        )
        Season.SUMMER -> listOf(
            SeasonalChallenge("Summer Fund", "Save for vacation without debt", "₹15000", 0.4f, "☀️"),
            SeasonalChallenge("Cool Savings", "Reduce AC costs by 20%", "20%", 0.6f, "🧊")
        )
        Season.AUTUMN -> listOf(
            SeasonalChallenge("Harvest Gold", "Increase savings rate by 5%", "5%", 0.3f, "🍂"),
            SeasonalChallenge("Festival Budget", "Plan Diwali expenses early", "₹10000", 0.8f, "🪔")
        )
        Season.WINTER -> listOf(
            SeasonalChallenge("Winter Warmth", "Build emergency fund", "₹25000", 0.5f, "❄️"),
            SeasonalChallenge("Year-End Review", "Analyze full year spending", "12 months", 0.9f, "📊")
        )
    }
}

private fun getDailyInspiration(day: Int): DailyInspiration {
    val inspirations = listOf(
        DailyInspiration("Wealth consists not in having great possessions", "Every step towards financial freedom counts!", "💎"),
        DailyInspiration("A penny saved is a penny earned", "Small savings create big opportunities!", "🪙"),
        DailyInspiration("Invest in yourself, it pays the best interest", "Your financial education is priceless!", "🎓"),
        DailyInspiration("The best time to plant a tree was 20 years ago", "The second best time is now!", "🌳"),
        DailyInspiration("Money is a terrible master but an excellent servant", "Take control of your finances today!", "👑")
    )

    return inspirations[day % inspirations.size]
}

private fun getSeasonEmoji(season: Season): String {
    return when (season) {
        Season.SPRING -> "🌸"
        Season.SUMMER -> "☀️"
        Season.AUTUMN -> "🍂"
        Season.WINTER -> "❄️"
    }
}

// Additional component implementations...
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
                    fontWeight = FontWeight.Bold
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
                    Text(text, fontWeight = FontWeight.Medium)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun AnimatedNotificationButton(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "notification_ring")
    val ring by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring"
    )

    IconButton(onClick = onClick) {
        Box {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = Color.White,
                modifier = Modifier.graphicsLayer { rotationZ = ring * 20 - 10 }
            )
            // Notification badge with pulse
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        Error500.copy(alpha = 0.5f + ring * 0.5f),
                        CircleShape
                    )
                    .offset(x = 8.dp, y = (-4).dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Error500, CircleShape)
                        .align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun AnimatedProfileButton(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "profile_glow")
    val glow by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile",
            tint = Color.White.copy(alpha = glow),
            modifier = Modifier.size(28.dp)
        )
    }
}

// Placeholder implementations for missing components
@Composable
private fun MoodBasedInsightCard(insight: FinancialInsight, mood: FinancialMood) {
    // Implementation placeholder
    Card(
        modifier = Modifier.width(280.dp),
        colors = CardDefaults.cardColors(containerColor = Primary50)
    ) {
        Text(
            text = insight.title,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun SmartCategoryCard(category: CategorySpending, seasonalColor: Color) {
    // Implementation placeholder
    Card(
        modifier = Modifier.width(160.dp),
        colors = CardDefaults.cardColors(containerColor = seasonalColor.copy(alpha = 0.1f))
    ) {
        Text(
            text = category.categoryName,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun PulsingBudgetCard(
    budgetOverview: `in`.syncboard.planmate.presentation.viewmodel.BudgetOverview,
    onViewDetails: () -> Unit,
    shouldPulse: Boolean
) {
    // Implementation placeholder
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Primary50)
    ) {
        Text(
            text = "Budget Overview",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun BouncyActionCard(action: ActionData, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = action.color.copy(alpha = 0.1f)),
        onClick = action.onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(action.icon, contentDescription = action.title, tint = action.color)
            Text(text = action.title, color = action.color)
        }
    }
}

@Composable
private fun TransactionStoryCard(
    transaction: `in`.syncboard.planmate.presentation.viewmodel.TransactionItem,
    isHighlighted: Boolean
) {
    Card(
        modifier = Modifier.width(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted) Primary50 else MaterialTheme.colorScheme.surface
        )
    ) {
        Text(
            text = transaction.title,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun EmptyTransactionStory(onAddTransaction: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No transactions yet")
            Button(onClick = onAddTransaction) {
                Text("Add Transaction")
            }
        }
    }
}

@Composable
private fun ComparisonCard(
    title: String,
    comparison: MonthlyComparison,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = title, tint = color)
            Text(text = title, color = color)
            Text(
                text = "₹${String.format("%,.0f", comparison.currentMonth)}",
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SeasonalChallengeCard(challenge: SeasonalChallenge, seasonalColor: Color) {
    Card(
        modifier = Modifier.width(180.dp),
        colors = CardDefaults.cardColors(containerColor = seasonalColor.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${challenge.emoji} ${challenge.title}",
                style = MaterialTheme.typography.titleSmall,
                color = seasonalColor
            )
            Text(
                text = challenge.description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun UrgentActionCard(action: UrgentAction) {
    Card(
        modifier = Modifier.width(160.dp),
        colors = CardDefaults.cardColors(containerColor = Error50),
        onClick = action.onClick
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(action.icon, contentDescription = action.title, tint = Error500)
            Text(text = action.title, color = Error600)
        }
    }
}