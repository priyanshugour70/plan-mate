// Path: app/src/main/java/in/syncboard/planmate/presentation/ui/screens/expense/AddExpenseScreen.kt
package `in`.syncboard.planmate.presentation.ui.screens.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
 * Add Expense Screen - With Working Date and Time Pickers
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add Expense",
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
                            viewModel.saveExpense {
                                onExpenseSaved()
                            }
                        },
                        enabled = addExpenseState.isValidForm && !addExpenseState.isSaving
                    ) {
                        Text(
                            if (addExpenseState.isSaving) "Saving..." else "Save",
                            fontWeight = FontWeight.Medium,
                            color = if (addExpenseState.isValidForm && !addExpenseState.isSaving)
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

            // Amount Input Section
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
                        text = "Amount",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "₹",
                            style = MaterialTheme.typography.displaySmall,
                            color = Primary500,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = addExpenseState.amount,
                            onValueChange = { viewModel.updateAmount(it) },
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
                                color = Primary500
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                cursorColor = Primary500
                            ),
                            modifier = Modifier.width(220.dp),
                            singleLine = true
                        )
                    }

                    if (addExpenseState.amount.isNotBlank()) {
                        val amountValue = addExpenseState.amount.toDoubleOrNull()
                        if (amountValue != null) {
                            Text(
                                text = "₹${String.format("%,.2f", amountValue)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Basic Details Section
            Text(
                text = "Transaction Details",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
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
            Text(
                text = "Category",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Category Grid
            if (addExpenseState.categories.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(200.dp)
                ) {
                    items(addExpenseState.categories.size) { index ->
                        val category = addExpenseState.categories[index]
                        CategorySelectionCard(
                            category = category,
                            isSelected = addExpenseState.selectedCategory?.id == category.id,
                            onClick = { viewModel.updateCategory(category) }
                        )
                    }
                }
            } else {
                Text(
                    text = "Loading categories...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Date and Time Selection
            Text(
                text = "Date & Time",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Date Selection
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showDatePicker = true },
                    shape = InputShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = "Select Date",
                                tint = Primary500,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Date",
                                style = MaterialTheme.typography.labelMedium,
                                color = Primary500,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = when {
                                selectedDate == LocalDate.now() -> "Today"
                                selectedDate == LocalDate.now().minusDays(1) -> "Yesterday"
                                selectedDate.isAfter(LocalDate.now()) -> "Future"
                                else -> "${LocalDate.now().toEpochDay() - selectedDate.toEpochDay()} days ago"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Time Selection
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showTimePicker = true },
                    shape = InputShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AccessTime,
                                contentDescription = "Select Time",
                                tint = Secondary500,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Time",
                                style = MaterialTheme.typography.labelMedium,
                                color = Secondary500,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = selectedTime.format(DateTimeFormatter.ofPattern("hh:mm a")),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = formatTimeAgo(selectedTime),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

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
            Text(
                text = "Payment Method",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyVerticalGrid(
                columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(120.dp)
            ) {
                items(paymentMethods.size) { index ->
                    val (method, icon) = paymentMethods[index]
                    PaymentMethodCard(
                        method = method,
                        icon = icon,
                        isSelected = addExpenseState.selectedPaymentMethod == method,
                        onClick = { viewModel.updatePaymentMethod(method) }
                    )
                }
            }

            // Error Message
            if (addExpenseState.errorMessage != null) {
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
                            text = addExpenseState.errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Error700
                        )
                    }
                }
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
                gradientColors = listOf(Primary500, Secondary500),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))
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

@Composable
private fun CategorySelectionCard(
    category: `in`.syncboard.planmate.domain.entity.Category,
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
        shape = CategoryCardShape,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                parseColor(category.color).copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected)
            androidx.compose.foundation.BorderStroke(2.dp, parseColor(category.color))
        else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = category.icon,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected)
                    parseColor(category.color)
                else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 2
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
        shape = CategoryCardShape,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Primary100 else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected)
            androidx.compose.foundation.BorderStroke(2.dp, Primary500)
        else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
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
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 1
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
                fontWeight = FontWeight.SemiBold
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

@Composable
private fun LazyVerticalGrid(
    columns: androidx.compose.foundation.lazy.grid.GridCells,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: androidx.compose.foundation.lazy.grid.LazyGridScope.() -> Unit
) {
    androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
        columns = columns,
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        content = content
    )
}