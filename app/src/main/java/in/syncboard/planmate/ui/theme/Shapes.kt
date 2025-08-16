package `in`.syncboard.planmate.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * PlanMate Shape System
 * Defines consistent rounded corners and shapes throughout the app
 * Based on Material 3 shape tokens with custom adjustments for modern look
 */
val Shapes = Shapes(
    // Extra Small - for small buttons, chips, badges
    extraSmall = RoundedCornerShape(4.dp),

    // Small - for small cards, input fields
    small = RoundedCornerShape(8.dp),

    // Medium - for cards, dialogs, most UI elements
    medium = RoundedCornerShape(12.dp),

    // Large - for bottom sheets, large cards
    large = RoundedCornerShape(16.dp),

    // Extra Large - for modal dialogs, important containers
    extraLarge = RoundedCornerShape(28.dp)
)

/**
 * Custom Shape Extensions for PlanMate
 * Additional shapes for specific use cases in our financial app
 */

// For main dashboard cards with prominent rounding
val CardLargeShape = RoundedCornerShape(20.dp)

// For expense/income category cards
val CategoryCardShape = RoundedCornerShape(16.dp)

// For bottom navigation and floating action buttons
val NavigationShape = RoundedCornerShape(24.dp)

// For input fields and text boxes
val InputShape = RoundedCornerShape(12.dp)

// For buttons with modern rounded corners
val ButtonShape = RoundedCornerShape(16.dp)

// For profile pictures and avatars
val AvatarShape = RoundedCornerShape(50) // Full circle when width = height

// For charts and graph containers
val ChartContainerShape = RoundedCornerShape(18.dp)

// For notification cards and alerts
val NotificationShape = RoundedCornerShape(14.dp)

// For bottom sheets and modal dialogs
val BottomSheetShape = RoundedCornerShape(
    topStart = 24.dp,
    topEnd = 24.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

// For top app bars with subtle rounding
val TopBarShape = RoundedCornerShape(
    topStart = 0.dp,
    topEnd = 0.dp,
    bottomStart = 16.dp,
    bottomEnd = 16.dp
)