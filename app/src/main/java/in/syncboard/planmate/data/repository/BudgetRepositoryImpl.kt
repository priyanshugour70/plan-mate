// Path: app/src/main/java/in/syncboard/planmate/data/repository/BudgetRepositoryImpl.kt

package `in`.syncboard.planmate.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import `in`.syncboard.planmate.data.local.database.dao.BudgetDao
import `in`.syncboard.planmate.data.local.database.entities.BudgetEntity
import `in`.syncboard.planmate.domain.repository.BudgetRepository
import `in`.syncboard.planmate.domain.repository.BudgetUtilization
import `in`.syncboard.planmate.domain.repository.BudgetAlert
import `in`.syncboard.planmate.domain.entity.Budget
import `in`.syncboard.planmate.domain.entity.BudgetPeriod
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao
) : BudgetRepository {

    override suspend fun createBudget(budget: Budget): Result<Budget> {
        return try {
            val budgetEntity = budget.toEntity()
            budgetDao.insertBudget(budgetEntity)
            Result.success(budget)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBudgetById(budgetId: String): Result<Budget?> {
        return try {
            val budgetEntity = budgetDao.getBudgetById(budgetId)
            Result.success(budgetEntity?.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBudgetsByUser(userId: String): Result<List<Budget>> {
        return try {
            val budgets = budgetDao.getBudgetsByUser(userId)
            Result.success(budgets.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentBudgets(userId: String): Result<List<Budget>> {
        return try {
            val currentTime = System.currentTimeMillis()
            val budgets = budgetDao.getCurrentBudgets(userId, currentTime)
            Result.success(budgets.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBudgetByCategory(userId: String, categoryId: String, period: BudgetPeriod): Result<Budget?> {
        return try {
            val budgetEntity = budgetDao.getBudgetByCategory(userId, categoryId, period.name)
            Result.success(budgetEntity?.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateBudget(budget: Budget): Result<Budget> {
        return try {
            val budgetEntity = budget.toEntity()
            budgetDao.updateBudget(budgetEntity)
            Result.success(budget)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteBudget(budgetId: String): Result<Unit> {
        return try {
            val budgetEntity = budgetDao.getBudgetById(budgetId)
            if (budgetEntity != null) {
                budgetDao.deleteBudget(budgetEntity)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Budget not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateSpentAmount(budgetId: String, amount: Double): Result<Unit> {
        return try {
            budgetDao.updateSpentAmount(budgetId, amount)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeBudgets(userId: String): Flow<List<Budget>> {
        return budgetDao.observeBudgets(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getBudgetUtilization(userId: String): Result<List<BudgetUtilization>> {
        return try {
            val budgets = budgetDao.getBudgetsByUser(userId)
            val utilizations = budgets.map { budget ->
                BudgetUtilization(
                    budgetId = budget.id,
                    categoryName = "", // Would need to join with category
                    allocatedAmount = budget.allocatedAmount,
                    spentAmount = budget.spentAmount,
                    utilizationPercentage = (budget.spentAmount / budget.allocatedAmount) * 100,
                    isOverBudget = budget.spentAmount > budget.allocatedAmount
                )
            }
            Result.success(utilizations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBudgetAlerts(userId: String): Result<List<BudgetAlert>> {
        return try {
            val budgets = budgetDao.getBudgetsNearLimit(userId)
            val alerts = budgets.map { budget ->
                val percentage = (budget.spentAmount / budget.allocatedAmount) * 100
                BudgetAlert(
                    budgetId = budget.id,
                    categoryName = "", // Would need to join with category
                    alertType = when {
                        percentage >= 100 -> `in`.syncboard.planmate.domain.repository.BudgetAlertType.EXCEEDED
                        percentage >= 80 -> `in`.syncboard.planmate.domain.repository.BudgetAlertType.WARNING
                        else -> `in`.syncboard.planmate.domain.repository.BudgetAlertType.WARNING
                    },
                    message = "Budget alert for category",
                    threshold = budget.allocatedAmount * 0.8,
                    currentAmount = budget.spentAmount
                )
            }
            Result.success(alerts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Extension functions
private fun Budget.toEntity(): BudgetEntity {
    return BudgetEntity(
        id = id,
        userId = userId,
        categoryId = categoryId,
        allocatedAmount = allocatedAmount,
        spentAmount = spentAmount,
        period = period.name,
        startDate = startDate,
        endDate = endDate,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

private fun BudgetEntity.toDomainModel(): Budget {
    return Budget(
        id = id,
        userId = userId,
        categoryId = categoryId,
        allocatedAmount = allocatedAmount,
        spentAmount = spentAmount,
        period = BudgetPeriod.valueOf(period),
        startDate = startDate,
        endDate = endDate,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}