// Path: app/src/main/java/in/syncboard/planmate/core/utils/Constants.kt

package `in`.syncboard.planmate.core.utils

object Constants {
    // Database
    const val DATABASE_NAME = "planmate_database"
    const val DATABASE_VERSION = 1

    // Shared Preferences
    const val PREFS_NAME = "planmate_prefs"

    // Animation Durations
    const val SPLASH_DELAY = 2000L
    const val ANIMATION_DURATION = 300L

    // Date Formats
    const val DATE_FORMAT = "MMM dd, yyyy"
    const val TIME_FORMAT = "HH:mm"
    const val DATETIME_FORMAT = "MMM dd, yyyy HH:mm"

    // Currency
    const val CURRENCY_SYMBOL = "₹"
    const val CURRENCY_CODE = "INR"

    // Validation
    const val MIN_PASSWORD_LENGTH = 6
    const val MIN_PHONE_LENGTH = 10
    const val MAX_AMOUNT = 999999999.99

    // Payment Methods
    val PAYMENT_METHODS = listOf("Cash", "Card", "UPI", "Wallet", "Bank Transfer")

    // Budget Thresholds
    const val BUDGET_WARNING_THRESHOLD = 80 // 80%
    const val BUDGET_DANGER_THRESHOLD = 95 // 95%
}