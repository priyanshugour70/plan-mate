// Path: app/src/main/java/in/syncboard/planmate/data/repository/UserRepositoryImpl.kt

package `in`.syncboard.planmate.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import `in`.syncboard.planmate.data.local.database.dao.UserDao
import `in`.syncboard.planmate.data.local.database.entities.UserEntity
import `in`.syncboard.planmate.domain.repository.UserRepository
import `in`.syncboard.planmate.domain.entity.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun createUser(user: User): Result<User> {
        return try {
            val userEntity = user.toEntity()
            userDao.insertUser(userEntity)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserById(userId: String): Result<User?> {
        return try {
            val userEntity = userDao.getUserById(userId)
            Result.success(userEntity?.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserByEmail(email: String): Result<User?> {
        return try {
            val userEntity = userDao.getUserByEmail(email)
            Result.success(userEntity?.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(user: User): Result<User> {
        return try {
            val userEntity = user.toEntity()
            userDao.updateUser(userEntity)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            val userEntity = userDao.getUserById(userId)
            if (userEntity != null) {
                userDao.deleteUser(userEntity)
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isEmailExists(email: String): Result<Boolean> {
        return try {
            val exists = userDao.isEmailExists(email)
            Result.success(exists)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeUser(userId: String): Flow<User?> {
        return userDao.observeUser(userId).map { it?.toDomainModel() }
    }
}

// Extension functions for conversion
private fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        phoneNumber = phoneNumber,
        passwordHash = "", // This should be handled by AuthRepository
        profilePictureUrl = profilePictureUrl,
        currency = currency,
        timezone = timezone,
        isEmailVerified = isEmailVerified,
        isPhoneVerified = isPhoneVerified,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

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
