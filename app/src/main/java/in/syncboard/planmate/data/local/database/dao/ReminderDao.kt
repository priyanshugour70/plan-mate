// Path: app/src/main/java/in/syncboard/planmate/data/local/database/dao/ReminderDao.kt

package `in`.syncboard.planmate.data.local.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import `in`.syncboard.planmate.data.local.database.entities.ReminderEntity

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE userId = :userId AND isActive = 1 ORDER BY reminderDate ASC")
    suspend fun getRemindersByUser(userId: String): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE id = :reminderId")
    suspend fun getReminderById(reminderId: String): ReminderEntity?

    @Query("SELECT * FROM reminders WHERE userId = :userId AND reminderDate BETWEEN :startDate AND :endDate AND isActive = 1 ORDER BY reminderDate ASC")
    suspend fun getUpcomingReminders(userId: String, startDate: Long, endDate: Long): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE userId = :userId AND isCompleted = 0 AND isActive = 1 ORDER BY reminderDate ASC")
    suspend fun getPendingReminders(userId: String): List<ReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity): Int

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity): Int

    @Query("UPDATE reminders SET isCompleted = 1 WHERE id = :reminderId")
    suspend fun markReminderCompleted(reminderId: String): Int

    @Query("SELECT * FROM reminders WHERE userId = :userId AND isActive = 1 ORDER BY reminderDate ASC")
    fun observeReminders(userId: String): Flow<List<ReminderEntity>>
}
