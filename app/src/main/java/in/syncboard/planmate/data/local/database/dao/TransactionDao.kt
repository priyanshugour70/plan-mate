// Path: app/src/main/java/in/syncboard/planmate/data/local/database/dao/TransactionDao.kt

package `in`.syncboard.planmate.data.local.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import `in`.syncboard.planmate.data.local.database.entities.TransactionEntity

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY transactionDate DESC")
    suspend fun getTransactionsByUser(userId: String): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: String): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE userId = :userId AND transactionDate BETWEEN :startDate AND :endDate ORDER BY transactionDate DESC")
    suspend fun getTransactionsByDateRange(userId: String, startDate: Long, endDate: Long): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND categoryId = :categoryId ORDER BY transactionDate DESC")
    suspend fun getTransactionsByCategory(userId: String, categoryId: String): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getRecentTransactions(userId: String, limit: Int): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') ORDER BY transactionDate DESC")
    suspend fun searchTransactions(userId: String, query: String): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND type = :type ORDER BY transactionDate DESC")
    suspend fun getTransactionsByType(userId: String, type: String): List<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity): Int

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity): Int

    @Query("DELETE FROM transactions WHERE id = :transactionId")
    suspend fun deleteTransactionById(transactionId: String): Int

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY transactionDate DESC")
    fun observeTransactions(userId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND categoryId = :categoryId ORDER BY transactionDate DESC")
    fun observeTransactionsByCategory(userId: String, categoryId: String): Flow<List<TransactionEntity>>

    // Analytics queries
    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND type = 'EXPENSE' AND transactionDate BETWEEN :startDate AND :endDate")
    suspend fun getTotalExpenses(userId: String, startDate: Long, endDate: Long): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND type = 'INCOME' AND transactionDate BETWEEN :startDate AND :endDate")
    suspend fun getTotalIncome(userId: String, startDate: Long, endDate: Long): Double?

    @Query("SELECT categoryId, SUM(amount) as total FROM transactions WHERE userId = :userId AND type = 'EXPENSE' AND transactionDate BETWEEN :startDate AND :endDate GROUP BY categoryId")
    suspend fun getExpensesByCategory(userId: String, startDate: Long, endDate: Long): List<CategoryExpense>

    @Query("SELECT AVG(amount) FROM transactions WHERE userId = :userId AND type = 'EXPENSE' AND transactionDate BETWEEN :startDate AND :endDate")
    suspend fun getAverageExpense(userId: String, startDate: Long, endDate: Long): Double?
}

data class CategoryExpense(
    val categoryId: String,
    val total: Double
)
