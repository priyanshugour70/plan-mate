// Path: app/src/main/java/in/syncboard/planmate/data/local/database/DatabaseCallback.kt

package `in`.syncboard.planmate.data.local.database

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import `in`.syncboard.planmate.data.local.database.entities.CategoryEntity
import java.util.UUID

class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // Pre-populate database with default categories
        CoroutineScope(Dispatchers.IO).launch {
            val database = PlanMateDatabase.getDatabase(context)
            populateDatabase(database.categoryDao())
        }
    }

    private suspend fun populateDatabase(categoryDao: `in`.syncboard.planmate.data.local.database.dao.CategoryDao) {
        // Default expense categories
        val defaultExpenseCategories = listOf(
            CategoryEntity(
                id = UUID.randomUUID().toString(),
                userId = "", // Empty for default categories
                name = "Food & Dining",
                type = "EXPENSE",
                icon = "🍕",
                color = "#FF9800",
                isDefault = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            CategoryEntity(
                id = UUID.randomUUID().toString(),
                userId = "",
                name = "Transportation",
                type = "EXPENSE",
                icon = "🚗",
                color = "#2196F3",
                isDefault = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            CategoryEntity(
                id = UUID.randomUUID().toString(),
                userId = "",
                name = "Shopping",
                type = "EXPENSE",
                icon = "🛍️",
                color = "#E91E63",
                isDefault = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            CategoryEntity(
                id = UUID.randomUUID().toString(),
                userId = "",
                name = "Entertainment",
                type = "EXPENSE",
                icon = "🎮",
                color = "#9C27B0",
                isDefault = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            CategoryEntity(
                id = UUID.randomUUID().toString(),
                userId = "",
                name = "Health",
                type = "EXPENSE",
                icon = "🏥",
                color = "#4CAF50",
                isDefault = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            CategoryEntity(
                id = UUID.randomUUID().toString(),
                userId = "",
                name = "Bills & Utilities",
                type = "EXPENSE",
                icon = "⚡",
                color = "#FF5722",
                isDefault = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            CategoryEntity(
                id = UUID.randomUUID().toString(),
                userId = "",
                name = "Education",
                type = "EXPENSE",
                icon = "📚",
                color = "#607D8B",
                isDefault = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            CategoryEntity(
                id = UUID.randomUUID().toString(),
                userId = "",
                name = "Travel",
                type = "EXPENSE",
                icon = "✈️",
                color = "#795548",
                isDefault = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        )

        // Default income categories
        val defaultIncomeCategories = listOf(
            CategoryEntity(
                id = UUID.randomUUID().toString(),
                userId = "",
                name = "Salary",
                type = "INCOME",
                icon = "💰",
                color = "#4CAF50",
                isDefault = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            CategoryEntity(
                id = UUID.randomUUID().toString(),
                userId = "",
                name = "Freelance",
                type = "INCOME",
                icon = "💻",
                color = "#2196F3",
                isDefault = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            CategoryEntity(
                id = UUID.randomUUID().toString(),
                userId = "",
                name = "Investment",
                type = "INCOME",
                icon = "📈",
                color = "#FF9800",
                isDefault = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            CategoryEntity(
                id = UUID.randomUUID().toString(),
                userId = "",
                name = "Business",
                type = "INCOME",
                icon = "🏢",
                color = "#9C27B0",
                isDefault = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            CategoryEntity(
                id = UUID.randomUUID().toString(),
                userId = "",
                name = "Other Income",
                type = "INCOME",
                icon = "💵",
                color = "#607D8B",
                isDefault = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        )

        // Insert default categories
        categoryDao.insertCategories(defaultExpenseCategories + defaultIncomeCategories)
    }
}