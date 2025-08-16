// AddExpenseScreen.kt
package `in`.syncboard.planmate.presentation.ui.screens.expense

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.syncboard.planmate.presentation.ui.components.CustomTextField
import `in`.syncboard.planmate.presentation.ui.components.GradientButton
import `in`.syncboard.planmate.presentation.ui.components.LoadingState
import `in`.syncboard.planmate.presentation.viewmodel.ExpenseViewModel
import `in`.syncboard.planmate.ui.theme.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.util.Calendar

/**
 * Add Expense Screen with Modern UI
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onNavigateBack: () -> Unit,
    onExpenseSaved: () -> Unit,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val addExpenseState = viewModel.addExpenseState

    // Date and Time state
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Date picker state
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    // Time picker state
    val timePickerState = rememberTimePickerState(
        initialHour = selectedTime.hour,
        initialMinute = selectedTime.minute
    )

    // Payment methods
    val paymentMethods = listOf(
        Pair("Cash", "💵"),
        Pair("Card", "💳"),
        Pair("UPI", "📱"),
        Pair("Wallet", "👛"),
        Pair("Bank Transfer", "🏦")
    )

    // Reset form when screen opens
    LaunchedEffect(Unit) {
        viewModel.resetAddExpenseForm()
    }

    // Update date in viewmodel when selectedDate changes
    LaunchedEffect(selectedDate, selectedTime) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedDate.year)
            set(Calendar.MONTH, selectedDate.monthValue - 1)
            set(Calendar.DAY_OF_MONTH, selectedDate.dayOfMonth)
            set(Calendar.HOUR_OF_DAY, selectedTime.hour)
            set(Calendar.MINUTE, selectedTime.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        viewModel.updateDate(calendar.timeInMillis)
    }

    // Show loading state
    if (addExpenseState.isSaving) {
        LoadingState(message = "Saving expense...")
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
                AddExpenseTopBar(
                    onNavigateBack = onNavigateBack,
                    onSave = {
                        viewModel.saveExpense {
                            onExpenseSaved()
                        }
                    },
                    isEnabled = addExpenseState.isValidForm && !addExpenseState.isSaving,
                    isSaving = addExpenseState.isSaving
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

                // Amount Input Section
                AmountInputCard(
                    amount = addExpenseState.amount,
                    onAmountChange = { viewModel.updateAmount(it) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Basic Details Section
                Text(
                    text = "Transaction Details",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Description
                CustomTextField(
                    value = addExpenseState.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    label = "Description",
                    placeholder = "What did you spend on?",
                    leadingIcon = Icons.Default.Edit,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Category Selection
                CategorySelectionSection(
                    categories = addExpenseState.categories,
                    selectedCategory = addExpenseState.selectedCategory,
                    onCategorySelected = { viewModel.updateCategory(it) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Date and Time Selection
                DateTimeSelectionSection(
                    selectedDate = selectedDate,
                    selectedTime = selectedTime,
                    onDateClick = { showDatePicker = true },
                    onTimeClick = { showTimePicker = true }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Location (Optional)
                CustomTextField(
                    value = addExpenseState.location,
                    onValueChange = { viewModel.updateLocation(it) },
                    label = "Location (Optional)",
                    placeholder = "Where did you spend?",
                    leadingIcon = Icons.Default.LocationOn,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Notes (Optional)
                CustomTextField(
                    value = addExpenseState.notes,
                    onValueChange = { viewModel.updateNotes(it) },
                    label = "Notes (Optional)",
                    placeholder = "Add any additional notes...",
                    leadingIcon = Icons.Default.Notes,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Payment Method
                PaymentMethodSection(
                    paymentMethods = paymentMethods,
                    selectedMethod = addExpenseState.selectedPaymentMethod,
                    onMethodSelected = { viewModel.updatePaymentMethod(it) }
                )

                // Error Message
                if (addExpenseState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    ErrorCard(message = addExpenseState.errorMessage)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Save Button
                GradientButton(
                    text = if (addExpenseState.isSaving) "Saving Expense..." else "Save Expense",
                    onClick = {
                        viewModel.saveExpense {
                            onExpenseSaved()
                        }
                    },
                    enabled = addExpenseState.isValidForm && !addExpenseState.isSaving,
                    loading = addExpenseState.isSaving,
                    icon = Icons.Default.Save,
                    gradientColors = listOf(Primary500, Secondary500),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            TimePicker(
                state = timePickerState,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddExpenseTopBar(
    onNavigateBack: () -> Unit,
    onSave: () -> Unit,
    isEnabled: Boolean,
    isSaving: Boolean
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
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Add Expense",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            TextButton(
                onClick = onSave,
                enabled = isEnabled,
                modifier = Modifier
                    .background(
                        if (isEnabled) Color.White.copy(alpha = 0.1f) else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (isSaving) "Saving..." else "Save",
                    fontWeight = FontWeight.Bold,
                    color = if (isEnabled) Color.White else Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun AmountInputCard(
    amount: String,
    onAmountChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Primary50,
                            Secondary50.copy(alpha = 0.5f)
                        )
                    )
                )
                .padding(32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Amount",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Primary700,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "₹",
                        style = MaterialTheme.typography.displaySmall,
                        color = Primary600,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = amount,
                        onValueChange = onAmountChange,
                        placeholder = {
                            Text(
                                "0",
                                style = MaterialTheme.typography.displaySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        },
                        textStyle = MaterialTheme.typography.displaySmall.copy(
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = Primary600
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = Primary500
                        ),
                        modifier = Modifier.width(240.dp),
                        singleLine = true
                    )
                }

                if (amount.isNotBlank()) {
                    val amountValue = amount.toDoubleOrNull()
                    if (amountValue != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "₹${String.format("%,.2f", amountValue)}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Primary500,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategorySelectionSection(
    categories: List<`in`.syncboard.planmate.domain.entity.Category>,
    selectedCategory: `in`.syncboard.planmate.domain.entity.Category?,
    onCategorySelected: (`in`.syncboard.planmate.domain.entity.Category) -> Unit
) {
    Text(
        text = "Category",
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    if (categories.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(240.dp)
        ) {
            items(categories) { category ->
                CategorySelectionCard(
                    category = category,
                    isSelected = selectedCategory?.id == category.id,
                    onClick = { onCategorySelected(category) }
                )
            }
        }
    } else {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Text(
                text = "Loading categories...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(24.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CategorySelectionCard(
    category: `in`.syncboard.planmate.domain.entity.Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = ""
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .selectable(
                selected = isSelected,
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                parseColor(category.color).copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected)
            BorderStroke(2.dp, parseColor(category.color))
        else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Category Icon with glow
            Box {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .blur(8.dp)
                            .background(
                                color = parseColor(category.color).copy(alpha = 0.4f),
                                shape = CircleShape
                            )
                    )
                }
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            brush = if (isSelected) {
                                Brush.radialGradient(
                                    colors = listOf(
                                        parseColor(category.color).copy(alpha = 0.3f),
                                        parseColor(category.color).copy(alpha = 0.1f)
                                    )
                                )
                            } else {
                                Brush.radialGradient(
                                    colors = listOf(
                                        parseColor(category.color).copy(alpha = 0.1f),
                                        parseColor(category.color).copy(alpha = 0.05f)
                                    )
                                )
                            },
                            shape = CircleShape
                        )
                        .zIndex(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.icon,
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 28.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected)
                    parseColor(category.color)
                else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun DateTimeSelectionSection(
    selectedDate: LocalDate,
    selectedTime: LocalTime,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit
) {
    Text(
        text = "Date & Time",
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Date Selection Card
        DateTimeCard(
            modifier = Modifier.weight(1f),
            title = "Date",
            value = selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            subtitle = when {
                selectedDate == LocalDate.now() -> "Today"
                selectedDate == LocalDate.now().minusDays(1) -> "Yesterday"
                selectedDate.isAfter(LocalDate.now()) -> "Future"
                else -> "${LocalDate.now().toEpochDay() - selectedDate.toEpochDay()} days ago"
            },
            icon = Icons.Default.CalendarToday,
            color = Primary500,
            onClick = onDateClick
        )

        // Time Selection Card
        DateTimeCard(
            modifier = Modifier.weight(1f),
            title = "Time",
            value = selectedTime.format(DateTimeFormatter.ofPattern("hh:mm a")),
            subtitle = formatTimeAgo(selectedTime),
            icon = Icons.Default.AccessTime,
            color = Secondary500,
            onClick = onTimeClick
        )
    }
}

@Composable
private fun DateTimeCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PaymentMethodSection(
    paymentMethods: List<Pair<String, String>>,
    selectedMethod: String,
    onMethodSelected: (String) -> Unit
) {
    Text(
        text = "Payment Method",
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.height(120.dp)
    ) {
        items(paymentMethods) { (method, icon) ->
            PaymentMethodCard(
                method = method,
                icon = icon,
                isSelected = selectedMethod == method,
                onClick = { onMethodSelected(method) }
            )
        }
    }
}

@Composable
private fun PaymentMethodCard(
    method: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Primary100 else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected)
            BorderStroke(2.dp, Primary500)
        else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = method,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) Primary700 else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Error50),
        shape = RoundedCornerShape(16.dp)
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
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Error700
            )
        }
    }
}

@Composable
private fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        text = content,
        title = {
            Text(
                text = "Select Time",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    )
}

// Helper functions
private fun parseColor(colorString: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorString))
    } catch (e: Exception) {
        Primary500
    }
}

private fun formatTimeAgo(selectedTime: LocalTime): String {
    val now = LocalTime.now()
    val diffMinutes = java.time.Duration.between(selectedTime, now).toMinutes()

    return when {
        diffMinutes < 0 -> "In the future"
        diffMinutes == 0L -> "Just now"
        diffMinutes < 60 -> "$diffMinutes minutes ago"
        diffMinutes < 120 -> "1 hour ago"
        diffMinutes < 1440 -> "${diffMinutes / 60} hours ago"
        else -> "More than a day ago"
    }
}