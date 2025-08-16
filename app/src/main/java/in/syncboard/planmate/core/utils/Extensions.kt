package `in`.syncboard.planmate.core.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Extension Functions for PlanMate
 * Provides utility functions to make common operations easier
 */

// ========== String Extensions ==========

/**
 * Check if string is a valid email
 */
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Check if string is a valid phone number
 */
fun String.isValidPhone(): Boolean {
    return this.length >= 10 && this.all { it.isDigit() }
}

/**
 * Capitalize first letter of each word
 */
fun String.toTitleCase(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

/**
 * Remove all non-digit characters
 */
fun String.digitsOnly(): String {
    return this.filter { it.isDigit() }
}

/**
 * Check if string contains only letters and spaces
 */
fun String.isValidName(): Boolean {
    return this.isNotBlank() && this.all { it.isLetter() || it.isWhitespace() }
}

// ========== Number Extensions ==========

/**
 * Format number as currency (Indian Rupees)
 */
fun Double.toCurrency(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    return formatter.format(this).replace("₹", "₹ ")
}

/**
 * Format number as currency without symbol
 */
fun Double.toCurrencyWithoutSymbol(): String {
    val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
    formatter.maximumFractionDigits = 2
    formatter.minimumFractionDigits = 2
    return formatter.format(this)
}

/**
 * Format large numbers with K, M, B suffixes
 */
fun Double.toShortCurrency(): String {
    return when {
        this >= 10_000_000 -> "₹${(this / 10_000_000).format(1)}Cr" // Crores
        this >= 100_000 -> "₹${(this / 100_000).format(1)}L" // Lakhs
        this >= 1_000 -> "₹${(this / 1_000).format(1)}K" // Thousands
        else -> "₹${this.format(0)}"
    }
}

/**
 * Format double to specified decimal places
 */
fun Double.format(digits: Int): String {
    return "%.${digits}f".format(this)
}

/**
 * Convert to percentage string
 */
fun Double.toPercentage(): String {
    return "${(this * 100).format(1)}%"
}

// ========== Date Extensions ==========

/**
 * Format timestamp to display date
 */
fun Long.toDisplayDate(): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(Date(this))
}

/**
 * Format timestamp to display time
 */
fun Long.toDisplayTime(): String {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(Date(this))
}

/**
 * Format timestamp to display date and time
 */
fun Long.toDisplayDateTime(): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date(this))
}

/**
 * Get relative time string (e.g., "2 hours ago")
 */
fun Long.toRelativeTime(): String {
    val now = System.currentTimeMillis()
    val diff = now - this

    return when {
        diff < 60_000 -> "Just now" // Less than 1 minute
        diff < 3_600_000 -> "${diff / 60_000}m ago" // Less than 1 hour
        diff < 86_400_000 -> "${diff / 3_600_000}h ago" // Less than 1 day
        diff < 2_592_000_000 -> "${diff / 86_400_000}d ago" // Less than 30 days
        else -> this.toDisplayDate() // More than 30 days, show full date
    }
}

/**
 * Check if timestamp is today
 */
fun Long.isToday(): Boolean {
    val today = Calendar.getInstance()
    val date = Calendar.getInstance().apply { timeInMillis = this@isToday }

    return today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
}

/**
 * Check if timestamp is this week
 */
fun Long.isThisWeek(): Boolean {
    val today = Calendar.getInstance()
    val date = Calendar.getInstance().apply { timeInMillis = this@isThisWeek }

    return today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
            today.get(Calendar.WEEK_OF_YEAR) == date.get(Calendar.WEEK_OF_YEAR)
}

// ========== Context Extensions ==========

/**
 * Show toast message
 */
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

/**
 * Show long toast message
 */
fun Context.showLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

// ========== Compose Extensions ==========

/**
 * Convert Dp to Px in Compose
 */
@Composable
fun Dp.toPx(): Float {
    return with(LocalDensity.current) { this@toPx.toPx() }
}

/**
 * Convert Px to Dp in Compose
 */
@Composable
fun Float.toDp(): Dp {
    return with(LocalDensity.current) { this@toDp.toDp() }
}

// ========== List Extensions ==========

/**
 * Safe get item from list
 */
fun <T> List<T>.safeGet(index: Int): T? {
    return if (index >= 0 && index < this.size) this[index] else null
}

/**
 * Group expenses by date
 */
fun <T> List<T>.groupByDate(dateSelector: (T) -> Long): Map<String, List<T>> {
    return this.groupBy { item ->
        val timestamp = dateSelector(item)
        when {
            timestamp.isToday() -> "Today"
            timestamp.isThisWeek() -> timestamp.toDisplayDate()
            else -> timestamp.toDisplayDate()
        }
    }
}

// ========== Validation Helpers ==========

/**
 * Validate amount input
 */
fun String.isValidAmount(): Boolean {
    return try {
        val amount = this.toDoubleOrNull()
        amount != null && amount > 0 && amount <= 999_999_999.99
    } catch (e: Exception) {
        false
    }
}

/**
 * Clean and format amount input
 */
fun String.cleanAmount(): String {
    return this.replace(",", "")
        .replace(" ", "")
        .replace("₹", "")
        .trim()
}

// ========== Color Helpers ==========

/**
 * Get contrasting text color for background
 */
fun androidx.compose.ui.graphics.Color.getContrastingTextColor(): androidx.compose.ui.graphics.Color {
    val luminance = (0.299 * this.red + 0.587 * this.green + 0.114 * this.blue)
    return if (luminance > 0.5) {
        androidx.compose.ui.graphics.Color.Black
    } else {
        androidx.compose.ui.graphics.Color.White
    }
}

// ========== Financial Calculations ==========

/**
 * Calculate percentage of budget used
 */
fun Double.percentageOf(total: Double): Double {
    return if (total > 0) (this / total) * 100 else 0.0
}

/**
 * Calculate savings rate
 */
fun calculateSavingsRate(income: Double, expenses: Double): Double {
    return if (income > 0) ((income - expenses) / income) * 100 else 0.0
}

/**
 * Format as Indian number system (with commas)
 */
fun Double.toIndianNumberFormat(): String {
    val formatter = NumberFormat.getInstance(Locale("en", "IN"))
    return formatter.format(this)
}