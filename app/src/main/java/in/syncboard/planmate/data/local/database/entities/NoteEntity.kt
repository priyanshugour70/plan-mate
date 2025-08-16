// Path: app/src/main/java/in/syncboard/planmate/data/local/database/entities/NoteEntity.kt

package `in`.syncboard.planmate.data.local.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class NoteEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val content: String,
    val color: String = "#FFFFFF",
    val tags: String = "", // JSON string
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)
