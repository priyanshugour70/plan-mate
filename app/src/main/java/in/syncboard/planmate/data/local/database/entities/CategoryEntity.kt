// Path: app/src/main/java/in/syncboard/planmate/data/local/database/entities/CategoryEntity.kt

package `in`.syncboard.planmate.data.local.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "categories",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val name: String,
    val type: String, // INCOME or EXPENSE
    val icon: String,
    val color: String,
    val isDefault: Boolean = false,
    val parentCategoryId: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)