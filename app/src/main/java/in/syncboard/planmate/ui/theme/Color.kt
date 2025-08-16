package `in`.syncboard.planmate.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Colors - Deep Violet/Purple
val Primary50 = Color(0xFFF3E5F5)
val Primary100 = Color(0xFFE1BEE7)
val Primary200 = Color(0xFFCE93D8)
val Primary300 = Color(0xFFBA68C8)
val Primary400 = Color(0xFFAB47BC)
val Primary500 = Color(0xFF9C27B0) // Main primary (Deep Purple)
val Primary600 = Color(0xFF8E24AA)
val Primary700 = Color(0xFF7B1FA2)
val Primary800 = Color(0xFF6A1B9A)
val Primary900 = Color(0xFF4A148C)

// Secondary Colors - Ocean Blue
val Secondary50 = Color(0xFFE8F4FD)
val Secondary100 = Color(0xFFC3E2FB)
val Secondary200 = Color(0xFF9ACEF8)
val Secondary300 = Color(0xFF6BB6F5)
val Secondary400 = Color(0xFF42A5F5)
val Secondary500 = Color(0xFF2196F3) // Main secondary (Blue)
val Secondary600 = Color(0xFF1E88E5)
val Secondary700 = Color(0xFF1976D2)
val Secondary800 = Color(0xFF1565C0)
val Secondary900 = Color(0xFF0D47A1)

// Tertiary Colors - Teal Accent
val Tertiary50 = Color(0xFFE0F2F1)
val Tertiary100 = Color(0xFFB2DFDB)
val Tertiary200 = Color(0xFF80CBC4)
val Tertiary300 = Color(0xFF4DB6AC)
val Tertiary400 = Color(0xFF26A69A)
val Tertiary500 = Color(0xFF009688) // Main tertiary (Teal)
val Tertiary600 = Color(0xFF00897B)
val Tertiary700 = Color(0xFF00796B)
val Tertiary800 = Color(0xFF00695C)
val Tertiary900 = Color(0xFF004D40)

// Success Colors - Emerald Green
val Success50 = Color(0xFFECFDF5)
val Success100 = Color(0xFFD1FAE5)
val Success200 = Color(0xFFA7F3D0)
val Success300 = Color(0xFF6EE7B7)
val Success400 = Color(0xFF34D399)
val Success500 = Color(0xFF10B981) // Main success
val Success600 = Color(0xFF059669)
val Success700 = Color(0xFF047857)
val Success800 = Color(0xFF065F46)
val Success900 = Color(0xFF064E3B)

// Error Colors - Soft Red
val Error50 = Color(0xFFFEF2F2)
val Error100 = Color(0xFFFEE2E2)
val Error200 = Color(0xFFFECACA)
val Error300 = Color(0xFFFCA5A5)
val Error400 = Color(0xFFF87171)
val Error500 = Color(0xFFEF4444) // Main error
val Error600 = Color(0xFFDC2626)
val Error700 = Color(0xFFB91C1C)
val Error800 = Color(0xFF991B1B)
val Error900 = Color(0xFF7F1D1D)

// Warning Colors - Amber
val Warning50 = Color(0xFFFFFBEB)
val Warning100 = Color(0xFFFEF3C7)
val Warning200 = Color(0xFFFDE68A)
val Warning300 = Color(0xFFFCD34D)
val Warning400 = Color(0xFFFBBF24)
val Warning500 = Color(0xFFF59E0B) // Main warning
val Warning600 = Color(0xFFD97706)
val Warning700 = Color(0xFFB45309)
val Warning800 = Color(0xFF92400E)
val Warning900 = Color(0xFF78350F)

// Neutral Colors - Modern Grayscale
val Neutral0 = Color(0xFFFFFFFF) // Pure white
val Neutral10 = Color(0xFFFCFCFD)
val Neutral20 = Color(0xFFF9FAFB)
val Neutral30 = Color(0xFFF3F4F6)
val Neutral40 = Color(0xFFE5E7EB)
val Neutral50 = Color(0xFFD1D5DB)
val Neutral60 = Color(0xFF9CA3AF)
val Neutral70 = Color(0xFF6B7280)
val Neutral80 = Color(0xFF374151)
val Neutral90 = Color(0xFF1F2937)
val Neutral95 = Color(0xFF111827)
val Neutral100 = Color(0xFF000000) // Pure black

// Surface Colors - Enhanced with subtle tints
val SurfaceBright = Color(0xFFFFFFFF)
val SurfaceDim = Color(0xFFF8F9FF)
val SurfaceContainer = Color(0xFFF5F7FF)
val SurfaceContainerHigh = Color(0xFFF0F3FF)
val SurfaceContainerHighest = Color(0xFFEBEFFF)

// Gradient Colors - Violet to Blue
val GradientStart = Primary500
val GradientEnd = Secondary500
val GradientAccent = Tertiary500

// Income/Expense Colors
val IncomeGreen = Success500 // Emerald Green
val ExpenseRed = Error500   // Soft Red
val SavingsBlue = Secondary600 // Ocean Blue

// Enhanced Category Colors - Purple/Blue theme
val FoodColor = Color(0xFF8B5CF6) // Violet
val TransportColor = Color(0xFF06B6D4) // Cyan
val ShoppingColor = Color(0xFF3B82F6) // Blue
val EntertainmentColor = Color(0xFF8B5CF6) // Purple
val HealthColor = Color(0xFF10B981) // Emerald
val BillsColor = Color(0xFF6366F1) // Indigo
val EducationColor = Color(0xFF8B5CF6) // Violet
val TravelColor = Color(0xFF0EA5E9) // Sky Blue
val PersonalColor = Color(0xFFA855F7) // Purple
val InvestmentColor = Color(0xFF3B82F6) // Blue

// Special Effect Colors
val GlowPurple = Color(0xFF9C27B0).copy(alpha = 0.3f)
val GlowBlue = Color(0xFF2196F3).copy(alpha = 0.3f)
val GlowTeal = Color(0xFF009688).copy(alpha = 0.3f)

// Background Gradients
val BackgroundGradientLight = listOf(
    Color(0xFFFAFAFF),
    Color(0xFFF5F7FF),
    Color(0xFFF0F3FF)
)

val BackgroundGradientDark = listOf(
    Color(0xFF1A1B2E),
    Color(0xFF16213E),
    Color(0xFF0F3460)
)