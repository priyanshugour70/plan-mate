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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import `in`.syncboard.planmate.ui.theme.*

/**
 * Reminder Data Class
 */
data class Reminder(
    val id: String,
    val title: String,
    val time: String,
    val priority: ReminderPriority,
    val isCompleted: Boolean = false,
    val category: ReminderCategory = ReminderCategory.GENERAL
)

/**
 * Note Data Class
 */
data class Note(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: String,
    val color: Color
)

/**
 * Reminder Priority Enum
 */
enum class ReminderPriority(val displayName: String, val color: Color) {
    HIGH("High", Error500),
    MEDIUM("Medium", Warning500),
    LOW("Low", Primary500)
}

/**
 * Reminder Category Enum
 */
enum class ReminderCategory(val displayName: String, val icon: ImageVector) {
    BILLS("Bills", Icons.Default.Receipt),
    SHOPPING("Shopping", Icons.Default.ShoppingBag),
    HEALTH("Health", Icons.Default.LocalHospital),
    WORK("Work", Icons.Default.Work),
    GENERAL("General", Icons.Default.Star)
}

/**
 * Reminder Screen
 * Shows reminders, tasks, and notes for better life organization
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Reminders", "Notes")

    // Mock data for reminders
    val reminders = remember {
        mutableStateListOf(
            Reminder("1", "Pay electricity bill", "2:00 PM", ReminderPriority.HIGH, false, ReminderCategory.BILLS),
            Reminder("2", "Buy groceries", "4:30 PM", ReminderPriority.MEDIUM, false, ReminderCategory.SHOPPING),
            Reminder("3", "Call insurance agent", "6:00 PM", ReminderPriority.LOW, true, ReminderCategory.GENERAL),
            Reminder("4", "Doctor appointment", "10:00 AM", ReminderPriority.HIGH, false, ReminderCategory.HEALTH),
            Reminder("5", "Submit monthly report", "5:00 PM", ReminderPriority.MEDIUM, false, ReminderCategory.WORK)
        )
    }

    // Mock data for notes
    val notes = remember {
        listOf(
            Note("1", "Budget Review Meeting", "Discuss Q2 budget allocation and expense optimization strategies for better financial management...", "2 hours ago", Warning100),
            Note("2", "Investment Ideas", "Research mutual funds and SIP options for long-term savings. Consider ELSS for tax benefits...", "1 day ago", Primary100),
            Note("3", "Shopping List", "Milk, Bread, Vegetables, Cleaning supplies, Coffee beans, Fruits for the week...", "2 days ago", Tertiary100),
            Note("4", "Travel Planning", "Plan summer vacation budget. Research destinations, accommodation, and activities within budget...", "3 days ago", Secondary100)
        )
    }

    val pendingReminders = reminders.count { !it.isCompleted }
    val completedReminders = reminders.count { it.isCompleted }

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
                    IconButton(onClick = { /* Handle add */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Handle add reminder/note */ },
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
                            text = pendingReminders.toString(),
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
                            text = completedReminders.toString(),
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
                            text = notes.size.toString(),
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
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Today's Reminders Header
                        item {
                            Text(
                                text = "Today's Reminders",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Reminders List
                        items(reminders) { reminder ->
                            ReminderCard(
                                reminder = reminder,
                                onToggleComplete = { reminderId ->
                                    val index = reminders.indexOfFirst { it.id == reminderId }
                                    if (index != -1) {
                                        reminders[index] = reminders[index].copy(isCompleted = !reminders[index].isCompleted)
                                    }
                                },
                                onEdit = { /* Handle edit */ },
                                onDelete = { /* Handle delete */ }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
                        }
                    }
                }
                1 -> {
                    // Notes Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Quick Notes Header
                        item {
                            Text(
                                text = "Quick Notes",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Notes List
                        items(notes) { note ->
                            NoteCard(
                                note = note,
                                onEdit = { /* Handle edit */ },
                                onDelete = { /* Handle delete */ }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
                        }
                    }
                }
            }
        }
    }
}

/**
 * Reminder Card Component
 */
@Composable
private fun ReminderCard(
    reminder: Reminder,
    onToggleComplete: (String) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                onCheckedChange = { onToggleComplete(reminder.id) },
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
                        color = reminder.priority.color.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = reminder.priority.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = reminder.priority.color,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Category Icon
                    Icon(
                        reminder.category.icon,
                        contentDescription = reminder.category.displayName,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Actions
            IconButton(onClick = { /* Handle more actions */ }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Note Card Component
 */
@Composable
private fun NoteCard(
    note: Note,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CategoryCardShape,
        colors = CardDefaults.cardColors(
            containerColor = note.color
        ),
        onClick = onEdit
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

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Content
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
}