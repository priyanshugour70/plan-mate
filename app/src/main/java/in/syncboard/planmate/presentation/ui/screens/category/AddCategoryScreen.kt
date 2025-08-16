// Path: app/src/main/java/in/syncboard/planmate/presentation/ui/screens/category/AddCategoryScreen.kt

package `in`.syncboard.planmate.presentation.ui.screens.category

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.syncboard.planmate.presentation.ui.components.CustomTextField
import `in`.syncboard.planmate.presentation.ui.components.GradientButton
import `in`.syncboard.planmate.presentation.ui.components.LoadingState
import `in`.syncboard.planmate.presentation.viewmodel.CategoryViewModel
import `in`.syncboard.planmate.domain.entity.TransactionType
import `in`.syncboard.planmate.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(
    onNavigateBack: () -> Unit,
    onCategorySaved: () -> Unit,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val addCategoryState = viewModel.addCategoryState

    // Predefined icons and colors
    val availableIcons = listOf(
        "🍕", "🚗", "🛍️", "🎮", "🏥", "⚡", "📚", "✈️",
        "🏠", "📱", "💰", "🎵", "💊", "⛽", "👔", "🎯",
        "☕", "🍎", "🎪", "📺", "💻", "🎨", "🏃", "🍔",
        "🚌", "🎬", "📖", "💳", "🎤", "🏋️", "🎸", "🍷"
    )

    val availableColors = listOf(
        "#FF5722", "#E91E63", "#9C27B0", "#673AB7",
        "#3F51B5", "#2196F3", "#03A9F4", "#00BCD4",
        "#009688", "#4CAF50", "#8BC34A", "#CDDC39",
        "#FFEB3B", "#FFC107", "#FF9800", "#795548"
    )

    LaunchedEffect(Unit) {
        viewModel.resetAddCategoryForm()
    }

    if (addCategoryState.isSaving) {
        LoadingState(message = "Saving category...")
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add Category",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.saveCategory {
                                onCategorySaved()
                            }
                        },
                        enabled = addCategoryState.isValidForm && !addCategoryState.isSaving
                    ) {
                        Text(
                            if (addCategoryState.isSaving) "Saving..." else "Save",
                            fontWeight = FontWeight.Medium,
                            color = if (addCategoryState.isValidForm && !addCategoryState.isSaving)
                                Primary500 else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Category Preview Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = CardLargeShape,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Preview",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Category Preview
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                color = parseColor(addCategoryState.selectedColor).copy(alpha = 0.1f),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = addCategoryState.selectedIcon.ifEmpty { "📁" },
                            style = MaterialTheme.typography.displaySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = addCategoryState.name.ifEmpty { "Category Name" },
                        style = MaterialTheme.typography.titleMedium,
                        color = parseColor(addCategoryState.selectedColor),
                        fontWeight = FontWeight.SemiBold
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (addCategoryState.selectedType == TransactionType.EXPENSE) Error50 else Tertiary50,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = addCategoryState.selectedType.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (addCategoryState.selectedType == TransactionType.EXPENSE) Error700 else Tertiary700,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Basic Details Section
            Text(
                text = "Category Details",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Category Name
            CustomTextField(
                value = addCategoryState.name,
                onValueChange = { viewModel.updateCategoryName(it) },
                label = "Category Name",
                placeholder = "Enter category name",
                leadingIcon = Icons.Default.Label,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Transaction Type Selection
            Text(
                text = "Type",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Expense Type
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .selectable(
                            selected = addCategoryState.selectedType == TransactionType.EXPENSE,
                            onClick = { viewModel.updateCategoryType(TransactionType.EXPENSE) }
                        ),
                    shape = CategoryCardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = if (addCategoryState.selectedType == TransactionType.EXPENSE)
                            Error100 else MaterialTheme.colorScheme.surface
                    ),
                    border = if (addCategoryState.selectedType == TransactionType.EXPENSE)
                        androidx.compose.foundation.BorderStroke(2.dp, Error500) else null
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.TrendingDown,
                            contentDescription = "Expense",
                            tint = if (addCategoryState.selectedType == TransactionType.EXPENSE) Error700 else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Expense",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (addCategoryState.selectedType == TransactionType.EXPENSE) Error700 else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Income Type
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .selectable(
                            selected = addCategoryState.selectedType == TransactionType.INCOME,
                            onClick = { viewModel.updateCategoryType(TransactionType.INCOME) }
                        ),
                    shape = CategoryCardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = if (addCategoryState.selectedType == TransactionType.INCOME)
                            Tertiary100 else MaterialTheme.colorScheme.surface
                    ),
                    border = if (addCategoryState.selectedType == TransactionType.INCOME)
                        androidx.compose.foundation.BorderStroke(2.dp, Tertiary500) else null
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.TrendingUp,
                            contentDescription = "Income",
                            tint = if (addCategoryState.selectedType == TransactionType.INCOME) Tertiary700 else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Income",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (addCategoryState.selectedType == TransactionType.INCOME) Tertiary700 else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Icon Selection
            Text(
                text = "Choose Icon",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(8),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(200.dp)
            ) {
                items(availableIcons) { icon ->
                    IconSelectionCard(
                        icon = icon,
                        isSelected = addCategoryState.selectedIcon == icon,
                        onClick = { viewModel.updateCategoryIcon(icon) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Color Selection
            Text(
                text = "Choose Color",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(8),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(80.dp)
            ) {
                items(availableColors) { color ->
                    ColorSelectionCard(
                        color = color,
                        isSelected = addCategoryState.selectedColor == color,
                        onClick = { viewModel.updateCategoryColor(color) }
                    )
                }
            }

            // Error Message
            if (addCategoryState.errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Error50),
                    shape = CategoryCardShape
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = "Error",
                            tint = Error500,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = addCategoryState.errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Error700
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save Button
            GradientButton(
                text = if (addCategoryState.isSaving) "Saving Category..." else "Save Category",
                onClick = {
                    viewModel.saveCategory {
                        onCategorySaved()
                    }
                },
                enabled = addCategoryState.isValidForm && !addCategoryState.isSaving,
                gradientColors = listOf(Primary500, Secondary500),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun IconSelectionCard(
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) Primary100 else MaterialTheme.colorScheme.surface
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Primary500 else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ColorSelectionCard(
    color: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(parseColor(color))
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
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