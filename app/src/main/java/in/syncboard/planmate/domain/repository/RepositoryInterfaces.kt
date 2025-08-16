package `in`.syncboard.planmate.domain.repository

import kotlinx.coroutines.flow.Flow
import `in`.syncboard.planmate.domain.entity.*

/**
 * Repository Interfaces for PlanMate
 * These define contracts for data access without exposing implementation details
 * Clean Architecture: Domain layer defines interfaces, Data layer implements them
 */

/**
 * User Repository Interface
 * Handles user-related data operations
 */
interface UserRepository {
    suspend fun createUser(user: User): Result<User>
    suspend fun getUserById(userId: String): Result<User?>
    suspend fun getUserByEmail(email: String): Result<User?>
    suspend fun updateUser(user: User): Result<User>
    suspend fun deleteUser(userId: String): Result<Unit>
    suspend fun isEmailExists(email: String): Result<Boolean>
    fun observeUser(userId: String): Flow<User?>
}

/**
 * Authentication Repository Interface
 * Handles authentication-related operations
 */
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, email: String, phone: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): Result<User?>
    suspend fun isUserLoggedIn(): Boolean
    suspend fun refreshToken(): Result<String>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit>
    suspend fun verifyEmail(token: String): Result<Unit>
    suspend fun resendVerificationEmail(): Result<Unit>
}

/**
 * Transaction Repository Interface
 * Handles transaction-related data operations
 */
interface TransactionRepository {
    suspend fun addTransaction(transaction: Transaction): Result<Transaction>
    suspend fun getTransactionById(transactionId: String): Result<Transaction?>
    suspend fun getTransactionsByUser(userId: String): Result<List<Transaction>>
    suspend fun getTransactionsByDateRange(userId: String, startDate: Long, endDate: Long): Result<List<Transaction>>
    suspend fun getTransactionsByCategory(userId: String, categoryId: String): Result<List<Transaction>>
    suspend fun getRecentTransactions(userId: String, limit: Int = 10): Result<List<Transaction>>
    suspend fun updateTransaction(transaction: Transaction): Result<Transaction>
    suspend fun deleteTransaction(transactionId: String): Result<Unit>
    suspend fun searchTransactions(userId: String, query: String): Result<List<Transaction>>
    fun observeTransactions(userId: String): Flow<List<Transaction>>
    fun observeTransactionsByCategory(userId: String, categoryId: String): Flow<List<Transaction>>

    // Analytics
    suspend fun getTotalExpensesByCategory(userId: String, startDate: Long, endDate: Long): Result<Map<String, Double>>
    suspend fun getMonthlyExpenseTrend(userId: String, months: Int = 12): Result<List<Pair<String, Double>>>
    suspend fun getDailyExpenseAverage(userId: String, days: Int = 30): Result<Double>
}

/**
 * Category Repository Interface
 * Handles category-related data operations
 */
interface CategoryRepository {
    suspend fun createCategory(category: Category): Result<Category>
    suspend fun getCategoryById(categoryId: String): Result<Category?>
    suspend fun getCategoriesByUser(userId: String): Result<List<Category>>
    suspend fun getCategoriesByType(userId: String, type: TransactionType): Result<List<Category>>
    suspend fun getDefaultCategories(type: TransactionType): Result<List<Category>>
    suspend fun updateCategory(category: Category): Result<Category>
    suspend fun deleteCategory(categoryId: String): Result<Unit>
    suspend fun getCategoryUsageStats(userId: String, categoryId: String): Result<CategoryStats>
    fun observeCategories(userId: String): Flow<List<Category>>
}

/**
 * Category Statistics Data Class
 */
data class CategoryStats(
    val categoryId: String,
    val totalTransactions: Int,
    val totalAmount: Double,
    val averageAmount: Double,
    val lastUsed: Long?
)

/**
 * Budget Repository Interface
 * Handles budget-related data operations
 */
interface BudgetRepository {
    suspend fun createBudget(budget: Budget): Result<Budget>
    suspend fun getBudgetById(budgetId: String): Result<Budget?>
    suspend fun getBudgetsByUser(userId: String): Result<List<Budget>>
    suspend fun getCurrentBudgets(userId: String): Result<List<Budget>>
    suspend fun getBudgetByCategory(userId: String, categoryId: String, period: BudgetPeriod): Result<Budget?>
    suspend fun updateBudget(budget: Budget): Result<Budget>
    suspend fun deleteBudget(budgetId: String): Result<Unit>
    suspend fun updateSpentAmount(budgetId: String, amount: Double): Result<Unit>
    fun observeBudgets(userId: String): Flow<List<Budget>>

    // Analytics
    suspend fun getBudgetUtilization(userId: String): Result<List<BudgetUtilization>>
    suspend fun getBudgetAlerts(userId: String): Result<List<BudgetAlert>>
}

/**
 * Budget Utilization Data Class
 */
data class BudgetUtilization(
    val budgetId: String,
    val categoryName: String,
    val allocatedAmount: Double,
    val spentAmount: Double,
    val utilizationPercentage: Double,
    val isOverBudget: Boolean
)

/**
 * Budget Alert Data Class
 */
