package `in`.syncboard.planmate.domain.entity

/**
 * Domain Entities for PlanMate
 * These represent the core business objects in our app
 * Clean Architecture: Domain layer should not depend on any external frameworks
 */

/**
 * User Entity - Represents a user in the system
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String?,
    val profilePictureUrl: String? = null,
    val currency: String = "INR",
    val timezone: String = "Asia/Kolkata",
    val isEmailVerified: Boolean = false,
    val isPhoneVerified: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Account Entity - Represents a financial account (bank account, wallet, etc.)
 */
data class Account(
    val id: String,
    val userId: String,
    val name: String,
    val type: AccountType,
    val balance: Double,
    val currency: String = "INR",
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Account Types
 */
enum class AccountType {
    BANK_ACCOUNT,
    CREDIT_CARD,
    DEBIT_CARD,
    DIGITAL_WALLET,
    CASH,
    INVESTMENT_ACCOUNT
}

/**
 * Transaction Entity - Represents a financial transaction
 */
data class Transaction(
    val id: String,
    val userId: String,
    val accountId: String,
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val title: String,
    val description: String? = null,
    val location: String? = null,
    val receiptUrl: String? = null,
    val tags: List<String> = emptyList(),
    val transactionDate: Long,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Transaction Types
 */
enum class TransactionType {
    INCOME,
    EXPENSE,
    TRANSFER
}

/**
 * Category Entity - Represents expense/income categories
 */
data class Category(
    val id: String,
    val userId: String,
    val name: String,
    val type: TransactionType,
    val icon: String,
    val color: String, // Hex color code
    val isDefault: Boolean = false,
    val parentCategoryId: String? = null, // For subcategories
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Budget Entity - Represents budget allocation for categories
 */
data class Budget(
    val id: String,
    val userId: String,
    val categoryId: String,
    val allocatedAmount: Double,
    val spentAmount: Double = 0.0,
    val period: BudgetPeriod,
    val startDate: Long,
    val endDate: Long,
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Budget Period Types
 */
enum class BudgetPeriod {
    WEEKLY,
    MONTHLY,
    QUARTERLY,
    YEARLY
}

/**
 * Goal Entity - Represents financial goals/targets
 */
data class Goal(
    val id: String,
    val userId: String,
    val title: String,
    val description: String? = null,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val targetDate: Long,
    val priority: GoalPriority,
    val status: GoalStatus,
    val categoryId: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Goal Priority Levels
 */
enum class GoalPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * Goal Status Types
 */
enum class GoalStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
    PAUSED,
    CANCELLED
}

/**
 * Reminder Entity - Represents reminders and tasks
 */
data class Reminder(
    val id: String,
    val userId: String,
    val title: String,
    val description: String? = null,
    val reminderDate: Long,
    val reminderTime: String, // HH:mm format
    val priority: ReminderPriority,
    val category: ReminderCategory,
    val isCompleted: Boolean = false,
    val isRecurring: Boolean = false,
    val recurringPattern: RecurringPattern? = null,
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Reminder Priority Levels
 */
enum class ReminderPriority {
    LOW,
    MEDIUM,
    HIGH
}

/**
 * Reminder Categories
 */
enum class ReminderCategory {
    BILLS,
    SHOPPING,
    HEALTH,
    WORK,
    PERSONAL,
    FINANCIAL,
    GENERAL
}

/**
 * Recurring Pattern for Reminders
 */
data class RecurringPattern(
    val type: RecurringType,
    val interval: Int = 1, // Every X days/weeks/months
    val endDate: Long? = null
)

/**
 * Recurring Types
 */
enum class RecurringType {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

/**
 * Note Entity - Represents user notes
 */
data class Note(
    val id: String,
    val userId: String,
    val title: String,
    val content: String,
    val color: String = "#FFFFFF", // Hex color code
    val tags: List<String> = emptyList(),
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Notification Entity - Represents app notifications
 */
data class Notification(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val data: Map<String, String> = emptyMap(), // Additional data for handling
    val isRead: Boolean = false,
    val isActionRequired: Boolean = false,
    val expiresAt: Long? = null,
    val createdAt: Long
)

/**
 * Notification Types
 */
enum class NotificationType {
    BUDGET_WARNING,
    BUDGET_EXCEEDED,
    GOAL_MILESTONE,
    REMINDER_DUE,
    PAYMENT_DUE,
    SYSTEM_UPDATE,
    PROMOTIONAL,
    SECURITY_ALERT
}

/**
 * Settings Entity - Represents user preferences and app settings
 */
data class Settings(
    val userId: String,
    val currency: String = "INR",
    val language: String = "en",
    val timezone: String = "Asia/Kolkata",
    val dateFormat: String = "dd/MM/yyyy",
    val timeFormat: String = "HH:mm",
    val enableNotifications: Boolean = true,
    val enableBudgetAlerts: Boolean = true,
    val enableGoalReminders: Boolean = true,
    val enableBiometricAuth: Boolean = false,
    val autoBackup: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val updatedAt: Long
)

/**
 * Theme Mode Options
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM // Follow system setting
}

/**
 * Export Data Entity - For data export functionality
 */
data class ExportData(
    val id: String,
    val userId: String,
    val format: ExportFormat,
    val dataType: ExportDataType,
    val filePath: String,
    val startDate: Long,
    val endDate: Long,
    val status: ExportStatus,
    val createdAt: Long,
    val completedAt: Long? = null
)

/**
 * Export Format Types
 */
enum class ExportFormat {
    CSV,
    PDF,
    EXCEL,
    JSON
}

/**
 * Export Data Types
 */
enum class ExportDataType {
    TRANSACTIONS,
    BUDGETS,
    GOALS,
    COMPLETE_DATA
}

/**
 * Export Status
 */
enum class ExportStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED
}