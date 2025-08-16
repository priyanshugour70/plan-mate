// Path: app/src/main/java/in/syncboard/planmate/presentation/viewmodel/ReminderViewModel.kt

package `in`.syncboard.planmate.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import `in`.syncboard.planmate.domain.repository.AuthRepository
import `in`.syncboard.planmate.domain.repository.ReminderRepository
import `in`.syncboard.planmate.domain.repository.NoteRepository
import `in`.syncboard.planmate.domain.entity.*
import java.util.*
import javax.inject.Inject

/**
 * Reminder Item for UI
 */
data class ReminderItem(
    val id: String,
    val title: String,
    val description: String? = null,
    val time: String,
    val priority: ReminderPriority,
    val category: ReminderCategory = ReminderCategory.GENERAL,
    val isCompleted: Boolean = false,
    val isRecurring: Boolean = false
)

/**
 * Note Item for UI
 */
data class NoteItem(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: String,
    val color: androidx.compose.ui.graphics.Color,
    val isPinned: Boolean = false
)

/**
 * UI State for Reminder Screen
 */
data class ReminderUiState(
    val isLoading: Boolean = true,
    val reminders: List<ReminderItem> = emptyList(),
    val notes: List<NoteItem> = emptyList(),
    val pendingReminders: Int = 0,
    val completedReminders: Int = 0,
    val totalNotes: Int = 0,
    val errorMessage: String? = null,
    val currentUserId: String = ""
)

/**
 * Add Reminder UI State
 */
data class AddReminderUiState(
    val title: String = "",
    val description: String = "",
    val selectedDate: Long = System.currentTimeMillis(),
    val selectedTime: String = "",
    val priority: ReminderPriority = ReminderPriority.MEDIUM,
    val category: ReminderCategory = ReminderCategory.GENERAL,
    val isRecurring: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isValidForm: Boolean = false
)

/**
 * Add Note UI State
 */
