// Path: app/src/main/java/in/syncboard/planmate/presentation/ui/screens/reminder/ReminderScreen.kt

package `in`.syncboard.planmate.presentation.ui.screens.reminder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.syncboard.planmate.presentation.ui.components.LoadingState
import `in`.syncboard.planmate.presentation.viewmodel.ReminderViewModel
import `in`.syncboard.planmate.presentation.viewmodel.ReminderItem
import `in`.syncboard.planmate.presentation.viewmodel.NoteItem
import `in`.syncboard.planmate.domain.entity.ReminderPriority
import `in`.syncboard.planmate.domain.entity.ReminderCategory
import `in`.syncboard.planmate.ui.theme.*

/**
 * Reminder Screen - Updated with real data
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Reminders", "Notes")
    var showAddReminderDialog by remember { mutableStateOf(false) }
    var showAddNoteDialog by remember { mutableStateOf(false) }

    // Show loading state
    if (uiState.isLoading) {
        LoadingState(message = "Loading reminders...")
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Reminders & Notes",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (selectedTab == 0) {
                                showAddReminderDialog = true
                            } else {
                                showAddNoteDialog = true
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) {
                        showAddReminderDialog = true
                    } else {
                        showAddNoteDialog = true
                    }
                },
                containerColor = Primary500,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Quick Stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Pending Reminders
                Card(
                    modifier = Modifier.weight(1f),
                    shape = CategoryCardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Warning50
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.pendingReminders.toString(),
                            style = MaterialTheme.typography.amountMedium,
                            color = Warning700,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Pending",
                            style = MaterialTheme.typography.bodySmall,
                            color = Warning600
                        )
                    }
                }

                // Completed Reminders
                Card(
                    modifier = Modifier.weight(1f),
                    shape = CategoryCardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Tertiary50
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.completedReminders.toString(),
                            style = MaterialTheme.typography.amountMedium,
                            color = Tertiary700,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Completed",
                            style = MaterialTheme.typography.bodySmall,
                            color = Tertiary600
                        )
                    }
                }

                // Total Notes
                Card(
                    modifier = Modifier.weight(1f),
                    shape = CategoryCardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Primary50
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.totalNotes.toString(),
                            style = MaterialTheme.typography.amountMedium,
                            color = Primary700,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Notes",
                            style = MaterialTheme.typography.bodySmall,
                            color = Primary600
                        )
                    }
                }
            }

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Primary500
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Content based on selected tab
            when (selectedTab) {
                0 -> {
                    // Reminders Tab
                    if (uiState.reminders.isEmpty()) {
                        EmptyRemindersContent()
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            items(uiState.reminders) { reminder ->
                                ReminderCard(
                                    reminder = reminder,
                                    onToggleComplete = { viewModel.toggleReminderCompletion(reminder.id) },
                                    onDelete = { viewModel.deleteReminder(reminder.id) }
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
                1 -> {
                    // Notes Tab
                    if (uiState.notes.isEmpty()) {
                        EmptyNotesContent()
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            items(uiState.notes) { note ->
                                NoteCard(
                                    note = note,
                                    onPin = { viewModel.pinNote(note.id) },
                                    onDelete = { viewModel.deleteNote(note.id) }
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Reminder Dialog
    if (showAddReminderDialog) {
        AddReminderDialog(
            uiState = viewModel.addReminderState,
            onDismiss = {
                showAddReminderDialog = false
                viewModel.resetReminderForm()
            },
            onTitleChanged = { viewModel.updateReminderTitle(it) },
            onDescriptionChanged = { viewModel.updateReminderDescription(it) },
            onTimeChanged = { viewModel.updateReminderTime(it) },
            onPriorityChanged = { viewModel.updateReminderPriority(it) },
            onCategoryChanged = { viewModel.updateReminderCategory(it) },
            onSave = {
                viewModel.saveReminder {
                    showAddReminderDialog = false
                }
            }
        )
    }

    // Add Note Dialog
    if (showAddNoteDialog) {
        AddNoteDialog(
            uiState = viewModel.addNoteState,
            onDismiss = {
                showAddNoteDialog = false
                viewModel.resetNoteForm()
            },
            onTitleChanged = { viewModel.updateNoteTitle(it) },
            onContentChanged = { viewModel.updateNoteContent(it) },
            onColorChanged = { viewModel.updateNoteColor(it) },
            onSave = {
                viewModel.saveNote {
                    showAddNoteDialog = false
                }
            }
        )
    }
}

/**
 * Reminder Card Component
 */
