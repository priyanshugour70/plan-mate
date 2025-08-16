// ReminderScreen.kt
package `in`.syncboard.planmate.presentation.ui.screens.reminder

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.syncboard.planmate.presentation.ui.components.LoadingState
import `in`.syncboard.planmate.presentation.ui.components.GradientButton
import `in`.syncboard.planmate.presentation.ui.components.CustomTextField
import `in`.syncboard.planmate.presentation.viewmodel.ReminderViewModel
import `in`.syncboard.planmate.presentation.viewmodel.ReminderItem
import `in`.syncboard.planmate.presentation.viewmodel.NoteItem
import `in`.syncboard.planmate.domain.entity.ReminderPriority
import `in`.syncboard.planmate.domain.entity.ReminderCategory
import `in`.syncboard.planmate.ui.theme.*

/**
 * Reminder Screen with Modern UI
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
                ReminderTopBar(
                    onNavigateBack = onNavigateBack,
                    onActionClick = {
                        if (selectedTab == 0) {
                            showAddReminderDialog = true
                        } else {
                            showAddNoteDialog = true
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
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Quick Stats
                ReminderStatsSection(
                    pendingReminders = uiState.pendingReminders,
                    completedReminders = uiState.completedReminders,
                    totalNotes = uiState.totalNotes
                )

                // Tab Row
                ReminderTabRow(
                    selectedTab = selectedTab,
                    tabs = tabs,
                    onTabSelected = { selectedTab = it }
                )

                // Content based on selected tab
                when (selectedTab) {
                    0 -> {
                        // Reminders Tab
                        if (uiState.reminders.isEmpty()) {
                            ReminderEmptyState()
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
                            NoteEmptyState()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderTopBar(
    onNavigateBack: () -> Unit,
    onActionClick: () -> Unit
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
                    text = "Reminders & Notes",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = onActionClick,
                modifier = Modifier
                    .background(
                        Color.White.copy(alpha = 0.1f),
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun ReminderStatsSection(
    pendingReminders: Int,
    completedReminders: Int,
    totalNotes: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Pending Reminders
        StatsCard(
            modifier = Modifier.weight(1f),
            title = "Pending",
            value = pendingReminders.toString(),
            icon = Icons.Default.NotificationImportant,
            color = Warning500
        )

        // Completed Reminders
        StatsCard(
            modifier = Modifier.weight(1f),
            title = "Completed",
            value = completedReminders.toString(),
            icon = Icons.Default.CheckCircle,
            color = Success500
        )

        // Total Notes
        StatsCard(
            modifier = Modifier.weight(1f),
            title = "Notes",
            value = totalNotes.toString(),
            icon = Icons.Default.Note,
            color = Primary500
        )
    }
}

@Composable
private fun StatsCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            color.copy(alpha = 0.1f),
                            color.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    color = color,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = color.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ReminderTabRow(
    selectedTab: Int,
    tabs: List<String>,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab,
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = Primary500,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                color = Primary500,
                height = 3.dp
            )
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == index) Primary600 else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (reminder.isCompleted) Success50 else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Completion Checkbox with animation
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        if (reminder.isCompleted) Success500 else Color.Transparent,
                        CircleShape
                    )
                    .border(
                        2.dp,
                        if (reminder.isCompleted) Success500 else MaterialTheme.colorScheme.outline,
                        CircleShape
                    )
                    .clickable { onToggleComplete() },
                contentAlignment = Alignment.Center
            ){
                androidx.compose.animation.AnimatedVisibility(
                    visible = reminder.isCompleted,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (reminder.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (reminder.isCompleted) TextDecoration.LineThrough
                    else TextDecoration.None,
                    fontWeight = FontWeight.Bold
                )

                if (reminder.description?.isNotEmpty() == true) {
                    Text(
                        text = reminder.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Time
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Secondary100
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.AccessTime,
                                contentDescription = "Time",
                                modifier = Modifier.size(12.dp),
                                tint = Secondary700
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = reminder.time,
                                style = MaterialTheme.typography.labelSmall,
                                color = Secondary700,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Priority Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = getPriorityColor(reminder.priority).copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = reminder.priority.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = getPriorityColor(reminder.priority),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Bold
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
            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .background(
                        Error50,
                        RoundedCornerShape(8.dp)
                    )
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Error500,
                    modifier = Modifier.size(20.dp)
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = note.color
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Row {
                    IconButton(
                        onClick = onPin,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (note.isPinned) Warning100 else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            if (note.isPinned) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Pin",
                            modifier = Modifier.size(18.dp),
                            tint = if (note.isPinned) Warning600 else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                Error50,
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(18.dp),
                            tint = Error500
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Created time
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            ) {
                Text(
                    text = note.createdAt,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
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

// Empty state components
@Composable
private fun ReminderEmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(40.dp)
        ) {
            Box {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .blur(16.dp)
                        .background(
                            Primary500.copy(alpha = 0.3f),
                            CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Primary100, Primary50)
                            ),
                            shape = CircleShape
                        )
                        .zIndex(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = "No reminders",
                        modifier = Modifier.size(40.dp),
                        tint = Primary600
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "No reminders yet",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Create your first reminder to stay organized",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun NoteEmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(40.dp)
        ) {
            Box {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .blur(16.dp)
                        .background(
                            Secondary500.copy(alpha = 0.3f),
                            CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Secondary100, Secondary50)
                            ),
                            shape = CircleShape
                        )
                        .zIndex(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Note,
                        contentDescription = "No notes",
                        modifier = Modifier.size(40.dp),
                        tint = Secondary600
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "No notes yet",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Create your first note to capture your thoughts",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
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
        title = {
            Text(
                "Add Reminder",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CustomTextField(
                    value = uiState.title,
                    onValueChange = onTitleChanged,
                    label = "Title",
                    leadingIcon = Icons.Default.Title
                )

                CustomTextField(
                    value = uiState.description,
                    onValueChange = onDescriptionChanged,
                    label = "Description (Optional)",
                    leadingIcon = Icons.Default.Description
                )

                CustomTextField(
                    value = uiState.selectedTime,
                    onValueChange = onTimeChanged,
                    label = "Time (HH:MM)",
                    placeholder = "14:30",
                    leadingIcon = Icons.Default.AccessTime
                )

                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = Error500
                    )
                }
            }
        },
        confirmButton = {
            GradientButton(
                text = if (uiState.isSaving) "Saving..." else "Save",
                onClick = onSave,
                enabled = uiState.isValidForm && !uiState.isSaving,
                loading = uiState.isSaving,
                modifier = Modifier.width(120.dp).height(40.dp)
            )
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
        title = {
            Text(
                "Add Note",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CustomTextField(
                    value = uiState.title,
                    onValueChange = onTitleChanged,
                    label = "Title",
                    leadingIcon = Icons.Default.Title
                )

                OutlinedTextField(
                    value = uiState.content,
                    onValueChange = onContentChanged,
                    label = { Text("Content") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = Error500
                    )
                }
            }
        },
        confirmButton = {
            GradientButton(
                text = if (uiState.isSaving) "Saving..." else "Save",
                onClick = onSave,
                enabled = uiState.isValidForm && !uiState.isSaving,
                loading = uiState.isSaving,
                modifier = Modifier.width(120.dp).height(40.dp)
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun getPriorityColor(priority: ReminderPriority): Color {
    return when (priority) {
        ReminderPriority.HIGH -> Error500
        ReminderPriority.MEDIUM -> Warning500
        ReminderPriority.LOW -> Primary500
    }
}

private fun getCategoryIcon(category: ReminderCategory): ImageVector {
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