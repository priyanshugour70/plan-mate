// Path: app/src/main/java/in/syncboard/planmate/data/repository/AuthRepositoryImpl.kt

package `in`.syncboard.planmate.data.repository

import kotlinx.coroutines.flow.first
import `in`.syncboard.planmate.data.local.database.dao.UserDao
import `in`.syncboard.planmate.data.local.database.dao.SettingsDao
import `in`.syncboard.planmate.data.local.database.dao.CategoryDao
import `in`.syncboard.planmate.data.local.database.entities.UserEntity
import `in`.syncboard.planmate.data.local.database.entities.SettingsEntity
import `in`.syncboard.planmate.data.local.database.entities.CategoryEntity
import `in`.syncboard.planmate.data.local.preferences.PreferencesManager
import `in`.syncboard.planmate.domain.repository.AuthRepository
import `in`.syncboard.planmate.domain.entity.User
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val settingsDao: SettingsDao,
    private val categoryDao: CategoryDao,
    private val preferencesManager: PreferencesManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val userEntity = userDao.getUserByEmail(email)
            if (userEntity != null && verifyPassword(password, userEntity.passwordHash)) {
                // Save login state
                preferencesManager.saveLoginState(userEntity.id, true)
                Result.success(userEntity.toDomainModel())
            } else {
                Result.failure(Exception("Invalid email or password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(name: String, email: String, phone: String, password: String): Result<User> {
        return try {
            // Check if email already exists
            if (userDao.isEmailExists(email)) {
                return Result.failure(Exception("Email already exists"))
            }

            val userId = UUID.randomUUID().toString()
            val currentTime = System.currentTimeMillis()

            val userEntity = UserEntity(
                id = userId,
                name = name,
                email = email,
                phoneNumber = phone,
                passwordHash = hashPassword(password),
                createdAt = currentTime,
                updatedAt = currentTime
            )

            userDao.insertUser(userEntity)

            // Create default settings for user
            val defaultSettings = SettingsEntity(
                userId = userId,
                updatedAt = currentTime
            )
            settingsDao.insertSettings(defaultSettings)

            // Copy default categories for the user
            createUserCategories(userId)

            // Save login state
            preferencesManager.saveLoginState(userId, true)

            Result.success(userEntity.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            preferencesManager.clearLoginState()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): Result<User?> {
        return try {
            val userId = preferencesManager.getCurrentUserId()
            if (userId != null) {
                val userEntity = userDao.getUserById(userId)
                Result.success(userEntity?.toDomainModel())
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return preferencesManager.isLoggedIn()
    }

    override suspend fun refreshToken(): Result<String> {
        // For local database, we don't need token refresh
        return Result.success("local_token")
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            val userEntity = userDao.getUserByEmail(email)
            if (userEntity != null) {
                // In a real app, you would send an email here
                Result.success(Unit)
            } else {
                Result.failure(Exception("Email not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> {
        return try {
            val userId = preferencesManager.getCurrentUserId()
            if (userId != null) {
                val userEntity = userDao.getUserById(userId)
                if (userEntity != null && verifyPassword(oldPassword, userEntity.passwordHash)) {
                    val updatedUser = userEntity.copy(
                        passwordHash = hashPassword(newPassword),
                        updatedAt = System.currentTimeMillis()
                    )
                    userDao.updateUser(updatedUser)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Invalid current password"))
                }
            } else {
                Result.failure(Exception("User not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyEmail(token: String): Result<Unit> {
        // For local app, we'll just mark email as verified
        return try {
            val userId = preferencesManager.getCurrentUserId()
            if (userId != null) {
                val userEntity = userDao.getUserById(userId)
                if (userEntity != null) {
                    val updatedUser = userEntity.copy(
                        isEmailVerified = true,
                        updatedAt = System.currentTimeMillis()
                    )
                    userDao.updateUser(updatedUser)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("User not found"))
                }
            } else {
                Result.failure(Exception("User not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resendVerificationEmail(): Result<Unit> {
        // For local app, this is a no-op
        return Result.success(Unit)
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray())
        return hash.fold("") { str, it -> str + "%02x".format(it) }
    }

    private fun verifyPassword(password: String, hash: String): Boolean {
        return hashPassword(password) == hash
    }

    private suspend fun createUserCategories(userId: String) {
        val defaultCategories = categoryDao.getDefaultCategories("EXPENSE") +
                categoryDao.getDefaultCategories("INCOME")

        val userCategories = defaultCategories.map { defaultCategory ->
            defaultCategory.copy(
                id = UUID.randomUUID().toString(),
                userId = userId,
                isDefault = false
            )
        }

        categoryDao.insertCategories(userCategories)
    }
}

// Extension function to convert Entity to Domain model
private fun UserEntity.toDomainModel(): User {
    return User(
        id = id,
        name = name,
        email = email,
        phoneNumber = phoneNumber,
        profilePictureUrl = profilePictureUrl,
        currency = currency,
        timezone = timezone,
        isEmailVerified = isEmailVerified,
        isPhoneVerified = isPhoneVerified,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
