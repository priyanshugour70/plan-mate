// Path: app/src/main/java/in/syncboard/planmate/data/local/database/dao/CategoryDao.kt

package `in`.syncboard.planmate.data.local.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import `in`.syncboard.planmate.data.local.database.entities.CategoryEntity

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE userId = :userId AND isActive = 1")
    suspend fun getCategoriesByUser(userId: String): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE userId = :userId AND type = :type AND isActive = 1")
    suspend fun getCategoriesByType(userId: String, type: String): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: String): CategoryEntity?

    @Query("SELECT * FROM categories WHERE isDefault = 1 AND type = :type")
    suspend fun getDefaultCategories(type: String): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>): List<Long>

    @Update
    suspend fun updateCategory(category: CategoryEntity): Int

    @Delete
    suspend fun deleteCategory(category: CategoryEntity): Int

    @Query("UPDATE categories SET isActive = 0 WHERE id = :categoryId")
    suspend fun deactivateCategory(categoryId: String): Int

    @Query("SELECT * FROM categories WHERE userId = :userId AND isActive = 1")
    fun observeCategories(userId: String): Flow<List<CategoryEntity>>
}