@Composable
private fun ReminderCard(
    reminder: ReminderItem,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CategoryCardShape,
        colors = CardDefaults.cardColors(
            containerColor = if (reminder.isCompleted) Tertiary50 else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Completion Checkbox
            Checkbox(
                checked = reminder.isCompleted,
                onCheckedChange = { onToggleComplete() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Tertiary500
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (reminder.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (reminder.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Time
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = "Time",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = reminder.time,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Priority Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = getPriorityColor(reminder.priority).copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = reminder.priority.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = getPriorityColor(reminder.priority),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Category Icon
                    Icon(
                        getCategoryIcon(reminder.category),
                        contentDescription = reminder.category.name,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Delete Button
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Reminder") },
            text = { Text("Are you sure you want to delete this reminder?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = Error500)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Note Card Component
 */
@Composable
private fun NoteCard(
    note: NoteItem,
    onPin: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CategoryCardShape,
        colors = CardDefaults.cardColors(
            containerColor = note.color
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                Row {
                    IconButton(
                        onClick = onPin,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            if (note.isPinned) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Pin",
                            modifier = Modifier.size(16.dp),
                            tint = if (note.isPinned) Warning500 else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Content
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 8.dp),
                maxLines = 3
            )

            // Created time
            Text(
                text = note.createdAt,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to delete this note?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = Error500)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Helper functions and dialogs
@Composable
private fun EmptyRemindersContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsNone,
                contentDescription = "No reminders",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No reminders yet",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Create your first reminder to stay organized",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptyNotesContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Note,
                contentDescription = "No notes",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No notes yet",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Create your first note to capture your thoughts",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// Add Reminder Dialog
@Composable
private fun AddReminderDialog(
    uiState: `in`.syncboard.planmate.presentation.viewmodel.AddReminderUiState,
    onDismiss: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onTimeChanged: (String) -> Unit,
    onPriorityChanged: (ReminderPriority) -> Unit,
    onCategoryChanged: (ReminderCategory) -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Reminder") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = onTitleChanged,
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = onDescriptionChanged,
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.selectedTime,
                    onValueChange = onTimeChanged,
                    label = { Text("Time (HH:MM)") },
                    placeholder = { Text("14:30") },
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

// Add Note Dialog
@Composable
private fun AddNoteDialog(
    uiState: `in`.syncboard.planmate.presentation.viewmodel.AddNoteUiState,
    onDismiss: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit,
    onColorChanged: (androidx.compose.ui.graphics.Color) -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Note") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = onTitleChanged,
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.content,
                    onValueChange = onContentChanged,
                    label = { Text("Content") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
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

private fun getPriorityColor(priority: ReminderPriority): androidx.compose.ui.graphics.Color {
    return when (priority) {
        ReminderPriority.HIGH -> Error500
        ReminderPriority.MEDIUM -> Warning500
        ReminderPriority.LOW -> Primary500
    }
}

private fun getCategoryIcon(category: ReminderCategory): androidx.compose.ui.graphics.vector.ImageVector {
    return when (category) {
        ReminderCategory.BILLS -> Icons.Default.Receipt
        ReminderCategory.SHOPPING -> Icons.Default.ShoppingBag
        ReminderCategory.HEALTH -> Icons.Default.LocalHospital
        ReminderCategory.WORK -> Icons.Default.Work
        ReminderCategory.PERSONAL -> Icons.Default.Person
        ReminderCategory.FINANCIAL -> Icons.Default.AccountBalance
        ReminderCategory.GENERAL -> Icons.Default.Star
    }
}