// Path: app/src/main/java/in/syncboard/planmate/data/local/database/entities/BudgetEntity.kt

package `in`.syncboard.planmate.data.local.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "budgets",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BudgetEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val categoryId: String,
    val allocatedAmount: Double,
    val spentAmount: Double = 0.0,
    val period: String, // MONTHLY, WEEKLY, etc.
    val startDate: Long,
    val endDate: Long,
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)