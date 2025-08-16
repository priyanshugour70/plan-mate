// Path: app/src/main/java/in/syncboard/planmate/data/repository/TransactionRepositoryImpl.kt

package `in`.syncboard.planmate.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import `in`.syncboard.planmate.data.local.database.dao.TransactionDao
import `in`.syncboard.planmate.data.local.database.dao.BudgetDao
import `in`.syncboard.planmate.data.local.database.entities.TransactionEntity
import `in`.syncboard.planmate.domain.repository.TransactionRepository
import `in`.syncboard.planmate.domain.entity.Transaction
import `in`.syncboard.planmate.domain.entity.TransactionType
import `in`.syncboard.planmate.domain.entity.Category
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val budgetDao: BudgetDao
) : TransactionRepository {

    override suspend fun addTransaction(transaction: Transaction): Result<Transaction> {
        return try {
            val transactionEntity = transaction.toEntity()
            transactionDao.insertTransaction(transactionEntity)

            // Update budget spent amount if it's an expense
            if (transaction.type == TransactionType.EXPENSE) {
                updateBudgetSpentAmount(transaction.userId, transaction.category.id, transaction.amount)
            }

            Result.success(transaction)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTransactionById(transactionId: String): Result<Transaction?> {
        return try {
            val transactionEntity = transactionDao.getTransactionById(transactionId)
            Result.success(transactionEntity?.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTransactionsByUser(userId: String): Result<List<Transaction>> {
        return try {
            val transactions = transactionDao.getTransactionsByUser(userId)
            Result.success(transactions.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTransactionsByDateRange(userId: String, startDate: Long, endDate: Long): Result<List<Transaction>> {
        return try {
            val transactions = transactionDao.getTransactionsByDateRange(userId, startDate, endDate)
            Result.success(transactions.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTransactionsByCategory(userId: String, categoryId: String): Result<List<Transaction>> {
        return try {
            val transactions = transactionDao.getTransactionsByCategory(userId, categoryId)
            Result.success(transactions.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecentTransactions(userId: String, limit: Int): Result<List<Transaction>> {
        return try {
            val transactions = transactionDao.getRecentTransactions(userId, limit)
            Result.success(transactions.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTransaction(transaction: Transaction): Result<Transaction> {
        return try {
            val transactionEntity = transaction.toEntity()
            transactionDao.updateTransaction(transactionEntity)
            Result.success(transaction)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTransaction(transactionId: String): Result<Unit> {
        return try {
            transactionDao.deleteTransactionById(transactionId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchTransactions(userId: String, query: String): Result<List<Transaction>> {
        return try {
            val transactions = transactionDao.searchTransactions(userId, query)
            Result.success(transactions.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeTransactions(userId: String): Flow<List<Transaction>> {
        return transactionDao.observeTransactions(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun observeTransactionsByCategory(userId: String, categoryId: String): Flow<List<Transaction>> {
        return transactionDao.observeTransactionsByCategory(userId, categoryId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getTotalExpensesByCategory(userId: String, startDate: Long, endDate: Long): Result<Map<String, Double>> {
        return try {
            val categoryExpenses = transactionDao.getExpensesByCategory(userId, startDate, endDate)
            val result = categoryExpenses.associate { it.categoryId to it.total }
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMonthlyExpenseTrend(userId: String, months: Int): Result<List<Pair<String, Double>>> {
        return try {
            // This would require more complex query, simplified for now
            val endDate = System.currentTimeMillis()
            val startDate = endDate - (months * 30L * 24L * 60L * 60L * 1000L)
            val totalExpenses = transactionDao.getTotalExpenses(userId, startDate, endDate) ?: 0.0
            Result.success(listOf("Current Period" to totalExpenses))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDailyExpenseAverage(userId: String, days: Int): Result<Double> {
        return try {
            val endDate = System.currentTimeMillis()
            val startDate = endDate - (days * 24L * 60L * 60L * 1000L)
            val average = transactionDao.getAverageExpense(userId, startDate, endDate) ?: 0.0
            Result.success(average)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun updateBudgetSpentAmount(userId: String, categoryId: String, amount: Double) {
        try {
            val currentTime = System.currentTimeMillis()
            val budgets = budgetDao.getCurrentBudgets(userId, currentTime)
            val budget = budgets.find { it.categoryId == categoryId }
            budget?.let {
                val newSpentAmount = it.spentAmount + amount
                budgetDao.updateSpentAmount(it.id, newSpentAmount)
            }
        } catch (e: Exception) {
            // Log error but don't fail the transaction
        }
    }
}

// Extension functions
private fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        userId = userId,
        categoryId = category.id,
        amount = amount,
        type = type.name,
        title = title,
        description = description,
        location = location,
        receiptUrl = receiptUrl,
        paymentMethod = "", // This would come from a payment method field
        tags = tags.joinToString(","),
        transactionDate = transactionDate,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

private fun TransactionEntity.toDomainModel(): Transaction {
    return Transaction(
        id = id,
        userId = userId,
        accountId = "", // Would be implemented later
        amount = amount,
        type = TransactionType.valueOf(type),
        category = Category(
            id = categoryId,
            userId = userId,
            name = "", // Would need to join with category table
            type = TransactionType.valueOf(type),
            icon = "",
            color = "",
            isDefault = false,
            parentCategoryId = null,
            isActive = true,
            createdAt = createdAt,
            updatedAt = updatedAt
        ),
        title = title,
        description = description,
        location = location,
        receiptUrl = receiptUrl,
        tags = if (tags.isNotEmpty()) tags.split(",") else emptyList(),
        transactionDate = transactionDate,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
