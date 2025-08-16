// Path: app/src/main/java/in/syncboard/planmate/data/local/database/PlanMateDatabase.kt

package `in`.syncboard.planmate.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import `in`.syncboard.planmate.data.local.database.converters.Converters
import `in`.syncboard.planmate.data.local.database.dao.*
import `in`.syncboard.planmate.data.local.database.entities.*

@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        BudgetEntity::class,
        ReminderEntity::class,
        NoteEntity::class,
        SettingsEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class PlanMateDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun reminderDao(): ReminderDao
    abstract fun noteDao(): NoteDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: PlanMateDatabase? = null

        fun getDatabase(context: Context): PlanMateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlanMateDatabase::class.java,
                    "planmate_database"
                )
                    .addCallback(DatabaseCallback(context))
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}