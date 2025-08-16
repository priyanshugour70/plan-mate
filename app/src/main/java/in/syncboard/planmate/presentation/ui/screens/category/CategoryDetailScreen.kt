// Path: app/src/main/java/in/syncboard/planmate/presentation/ui/screens/category/CategoryDetailScreen.kt

package `in`.syncboard.planmate.presentation.ui.screens.category

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.syncboard.planmate.presentation.ui.components.LoadingState
import `in`.syncboard.planmate.presentation.viewmodel.CategoryDetailViewModel
import `in`.syncboard.planmate.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    categoryId: String,
    onNavigateBack: () -> Unit,
    onNavigateToSetBudget: (String) -> Unit,
    viewModel: CategoryDetailViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    LaunchedEffect(categoryId) {
        viewModel.loadCategoryDetails(categoryId)
    }

    if (uiState.isLoading) {
        LoadingState(message = "Loading category details...")
        return
    }

    val category = uiState.category ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        category.name,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle edit */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { onNavigateToSetBudget(categoryId) }) {
                        Icon(Icons.Default.AccountBalance, contentDescription = "Set Budget")
                    }
                }
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
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Category Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardLargeShape,
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        parseColor(category.color),
                                        parseColor(category.color).copy(alpha = 0.7f)
                                    )
                                ),
                                shape = CardLargeShape
                            )
                            .padding(24.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Category Icon
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.2f),
                                        RoundedCornerShape(20.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = category.icon,
                                    style = MaterialTheme.typography.displaySmall
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text(
                                    text = category.type.name,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Statistics Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = CategoryCardShape,
                        colors = CardDefaults.cardColors(containerColor = Primary50)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = uiState.transactionCount.toString(),
                                style = MaterialTheme.typography.amountMedium,
                                color = Primary700,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Transactions",
                                style = MaterialTheme.typography.bodySmall,
                                color = Primary600
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = CategoryCardShape,
                        colors = CardDefaults.cardColors(
                            containerColor = if (category.type.name == "EXPENSE") Error50 else Tertiary50
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "₹${String.format("%,.0f", uiState.totalAmount)}",
                                style = MaterialTheme.typography.amountMedium,
                                color = if (category.type.name == "EXPENSE") Error700 else Tertiary700,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Total Amount",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (category.type.name == "EXPENSE") Error600 else Tertiary600
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = CategoryCardShape,
                        colors = CardDefaults.cardColors(containerColor = Secondary50)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "₹${String.format("%,.0f", uiState.averageAmount)}",
                                style = MaterialTheme.typography.amountMedium,
                                color = Secondary700,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Average",
                                style = MaterialTheme.typography.bodySmall,
                                color = Secondary600
                            )
                        }
                    }
                }
            }

            // Budget Section (for expense categories)
            if (category.type.name == "EXPENSE") {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Budget",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (uiState.budget == null) {
                            FilledTonalButton(
                                onClick = { onNavigateToSetBudget(categoryId) }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Set Budget")
                            }
                        }
                    }
                }

                item {
                    if (uiState.budget != null) {
                        val budget = uiState.budget!!
                        val percentage = if (budget.allocatedAmount > 0) {
                            (budget.spentAmount / budget.allocatedAmount * 100).toInt()
                        } else 0

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = CategoryCardShape,
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    percentage > 100 -> Error50
                                    percentage > 80 -> Warning50
                                    else -> Tertiary50
                                }
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Monthly Budget",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${percentage}%",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            percentage > 100 -> Error700
                                            percentage > 80 -> Warning700
                                            else -> Tertiary700
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "₹${String.format("%,.0f", budget.spentAmount)} of ₹${String.format("%,.0f", budget.allocatedAmount)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                LinearProgressIndicator(
                                    progress = (percentage / 100f).coerceAtMost(1f),
                                    modifier = Modifier.fillMaxWidth(),
                                    color = when {
                                        percentage > 100 -> Error500
                                        percentage > 80 -> Warning500
                                        else -> Tertiary500
                                    }
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = if (budget.spentAmount > budget.allocatedAmount) {
                                        "Over budget by ₹${String.format("%,.0f", budget.spentAmount - budget.allocatedAmount)}"
                                    } else {
                                        "₹${String.format("%,.0f", budget.allocatedAmount - budget.spentAmount)} remaining"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = when {
                                        percentage > 100 -> Error600
                                        percentage > 80 -> Warning600
                                        else -> Tertiary600
                                    }
                                )
                            }
                        }
                    } else {
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
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalance,
                                    contentDescription = "No budget",
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No budget set",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Set a budget to track your spending in this category",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { onNavigateToSetBudget(categoryId) }) {
                                    Text("Set Budget")
                                }
                            }
                        }
                    }
                }
            }

            // Recent Transactions
            item {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (uiState.recentTransactions.isEmpty()) {
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
                                text = "Start adding transactions in this category",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            } else {
                items(uiState.recentTransactions.take(5)) { transaction ->
                    TransactionItemCard(transaction = transaction)
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun TransactionItemCard(
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${transaction.date} • ${transaction.time}",
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

private fun parseColor(colorString: String): androidx.compose.ui.graphics.Color {
    return try {
        androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(colorString))
    } catch (e: Exception) {
        Primary500
    }
}