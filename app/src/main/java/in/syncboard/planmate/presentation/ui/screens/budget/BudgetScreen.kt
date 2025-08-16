package `in`.syncboard.planmate.presentation.ui.screens.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import `in`.syncboard.planmate.ui.theme.*

/**
 * Budget Category Data Class
 */
data class BudgetCategory(
    val name: String,
    val icon: String,
    val budget: Double,
    val spent: Double,
    val color: Color
)

/**
 * Budget Screen
 * Shows monthly budget overview and category-wise budget allocation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    onNavigateBack: () -> Unit
) {
    // Mock data for budget categories
    val budgetCategories = remember {
        listOf(
            BudgetCategory("Food & Dining", "🍕", 15000.0, 12340.0, FoodColor),
            BudgetCategory("Transportation", "🚗", 8000.0, 5420.0, TransportColor),
            BudgetCategory("Shopping", "🛍️", 12000.0, 10200.0, ShoppingColor),
            BudgetCategory("Entertainment", "🎮", 5000.0, 3200.0, EntertainmentColor),
            BudgetCategory("Health", "🏥", 6000.0, 2800.0, HealthColor),
            BudgetCategory("Bills & Utilities", "⚡", 8000.0, 7800.0, BillsColor),
            BudgetCategory("Education", "📚", 4000.0, 1500.0, EducationColor)
        )
    }

    val totalBudget = budgetCategories.sumOf { it.budget }
    val totalSpent = budgetCategories.sumOf { it.spent }
    val remainingBudget = totalBudget - totalSpent
    val spentPercentage = (totalSpent / totalBudget * 100).toInt()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Budget Management",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Tertiary500, Primary500)
                    )
                )
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

            // Monthly Budget Overview Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardLargeShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Tertiary500, Primary500)
                                ),
                                shape = CardLargeShape
                            )
                            .padding(24.dp)
                    ) {
                        Column {
                            Text(
                                text = "Monthly Budget",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )

                            Text(
                                text = "₹${String.format("%,.0f", totalBudget)}",
                                style = MaterialTheme.typography.amountLarge,
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            Text(
                                text = "₹${String.format("%,.0f", remainingBudget)} remaining",
                                style = MaterialTheme.typography.amountSmall,
                                color = if (remainingBudget > 0) Color.White else Color.Red.copy(alpha = 0.8f),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Progress Bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.White.copy(alpha = 0.3f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(spentPercentage / 100f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.White)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Spent: ₹${String.format("%,.0f", totalSpent)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "${spentPercentage}% used",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            // Add Category Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Budget Categories",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )

                    FilledTonalButton(
                        onClick = { /* Handle add category */ },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Primary100
                        )
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Category")
                    }
                }
            }

            // Budget Categories List
            items(budgetCategories) { category ->
                BudgetCategoryCard(category = category)
            }

            // Budget Tips Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CategoryCardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Primary50
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Tip",
                            tint = Primary700,
                            modifier = Modifier.size(32.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Budget Tip",
                                style = MaterialTheme.typography.titleMedium,
                                color = Primary700,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = when {
                                    spentPercentage >= 90 -> "You're close to your budget limit. Consider reducing expenses."
                                    spentPercentage >= 75 -> "You've used most of your budget. Monitor your spending carefully."
                                    spentPercentage >= 50 -> "You're on track! Keep monitoring your expenses."
                                    else -> "Great start! You have plenty of room in your budget."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = Primary600
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

/**
 * Budget Category Card Component
 */
@Composable
private fun BudgetCategoryCard(
    category: BudgetCategory,
    modifier: Modifier = Modifier
) {
    val percentage = (category.spent / category.budget * 100).toInt()
    val isOverBudget = category.spent > category.budget

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CategoryCardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = { /* Handle category click */ }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Category Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = category.icon,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "₹${String.format("%,.0f", category.spent)} of ₹${String.format("%,.0f", category.budget)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "${percentage}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = when {
                        isOverBudget -> Error500
                        percentage > 80 -> Warning500
                        else -> Tertiary500
                    },
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(category.color.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth((percentage / 100f).coerceAtMost(1f))
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            if (isOverBudget) Error500
                            else if (percentage > 80) Warning500
                            else category.color
                        )
                )
            }

            // Over Budget Warning
            if (isOverBudget) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = Error500,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Over budget by ₹${String.format("%,.0f", category.spent - category.budget)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Error500
                    )
                }
            }
        }
    }
}