data class BudgetAlert(
    val budgetId: String,
    val categoryName: String,
    val alertType: BudgetAlertType,
    val message: String,
    val threshold: Double,
    val currentAmount: Double
)

enum class BudgetAlertType {
    WARNING, // 80% threshold
    EXCEEDED, // Over 100%
    CRITICAL // Over 120%
}

/**
 * Goal Repository Interface
 * Handles goal-related data operations
 */
interface GoalRepository {
    suspend fun createGoal(goal: Goal): Result<Goal>
    suspend fun getGoalById(goalId: String): Result<Goal?>
    suspend fun getGoalsByUser(userId: String): Result<List<Goal>>
    suspend fun getActiveGoals(userId: String): Result<List<Goal>>
    suspend fun updateGoal(goal: Goal): Result<Goal>
    suspend fun updateGoalProgress(goalId: String, amount: Double): Result<Goal>
    suspend fun deleteGoal(goalId: String): Result<Unit>
    suspend fun getGoalProgress(goalId: String): Result<GoalProgress>
    fun observeGoals(userId: String): Flow<List<Goal>>
}

/**
 * Goal Progress Data Class
 */
data class GoalProgress(
    val goalId: String,
    val progressPercentage: Double,
    val remainingAmount: Double,
    val remainingDays: Int,
    val averageRequired: Double, // Per day/month to achieve goal
    val isOnTrack: Boolean
)

/**
 * Reminder Repository Interface
 * Handles reminder-related data operations
 */
interface ReminderRepository {
    suspend fun createReminder(reminder: Reminder): Result<Reminder>
    suspend fun getReminderById(reminderId: String): Result<Reminder?>
    suspend fun getRemindersByUser(userId: String): Result<List<Reminder>>
    suspend fun getUpcomingReminders(userId: String, days: Int = 7): Result<List<Reminder>>
    suspend fun getPendingReminders(userId: String): Result<List<Reminder>>
    suspend fun updateReminder(reminder: Reminder): Result<Reminder>
    suspend fun markReminderCompleted(reminderId: String): Result<Reminder>
    suspend fun deleteReminder(reminderId: String): Result<Unit>
    fun observeReminders(userId: String): Flow<List<Reminder>>
}

/**
 * Note Repository Interface
 * Handles note-related data operations
 */
interface NoteRepository {
    suspend fun createNote(note: Note): Result<Note>
    suspend fun getNoteById(noteId: String): Result<Note?>
    suspend fun getNotesByUser(userId: String): Result<List<Note>>
    suspend fun searchNotes(userId: String, query: String): Result<List<Note>>
    suspend fun updateNote(note: Note): Result<Note>
    suspend fun deleteNote(noteId: String): Result<Unit>
    suspend fun pinNote(noteId: String): Result<Note>
    suspend fun archiveNote(noteId: String): Result<Note>
    fun observeNotes(userId: String): Flow<List<Note>>
}

/**
 * Settings Repository Interface
 * Handles user settings and preferences
 */
interface SettingsRepository {
    suspend fun getSettings(userId: String): Result<Settings>
    suspend fun updateSettings(settings: Settings): Result<Settings>
    suspend fun resetSettings(userId: String): Result<Settings>
    fun observeSettings(userId: String): Flow<Settings>
}

/**
 * Notification Repository Interface
 * Handles notification-related operations
 */
interface NotificationRepository {
    suspend fun createNotification(notification: Notification): Result<Notification>
    suspend fun getNotificationsByUser(userId: String): Result<List<Notification>>
    suspend fun getUnreadNotifications(userId: String): Result<List<Notification>>
    suspend fun markNotificationAsRead(notificationId: String): Result<Unit>
    suspend fun markAllNotificationsAsRead(userId: String): Result<Unit>
    suspend fun deleteNotification(notificationId: String): Result<Unit>
    suspend fun deleteExpiredNotifications(): Result<Unit>
    fun observeNotifications(userId: String): Flow<List<Notification>>
}

/**
 * Export Repository Interface
 * Handles data export operations
 */
interface ExportRepository {
    suspend fun exportTransactions(userId: String, format: ExportFormat, startDate: Long, endDate: Long): Result<String>
    suspend fun exportBudgets(userId: String, format: ExportFormat): Result<String>
    suspend fun exportGoals(userId: String, format: ExportFormat): Result<String>
    suspend fun exportAllData(userId: String, format: ExportFormat): Result<String>
    suspend fun getExportHistory(userId: String): Result<List<ExportData>>
    suspend fun deleteExportFile(exportId: String): Result<Unit>
}

/**
 * Sync Repository Interface
 * Handles data synchronization with cloud (future feature)
 */
interface SyncRepository {
    suspend fun syncUserData(userId: String): Result<Unit>
    suspend fun uploadData(userId: String): Result<Unit>
    suspend fun downloadData(userId: String): Result<Unit>
    suspend fun getLastSyncTime(userId: String): Result<Long?>
    suspend fun markDataForSync(userId: String, dataType: String): Result<Unit>
    fun observeSyncStatus(): Flow<SyncStatus>
}

/**
 * Sync Status Data Class
 */
data class SyncStatus(
    val isInProgress: Boolean,
    val lastSyncTime: Long?,
    val pendingUploads: Int,
    val error: String?
)