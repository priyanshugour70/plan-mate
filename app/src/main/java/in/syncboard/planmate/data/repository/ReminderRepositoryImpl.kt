// Path: app/src/main/java/in/syncboard/planmate/data/repository/ReminderRepositoryImpl.kt

package `in`.syncboard.planmate.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import `in`.syncboard.planmate.data.local.database.dao.ReminderDao
import `in`.syncboard.planmate.data.local.database.entities.ReminderEntity
import `in`.syncboard.planmate.domain.repository.ReminderRepository
import `in`.syncboard.planmate.domain.entity.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepositoryImpl @Inject constructor(
    private val reminderDao: ReminderDao
) : ReminderRepository {

    override suspend fun createReminder(reminder: Reminder): Result<Reminder> {
        return try {
            val reminderEntity = reminder.toEntity()
            reminderDao.insertReminder(reminderEntity)
            Result.success(reminder)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReminderById(reminderId: String): Result<Reminder?> {
        return try {
            val reminderEntity = reminderDao.getReminderById(reminderId)
            Result.success(reminderEntity?.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRemindersByUser(userId: String): Result<List<Reminder>> {
        return try {
            val reminders = reminderDao.getRemindersByUser(userId)
            Result.success(reminders.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUpcomingReminders(userId: String, days: Int): Result<List<Reminder>> {
        return try {
            val currentTime = System.currentTimeMillis()
            val endTime = currentTime + (days * 24L * 60L * 60L * 1000L)
            val reminders = reminderDao.getUpcomingReminders(userId, currentTime, endTime)
            Result.success(reminders.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPendingReminders(userId: String): Result<List<Reminder>> {
        return try {
            val reminders = reminderDao.getPendingReminders(userId)
            Result.success(reminders.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateReminder(reminder: Reminder): Result<Reminder> {
        return try {
            val reminderEntity = reminder.toEntity()
            reminderDao.updateReminder(reminderEntity)
            Result.success(reminder)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markReminderCompleted(reminderId: String): Result<Reminder> {
        return try {
            reminderDao.markReminderCompleted(reminderId)
            val updatedReminder = reminderDao.getReminderById(reminderId)
            if (updatedReminder != null) {
                Result.success(updatedReminder.toDomainModel())
            } else {
                Result.failure(Exception("Reminder not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteReminder(reminderId: String): Result<Unit> {
        return try {
            val reminderEntity = reminderDao.getReminderById(reminderId)
            if (reminderEntity != null) {
                reminderDao.deleteReminder(reminderEntity)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Reminder not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeReminders(userId: String): Flow<List<Reminder>> {
        return reminderDao.observeReminders(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
}

// Extension functions
private fun Reminder.toEntity(): ReminderEntity {
    return ReminderEntity(
        id = id,
        userId = userId,
        title = title,
        description = description,
        reminderDate = reminderDate,
        reminderTime = reminderTime,
        priority = priority.name,
        category = category.name,
        isCompleted = isCompleted,
        isRecurring = isRecurring,
        recurringPattern = recurringPattern?.let { "${it.type.name}:${it.interval}" },
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

private fun ReminderEntity.toDomainModel(): Reminder {
    return Reminder(
        id = id,
        userId = userId,
        title = title,
        description = description,
        reminderDate = reminderDate,
        reminderTime = reminderTime,
        priority = ReminderPriority.valueOf(priority),
        category = ReminderCategory.valueOf(category),
        isCompleted = isCompleted,
        isRecurring = isRecurring,
        recurringPattern = recurringPattern?.let {
            val parts = it.split(":")
            if (parts.size == 2) {
                RecurringPattern(
                    type = RecurringType.valueOf(parts[0]),
                    interval = parts[1].toIntOrNull() ?: 1
                )
            } else null
        },
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
