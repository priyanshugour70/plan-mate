// Path: app/src/main/java/in/syncboard/planmate/data/local/database/dao/BudgetDao.kt

package `in`.syncboard.planmate.data.local.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import `in`.syncboard.planmate.data.local.database.entities.BudgetEntity

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE userId = :userId AND isActive = 1")
    suspend fun getBudgetsByUser(userId: String): List<BudgetEntity>

    @Query("SELECT * FROM budgets WHERE id = :budgetId")
    suspend fun getBudgetById(budgetId: String): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE userId = :userId AND categoryId = :categoryId AND period = :period AND isActive = 1")
    suspend fun getBudgetByCategory(userId: String, categoryId: String, period: String): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE userId = :userId AND :currentDate BETWEEN startDate AND endDate AND isActive = 1")
    suspend fun getCurrentBudgets(userId: String, currentDate: Long): List<BudgetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity): Long

    @Update
    suspend fun updateBudget(budget: BudgetEntity): Int

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity): Int

    @Query("UPDATE budgets SET spentAmount = :amount WHERE id = :budgetId")
    suspend fun updateSpentAmount(budgetId: String, amount: Double): Int

    @Query("SELECT * FROM budgets WHERE userId = :userId AND isActive = 1")
    fun observeBudgets(userId: String): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE userId = :userId AND spentAmount >= allocatedAmount * 0.8 AND isActive = 1")
    suspend fun getBudgetsNearLimit(userId: String): List<BudgetEntity>
}