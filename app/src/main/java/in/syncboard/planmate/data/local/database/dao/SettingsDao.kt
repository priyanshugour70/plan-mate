// Path: app/src/main/java/in/syncboard/planmate/data/local/database/dao/SettingsDao.kt

package `in`.syncboard.planmate.data.local.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import `in`.syncboard.planmate.data.local.database.entities.SettingsEntity

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE userId = :userId")
    suspend fun getSettings(userId: String): SettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: SettingsEntity): Long

    @Update
    suspend fun updateSettings(settings: SettingsEntity): Int

    @Query("SELECT * FROM settings WHERE userId = :userId")
    fun observeSettings(userId: String): Flow<SettingsEntity?>
}