// Path: app/src/main/java/in/syncboard/planmate/core/di/DatabaseModule.kt

package `in`.syncboard.planmate.core.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import `in`.syncboard.planmate.data.local.database.PlanMateDatabase
import `in`.syncboard.planmate.data.local.database.dao.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePlanMateDatabase(@ApplicationContext context: Context): PlanMateDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            PlanMateDatabase::class.java,
            "planmate_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(database: PlanMateDatabase): UserDao = database.userDao()

    @Provides
    fun provideCategoryDao(database: PlanMateDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideTransactionDao(database: PlanMateDatabase): TransactionDao = database.transactionDao()

    @Provides
    fun provideBudgetDao(database: PlanMateDatabase): BudgetDao = database.budgetDao()

    @Provides
    fun provideReminderDao(database: PlanMateDatabase): ReminderDao = database.reminderDao()

    @Provides
    fun provideNoteDao(database: PlanMateDatabase): NoteDao = database.noteDao()

    @Provides
    fun provideSettingsDao(database: PlanMateDatabase): SettingsDao = database.settingsDao()
}
