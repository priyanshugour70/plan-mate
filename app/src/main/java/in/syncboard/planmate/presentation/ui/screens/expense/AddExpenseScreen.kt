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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import `in`.syncboard.planmate.presentation.ui.components.CustomTextField
import `in`.syncboard.planmate.presentation.ui.components.GradientButton
import `in`.syncboard.planmate.ui.theme.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Add Expense Screen
 * Allows users to add new expenses with details
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onNavigateBack: () -> Unit,
    onExpenseSaved: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    // Form state
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Food & Dining") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }
    var location by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("Card") }

    // UI state
    var isDatePickerOpen by remember { mutableStateOf(false) }
    var isTimePickerOpen by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    // Categories with icons and colors
    val categories = listOf(
        Triple("Food & Dining", "🍕", FoodColor),
        Triple("Transportation", "🚗", TransportColor),
        Triple("Shopping", "🛍️", ShoppingColor),
        Triple("Entertainment", "🎮", EntertainmentColor),
        Triple("Health", "🏥", HealthColor),
        Triple("Bills & Utilities", "⚡", BillsColor),
        Triple("Education", "📚", EducationColor),
        Triple("Travel", "✈️", TravelColor)
    )

    // Payment methods
    val paymentMethods = listOf(
        Pair("Cash", "💵"),
        Pair("Card", "💳"),
        Pair("UPI", "📱"),
        Pair("Wallet", "👛")
    )

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
                            // Save expense
                            if (amount.isNotBlank() && description.isNotBlank()) {
                                isSaving = true
                                // Simulate saving delay
                                coroutineScope.launch {
                                    delay(1500)
                                    onExpenseSaved()
                                }
                            }
                        },
                        enabled = amount.isNotBlank() && description.isNotBlank() && !isSaving
                    ) {
                        Text(
                            "Save",
                            fontWeight = FontWeight.Medium,
                            color = if (amount.isNotBlank() && description.isNotBlank()) Primary500 else MaterialTheme.colorScheme.onSurfaceVariant
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
                )
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

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "₹",
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            placeholder = { Text("0") },
                            textStyle = MaterialTheme.typography.displaySmall.copy(
                                textAlign = TextAlign.Center
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            modifier = Modifier.width(200.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Basic Details Section
            Text(
                text = "Basic Details",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Description
            CustomTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description",
                placeholder = "What did you spend on?",
                leadingIcon = Icons.Default.Edit,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category Selection
            Text(
                text = "Category",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Category Grid
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { (category, icon, color) ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .selectable(
                                        selected = selectedCategory == category,
                                        onClick = { selectedCategory = category }
                                    ),
                                shape = CategoryCardShape,
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedCategory == category) color.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                                ),
                                border = if (selectedCategory == category)
                                    androidx.compose.foundation.BorderStroke(2.dp, color) else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = icon,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    Text(
                                        text = category,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (selectedCategory == category) color else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (selectedCategory == category) FontWeight.Medium else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Date and Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Date
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isDatePickerOpen = true },
                    shape = InputShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = "Date",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Date",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Time
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isTimePickerOpen = true },
                    shape = InputShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = "Time",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Time",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Location (Optional)
            CustomTextField(
                value = location,
                onValueChange = { location = it },
                label = "Location (Optional)",
                placeholder = "Where did you spend?",
                leadingIcon = Icons.Default.LocationOn,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Notes (Optional)
            CustomTextField(
                value = notes,
                onValueChange = { notes = it },
                label = "Notes (Optional)",
                placeholder = "Add any additional notes...",
                leadingIcon = Icons.Default.Notes,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Receipt Upload (Placeholder)
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
                        Icons.Default.CameraAlt,
                        contentDescription = "Camera",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add Receipt Photo",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap to capture or upload receipt",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Payment Method
            Text(
                text = "Payment Method",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                paymentMethods.forEach { (method, icon) ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .selectable(
                                selected = selectedPaymentMethod == method,
                                onClick = { selectedPaymentMethod = method }
                            ),
                        shape = CategoryCardShape,
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedPaymentMethod == method) Primary100 else MaterialTheme.colorScheme.surface
                        ),
                        border = if (selectedPaymentMethod == method)
                            androidx.compose.foundation.BorderStroke(2.dp, Primary500) else null
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = icon,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = method,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (selectedPaymentMethod == method) Primary700 else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (selectedPaymentMethod == method) FontWeight.Medium else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save Button
            GradientButton(
                text = if (isSaving) "Saving..." else "Save Expense",
                onClick = {
                    if (amount.isNotBlank() && description.isNotBlank()) {
                        isSaving = true
                        // Simulate saving delay
                        coroutineScope.launch {
                            delay(1500)
                            onExpenseSaved()
                        }
                    }
                },
                enabled = amount.isNotBlank() && description.isNotBlank() && !isSaving,
                gradientColors = listOf(Primary500, Secondary500),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Date Picker Dialog
    if (isDatePickerOpen) {
        DatePickerDialog(
            onDateSelected = { date ->
                selectedDate = date
                isDatePickerOpen = false
            },
            onDismiss = { isDatePickerOpen = false }
        )
    }

    // Time Picker Dialog
    if (isTimePickerOpen) {
        TimePickerDialog(
            onTimeSelected = { time ->
                selectedTime = time
                isTimePickerOpen = false
            },
            onDismiss = { isTimePickerOpen = false }
        )
    }
}

@Composable
private fun DatePickerDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    // Simple date picker implementation
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Date") },
        text = { Text("Date picker will be implemented here") },
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(LocalDate.now())
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun TimePickerDialog(
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    // Simple time picker implementation
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Time") },
        text = { Text("Time picker will be implemented here") },
        confirmButton = {
            TextButton(onClick = {
                onTimeSelected(LocalTime.now())
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}