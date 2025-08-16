// Path: app/src/main/java/in/syncboard/planmate/data/local/database/entities/UserEntity.kt

package `in`.syncboard.planmate.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String?,
    val passwordHash: String,
    val profilePictureUrl: String? = null,
    val currency: String = "INR",
    val timezone: String = "Asia/Kolkata",
    val isEmailVerified: Boolean = false,
    val isPhoneVerified: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)