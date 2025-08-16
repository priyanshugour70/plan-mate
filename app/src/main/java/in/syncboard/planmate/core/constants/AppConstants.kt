package `in`.syncboard.planmate.core.constants

/**
 * App-wide Constants
 * Contains all constant values used throughout the application
 */
object AppConstants {

    // App Information
    const val APP_NAME = "PlanMate"
    const val APP_VERSION = "1.0.0"
    const val APP_TAGLINE = "Plan smart. Spend wise. Live better."

    // Database
    const val DATABASE_NAME = "planmate_database"
    const val DATABASE_VERSION = 1

    // SharedPreferences / DataStore
    const val PREFERENCES_NAME = "planmate_preferences"
    const val USER_PREFERENCES = "user_preferences"

    // API Configuration (for future use)
    const val BASE_URL = "https://api.planmate.syncboard.in/"
    const val API_VERSION = "v1"
    const val TIMEOUT_SECONDS = 30L

    // Authentication
    const val TOKEN_KEY = "auth_token"
    const val REFRESH_TOKEN_KEY = "refresh_token"
    const val USER_ID_KEY = "user_id"
    const val IS_LOGGED_IN_KEY = "is_logged_in"

    // Validation
    const val MIN_PASSWORD_LENGTH = 6
    const val MAX_PASSWORD_LENGTH = 128
    const val MIN_PHONE_LENGTH = 10
    const val MAX_AMOUNT_DIGITS = 10

    // File Handling
    const val MAX_RECEIPT_SIZE_MB = 10
    const val ALLOWED_IMAGE_TYPES = "image/jpeg,image/png,image/jpg"
    const val RECEIPTS_FOLDER = "Receipts"
    const val EXPORTS_FOLDER = "Exports"

    // Currency
    const val DEFAULT_CURRENCY = "INR"
    const val CURRENCY_SYMBOL = "₹"
    const val CURRENCY_DECIMAL_PLACES = 2

    // Date & Time Formats
    const val DATE_FORMAT_DISPLAY = "MMM dd, yyyy"
    const val DATE_FORMAT_API = "yyyy-MM-dd"
    const val TIME_FORMAT_DISPLAY = "HH:mm"
    const val DATETIME_FORMAT_DISPLAY = "MMM dd, yyyy HH:mm"
    const val DATETIME_FORMAT_API = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    // Pagination
    const val DEFAULT_PAGE_SIZE = 20
    const val MAX_PAGE_SIZE = 100

    // Categories (Default)
    val DEFAULT_EXPENSE_CATEGORIES = listOf(
        "Food & Dining",
        "Transportation",
        "Shopping",
        "Entertainment",
        "Health",
        "Bills & Utilities",
        "Education",
        "Travel",
        "Groceries",
        "Personal Care"
    )

    val DEFAULT_INCOME_CATEGORIES = listOf(
        "Salary",
        "Freelance",
        "Investment",
        "Business",
        "Rental",
        "Gift",
        "Other Income"
    )

    // Payment Methods
    val DEFAULT_PAYMENT_METHODS = listOf(
        "Cash",
        "Credit Card",
        "Debit Card",
        "UPI",
        "Digital Wallet",
        "Bank Transfer",
        "Cheque"
    )

    // Budget Defaults
    const val DEFAULT_MONTHLY_BUDGET = 50000.0
    const val BUDGET_WARNING_THRESHOLD = 80 // 80% of budget used
    const val BUDGET_DANGER_THRESHOLD = 95 // 95% of budget used

    // Notification
    const val NOTIFICATION_CHANNEL_ID = "planmate_notifications"
    const val NOTIFICATION_CHANNEL_NAME = "PlanMate Notifications"
    const val REMINDER_NOTIFICATION_ID = 1001
    const val BUDGET_NOTIFICATION_ID = 1002

    // Deep Links
    const val DEEP_LINK_SCHEME = "planmate"
    const val DEEP_LINK_HOST = "app"

    // Analytics Events (for future use)
    const val EVENT_LOGIN = "user_login"
    const val EVENT_REGISTER = "user_register"
    const val EVENT_ADD_EXPENSE = "add_expense"
    const val EVENT_SET_BUDGET = "set_budget"
    const val EVENT_VIEW_ANALYTICS = "view_analytics"

    // Error Codes
    const val ERROR_NETWORK = "network_error"
    const val ERROR_AUTHENTICATION = "auth_error"
    const val ERROR_VALIDATION = "validation_error"
    const val ERROR_UNKNOWN = "unknown_error"

    // Feature Flags (for gradual rollout)
    const val FEATURE_BIOMETRIC_AUTH = "biometric_auth"
    const val FEATURE_DARK_MODE = "dark_mode"
    const val FEATURE_EXPORT_DATA = "export_data"
    const val FEATURE_CLOUD_SYNC = "cloud_sync"

    // Cache Keys
    const val CACHE_USER_DATA = "user_data"
    const val CACHE_DASHBOARD_DATA = "dashboard_data"
    const val CACHE_CATEGORIES = "categories"
    const val CACHE_PAYMENT_METHODS = "payment_methods"

    // Timeouts and Delays
    const val SPLASH_DELAY_MS = 2000L
    const val DEBOUNCE_SEARCH_MS = 300L
    const val ANIMATION_DURATION_MS = 300L
    const val RETRY_DELAY_MS = 1000L

    // UI Constants
    const val MAX_DECIMAL_PLACES = 2
    const val GRID_COLUMNS = 2
    const val LIST_ITEM_HEIGHT_DP = 72
    const val CARD_ELEVATION_DP = 4

    // Security
    const val BIOMETRIC_MAX_ATTEMPTS = 3
    const val SESSION_TIMEOUT_MINUTES = 30
    const val AUTO_LOGOUT_MINUTES = 60
}