// Path: app/src/main/java/in/syncboard/planmate/data/local/database/entities/TransactionEntity.kt

package `in`.syncboard.planmate.data.local.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
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
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val categoryId: String,
    val amount: Double,
    val type: String, // INCOME or EXPENSE
    val title: String,
    val description: String? = null,
    val location: String? = null,
    val receiptUrl: String? = null,
    val paymentMethod: String,
    val tags: String = "", // JSON string of tags
    val transactionDate: Long,
    val createdAt: Long,
    val updatedAt: Long
)