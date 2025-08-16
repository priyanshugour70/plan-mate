// Path: app/src/main/java/in/syncboard/planmate/data/local/database/entities/SettingsEntity.kt

package `in`.syncboard.planmate.data.local.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "settings",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SettingsEntity(
    @PrimaryKey
    val userId: String,
    val currency: String = "INR",
    val language: String = "en",
    val timezone: String = "Asia/Kolkata",
    val dateFormat: String = "dd/MM/yyyy",
    val timeFormat: String = "HH:mm",
    val enableNotifications: Boolean = true,
    val enableBudgetAlerts: Boolean = true,
    val enableGoalReminders: Boolean = true,
    val enableBiometricAuth: Boolean = false,
    val autoBackup: Boolean = true,
    val themeMode: String = "SYSTEM",
    val updatedAt: Long
)