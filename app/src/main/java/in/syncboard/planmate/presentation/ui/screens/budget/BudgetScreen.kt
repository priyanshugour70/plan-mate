// Path: app/src/main/java/in/syncboard/planmate/presentation/ui/screens/budget/BudgetScreen.kt

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
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.syncboard.planmate.presentation.ui.components.LoadingState
import `in`.syncboard.planmate.presentation.viewmodel.BudgetViewModel
import `in`.syncboard.planmate.presentation.viewmodel.BudgetCategoryItem
import `in`.syncboard.planmate.ui.theme.*

/**
 * Budget Screen - Updated to work with real data
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    onNavigateBack: () -> Unit,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val addBudgetState = viewModel.addBudgetState
    var showAddBudgetDialog by remember { mutableStateOf(false) }

    // Show loading state
    if (uiState.isLoading) {
        LoadingState(message = "Loading budgets...")
        return
    }

    val remainingBudget = viewModel.getRemainingBudget()
    val spentPercentage = viewModel.getBudgetUsagePercentage()

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
                    IconButton(onClick = { showAddBudgetDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Budget")
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
                                text = viewModel.formatAmount(uiState.totalBudget),
                                style = MaterialTheme.typography.amountLarge,
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            Text(
                                text = "${viewModel.formatAmount(remainingBudget)} remaining",
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
                                    text = "Spent: ${viewModel.formatAmount(uiState.totalSpent)}",
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

            // Budget Categories Header
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
                        onClick = { showAddBudgetDialog = true },
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
                        Text("Add Budget")
                    }
                }
            }

            // Budget Categories List
            if (uiState.budgetCategories.isEmpty()) {
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
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = "No budgets",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No budgets yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Create your first budget to start tracking expenses",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            } else {
                items(uiState.budgetCategories) { category ->
                    BudgetCategoryCard(
                        category = category,
                        onEditClick = { /* Handle edit */ },
                        onDeleteClick = { viewModel.deleteBudget(category.id) }
                    )
                }
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
                                text = viewModel.getBudgetTip(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Primary600
                            )
                        }
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
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Add Budget Dialog
    if (showAddBudgetDialog) {
        AddBudgetDialog(
            uiState = addBudgetState,
            availableCategories = uiState.availableCategories,
            onDismiss = {
                showAddBudgetDialog = false
                viewModel.clearError()
            },
            onCategorySelected = { viewModel.updateSelectedCategory(it) },
            onAmountChanged = { viewModel.updateBudgetAmountInput(it) },
            onPeriodChanged = { viewModel.updateBudgetPeriod(it) },
            onSave = {
                viewModel.addBudget {
                    showAddBudgetDialog = false
                }
            }
        )
    }
}

/**
 * Budget Category Card Component
 */
@Composable
private fun BudgetCategoryCard(
    category: BudgetCategoryItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val percentage = if (category.budget > 0) (category.spent / category.budget * 100).toInt() else 0
    val isOverBudget = category.spent > category.budget

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CategoryCardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
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

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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

/**
 * Add Budget Dialog
 */
@Composable
private fun AddBudgetDialog(
    uiState: `in`.syncboard.planmate.presentation.viewmodel.AddBudgetUiState,
    availableCategories: List<`in`.syncboard.planmate.domain.entity.Category>,
    onDismiss: () -> Unit,
    onCategorySelected: (`in`.syncboard.planmate.domain.entity.Category) -> Unit,
    onAmountChanged: (String) -> Unit,
    onPeriodChanged: (`in`.syncboard.planmate.domain.entity.BudgetPeriod) -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Budget") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Category Selection
                if (availableCategories.isNotEmpty()) {
                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.labelMedium
                    )

                    LazyColumn(
                        modifier = Modifier.height(120.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(availableCategories) { category ->
                            FilterChip(
                                onClick = { onCategorySelected(category) },
                                label = { Text("${category.icon} ${category.name}") },
                                selected = uiState.selectedCategory?.id == category.id
                            )
                        }
                    }
                } else {
                    Text(
                        text = "All categories already have budgets",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Amount Input
                OutlinedTextField(
                    value = uiState.amount,
                    onValueChange = onAmountChanged,
                    label = { Text("Budget Amount") },
                    prefix = { Text("₹") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onSave,
                enabled = uiState.isValidForm && !uiState.isSaving
            ) {
                Text(if (uiState.isSaving) "Saving..." else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}