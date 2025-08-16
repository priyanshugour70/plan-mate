// Path: app/src/main/java/in/syncboard/planmate/data/repository/SettingsRepositoryImpl.kt

package `in`.syncboard.planmate.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import `in`.syncboard.planmate.data.local.database.dao.SettingsDao
import `in`.syncboard.planmate.data.local.database.entities.SettingsEntity
import `in`.syncboard.planmate.domain.repository.SettingsRepository
import `in`.syncboard.planmate.domain.entity.Settings
import `in`.syncboard.planmate.domain.entity.ThemeMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDao: SettingsDao
) : SettingsRepository {

    override suspend fun getSettings(userId: String): Result<Settings> {
        return try {
            val settingsEntity = settingsDao.getSettings(userId)
            if (settingsEntity != null) {
                Result.success(settingsEntity.toDomainModel())
            } else {
                // Create default settings if not exists
                val defaultSettings = createDefaultSettings(userId)
                settingsDao.insertSettings(defaultSettings.toEntity())
                Result.success(defaultSettings)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateSettings(settings: Settings): Result<Settings> {
        return try {
            val settingsEntity = settings.toEntity()
            settingsDao.updateSettings(settingsEntity)
            Result.success(settings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetSettings(userId: String): Result<Settings> {
        return try {
            val defaultSettings = createDefaultSettings(userId)
            val settingsEntity = defaultSettings.toEntity()
            settingsDao.insertSettings(settingsEntity)
            Result.success(defaultSettings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeSettings(userId: String): Flow<Settings> {
        return settingsDao.observeSettings(userId).map { entity ->
            entity?.toDomainModel() ?: createDefaultSettings(userId)
        }
    }

    private fun createDefaultSettings(userId: String): Settings {
        return Settings(
            userId = userId,
            currency = "INR",
            language = "en",
            timezone = "Asia/Kolkata",
            dateFormat = "dd/MM/yyyy",
            timeFormat = "HH:mm",
            enableNotifications = true,
            enableBudgetAlerts = true,
            enableGoalReminders = true,
            enableBiometricAuth = false,
            autoBackup = true,
            themeMode = ThemeMode.SYSTEM,
            updatedAt = System.currentTimeMillis()
        )
    }
}

// Extension functions
private fun Settings.toEntity(): SettingsEntity {
    return SettingsEntity(
        userId = userId,
        currency = currency,
        language = language,
        timezone = timezone,
        dateFormat = dateFormat,
        timeFormat = timeFormat,
        enableNotifications = enableNotifications,
        enableBudgetAlerts = enableBudgetAlerts,
        enableGoalReminders = enableGoalReminders,
        enableBiometricAuth = enableBiometricAuth,
        autoBackup = autoBackup,
        themeMode = themeMode.name,
        updatedAt = updatedAt
    )
}

private fun SettingsEntity.toDomainModel(): Settings {
    return Settings(
        userId = userId,
        currency = currency,
        language = language,
        timezone = timezone,
        dateFormat = dateFormat,
        timeFormat = timeFormat,
        enableNotifications = enableNotifications,
        enableBudgetAlerts = enableBudgetAlerts,
        enableGoalReminders = enableGoalReminders,
        enableBiometricAuth = enableBiometricAuth,
        autoBackup = autoBackup,
        themeMode = ThemeMode.valueOf(themeMode),
        updatedAt = updatedAt
    )
}