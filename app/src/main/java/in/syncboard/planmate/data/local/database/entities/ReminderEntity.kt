// Path: app/src/main/java/in/syncboard/planmate/data/local/database/entities/ReminderEntity.kt

package `in`.syncboard.planmate.data.local.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ReminderEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val description: String? = null,
    val reminderDate: Long,
    val reminderTime: String,
    val priority: String, // HIGH, MEDIUM, LOW
    val category: String, // BILLS, SHOPPING, etc.
    val isCompleted: Boolean = false,
    val isRecurring: Boolean = false,
    val recurringPattern: String? = null, // JSON string
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)
