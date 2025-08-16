// Path: app/src/main/java/in/syncboard/planmate/data/repository/CategoryRepositoryImpl.kt

package `in`.syncboard.planmate.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import `in`.syncboard.planmate.data.local.database.dao.CategoryDao
import `in`.syncboard.planmate.data.local.database.entities.CategoryEntity
import `in`.syncboard.planmate.domain.repository.CategoryRepository
import `in`.syncboard.planmate.domain.repository.CategoryStats
import `in`.syncboard.planmate.domain.entity.Category
import `in`.syncboard.planmate.domain.entity.TransactionType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override suspend fun createCategory(category: Category): Result<Category> {
        return try {
            val categoryEntity = category.toEntity()
            categoryDao.insertCategory(categoryEntity)
            Result.success(category)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCategoryById(categoryId: String): Result<Category?> {
        return try {
            val categoryEntity = categoryDao.getCategoryById(categoryId)
            Result.success(categoryEntity?.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCategoriesByUser(userId: String): Result<List<Category>> {
        return try {
            val categories = categoryDao.getCategoriesByUser(userId)
            Result.success(categories.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCategoriesByType(userId: String, type: TransactionType): Result<List<Category>> {
        return try {
            val categories = categoryDao.getCategoriesByType(userId, type.name)
            Result.success(categories.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDefaultCategories(type: TransactionType): Result<List<Category>> {
        return try {
            val categories = categoryDao.getDefaultCategories(type.name)
            Result.success(categories.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCategory(category: Category): Result<Category> {
        return try {
            val categoryEntity = category.toEntity()
            categoryDao.updateCategory(categoryEntity)
            Result.success(category)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCategory(categoryId: String): Result<Unit> {
        return try {
            categoryDao.deactivateCategory(categoryId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCategoryUsageStats(userId: String, categoryId: String): Result<CategoryStats> {
        return try {
            // This would require more complex queries, simplified for now
            Result.success(
                CategoryStats(
                    categoryId = categoryId,
                    totalTransactions = 0,
                    totalAmount = 0.0,
                    averageAmount = 0.0,
                    lastUsed = null
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeCategories(userId: String): Flow<List<Category>> {
        return categoryDao.observeCategories(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
}

// Extension functions
private fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        userId = userId,
        name = name,
        type = type.name,
        icon = icon,
        color = color,
        isDefault = isDefault,
        parentCategoryId = parentCategoryId,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

private fun CategoryEntity.toDomainModel(): Category {
    return Category(
        id = id,
        userId = userId,
        name = name,
        type = TransactionType.valueOf(type),
        icon = icon,
        color = color,
        isDefault = isDefault,
        parentCategoryId = parentCategoryId,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}