data class AddNoteUiState(
    val title: String = "",
    val content: String = "",
    val selectedColor: androidx.compose.ui.graphics.Color = `in`.syncboard.planmate.ui.theme.Primary100,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isValidForm: Boolean = false
)

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val reminderRepository: ReminderRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    var uiState by mutableStateOf(ReminderUiState())
        private set

    var addReminderState by mutableStateOf(AddReminderUiState())
        private set

    var addNoteState by mutableStateOf(AddNoteUiState())
        private set

    init {
        loadUserAndData()
    }

    private fun loadUserAndData() {
        viewModelScope.launch {
            authRepository.getCurrentUser().fold(
                onSuccess = { user ->
                    if (user != null) {
                        uiState = uiState.copy(currentUserId = user.id)
                        loadReminders(user.id)
                        loadNotes(user.id)
                    } else {
                        uiState = uiState.copy(
                            isLoading = false,
                            errorMessage = "User not found"
                        )
                    }
                },
                onFailure = { exception ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Failed to load user"
                    )
                }
            )
        }
    }

    private suspend fun loadReminders(userId: String) {
        reminderRepository.getRemindersByUser(userId).fold(
            onSuccess = { reminders ->
                val reminderItems = reminders.map { reminder ->
                    ReminderItem(
                        id = reminder.id,
                        title = reminder.title,
                        description = reminder.description,
                        time = reminder.reminderTime,
                        priority = reminder.priority,
                        category = reminder.category,
                        isCompleted = reminder.isCompleted,
                        isRecurring = reminder.isRecurring
                    )
                }

                val pendingCount = reminderItems.count { !it.isCompleted }
                val completedCount = reminderItems.count { it.isCompleted }

                uiState = uiState.copy(
                    reminders = reminderItems,
                    pendingReminders = pendingCount,
                    completedReminders = completedCount
                )
            },
            onFailure = { exception ->
                uiState = uiState.copy(
                    errorMessage = exception.message ?: "Failed to load reminders"
                )
            }
        )
    }

    private suspend fun loadNotes(userId: String) {
        noteRepository.getNotesByUser(userId).fold(
            onSuccess = { notes ->
                val noteItems = notes.map { note ->
                    NoteItem(
                        id = note.id,
                        title = note.title,
                        content = note.content,
                        createdAt = formatRelativeTime(note.createdAt),
                        color = parseColor(note.color),
                        isPinned = note.isPinned
                    )
                }

                uiState = uiState.copy(
                    notes = noteItems,
                    totalNotes = noteItems.size,
                    isLoading = false
                )
            },
            onFailure = { exception ->
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Failed to load notes"
                )
            }
        )
    }

    // Reminder Functions
    fun updateReminderTitle(title: String) {
        addReminderState = addReminderState.copy(title = title)
        validateReminderForm()
    }

    fun updateReminderDescription(description: String) {
        addReminderState = addReminderState.copy(description = description)
    }

    fun updateReminderDate(date: Long) {
        addReminderState = addReminderState.copy(selectedDate = date)
    }

    fun updateReminderTime(time: String) {
        addReminderState = addReminderState.copy(selectedTime = time)
        validateReminderForm()
    }

    fun updateReminderPriority(priority: ReminderPriority) {
        addReminderState = addReminderState.copy(priority = priority)
    }

    fun updateReminderCategory(category: ReminderCategory) {
        addReminderState = addReminderState.copy(category = category)
    }

    fun updateReminderRecurring(isRecurring: Boolean) {
        addReminderState = addReminderState.copy(isRecurring = isRecurring)
    }

    private fun validateReminderForm() {
        val isValid = addReminderState.title.isNotBlank() &&
                addReminderState.selectedTime.isNotBlank()

        addReminderState = addReminderState.copy(isValidForm = isValid)
    }

    fun saveReminder(onSuccess: () -> Unit) {
        if (!addReminderState.isValidForm || uiState.currentUserId.isEmpty()) return

        viewModelScope.launch {
            addReminderState = addReminderState.copy(isSaving = true)

            try {
                val reminder = Reminder(
                    id = UUID.randomUUID().toString(),
                    userId = uiState.currentUserId,
                    title = addReminderState.title,
                    description = addReminderState.description.ifBlank { null },
                    reminderDate = addReminderState.selectedDate,
                    reminderTime = addReminderState.selectedTime,
                    priority = addReminderState.priority,
                    category = addReminderState.category,
                    isCompleted = false,
                    isRecurring = addReminderState.isRecurring,
                    recurringPattern = null, // Would be implemented later
                    isActive = true,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                reminderRepository.createReminder(reminder).fold(
                    onSuccess = {
                        addReminderState = AddReminderUiState() // Reset form
                        loadReminders(uiState.currentUserId) // Refresh data
                        onSuccess()
                    },
                    onFailure = { exception ->
                        addReminderState = addReminderState.copy(
                            isSaving = false,
                            errorMessage = exception.message ?: "Failed to save reminder"
                        )
                    }
                )
            } catch (e: Exception) {
                addReminderState = addReminderState.copy(
                    isSaving = false,
                    errorMessage = "Failed to save reminder"
                )
            }
        }
    }

    fun toggleReminderCompletion(reminderId: String) {
        viewModelScope.launch {
            reminderRepository.markReminderCompleted(reminderId).fold(
                onSuccess = {
                    if (uiState.currentUserId.isNotEmpty()) {
                        loadReminders(uiState.currentUserId)
                    }
                },
                onFailure = { exception ->
                    uiState = uiState.copy(
                        errorMessage = exception.message ?: "Failed to update reminder"
                    )
                }
            )
        }
    }

    fun deleteReminder(reminderId: String) {
        viewModelScope.launch {
            reminderRepository.deleteReminder(reminderId).fold(
                onSuccess = {
                    if (uiState.currentUserId.isNotEmpty()) {
                        loadReminders(uiState.currentUserId)
                    }
                },
                onFailure = { exception ->
                    uiState = uiState.copy(
                        errorMessage = exception.message ?: "Failed to delete reminder"
                    )
                }
            )
        }
    }

    // Note Functions
    fun updateNoteTitle(title: String) {
        addNoteState = addNoteState.copy(title = title)
        validateNoteForm()
    }

    fun updateNoteContent(content: String) {
        addNoteState = addNoteState.copy(content = content)
        validateNoteForm()
    }

    fun updateNoteColor(color: androidx.compose.ui.graphics.Color) {
        addNoteState = addNoteState.copy(selectedColor = color)
    }

    private fun validateNoteForm() {
        val isValid = addNoteState.title.isNotBlank() &&
                addNoteState.content.isNotBlank()

        addNoteState = addNoteState.copy(isValidForm = isValid)
    }

    fun saveNote(onSuccess: () -> Unit) {
        if (!addNoteState.isValidForm || uiState.currentUserId.isEmpty()) return

        viewModelScope.launch {
            addNoteState = addNoteState.copy(isSaving = true)

            try {
                val note = Note(
                    id = UUID.randomUUID().toString(),
                    userId = uiState.currentUserId,
                    title = addNoteState.title,
                    content = addNoteState.content,
                    color = colorToHex(addNoteState.selectedColor),
                    tags = emptyList(),
                    isPinned = false,
                    isArchived = false,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                noteRepository.createNote(note).fold(
                    onSuccess = {
                        addNoteState = AddNoteUiState() // Reset form
                        loadNotes(uiState.currentUserId) // Refresh data
                        onSuccess()
                    },
                    onFailure = { exception ->
                        addNoteState = addNoteState.copy(
                            isSaving = false,
                            errorMessage = exception.message ?: "Failed to save note"
                        )
                    }
                )
            } catch (e: Exception) {
                addNoteState = addNoteState.copy(
                    isSaving = false,
                    errorMessage = "Failed to save note"
                )
            }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            noteRepository.deleteNote(noteId).fold(
                onSuccess = {
                    if (uiState.currentUserId.isNotEmpty()) {
                        loadNotes(uiState.currentUserId)
                    }
                },
                onFailure = { exception ->
                    uiState = uiState.copy(
                        errorMessage = exception.message ?: "Failed to delete note"
                    )
                }
            )
        }
    }

    fun pinNote(noteId: String) {
        viewModelScope.launch {
            noteRepository.pinNote(noteId).fold(
                onSuccess = {
                    if (uiState.currentUserId.isNotEmpty()) {
                        loadNotes(uiState.currentUserId)
                    }
                },
                onFailure = { exception ->
                    uiState = uiState.copy(
                        errorMessage = exception.message ?: "Failed to pin note"
                    )
                }
            )
        }
    }

    private fun formatRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60_000 -> "Just now"
            diff < 3_600_000 -> "${diff / 60_000}m ago"
            diff < 86_400_000 -> "${diff / 3_600_000}h ago"
            diff < 2_592_000_000 -> "${diff / 86_400_000}d ago"
            else -> {
                val date = Date(timestamp)
                java.text.SimpleDateFormat("MMM dd", Locale.getDefault()).format(date)
            }
        }
    }

    private fun parseColor(colorString: String): androidx.compose.ui.graphics.Color {
        return try {
            androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(colorString))
        } catch (e: Exception) {
            `in`.syncboard.planmate.ui.theme.Primary100
        }
    }

    private fun colorToHex(color: androidx.compose.ui.graphics.Color): String {
        val red = (color.red * 255).toInt()
        val green = (color.green * 255).toInt()
        val blue = (color.blue * 255).toInt()
        return String.format("#%02X%02X%02X", red, green, blue)
    }

    fun resetReminderForm() {
        addReminderState = AddReminderUiState()
    }

    fun resetNoteForm() {
        addNoteState = AddNoteUiState()
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
        addReminderState = addReminderState.copy(errorMessage = null)
        addNoteState = addNoteState.copy(errorMessage = null)
    }

    fun refreshData() {
        if (uiState.currentUserId.isNotEmpty()) {
            viewModelScope.launch {
                loadReminders(uiState.currentUserId)
                loadNotes(uiState.currentUserId)
            }
        }
    }
}