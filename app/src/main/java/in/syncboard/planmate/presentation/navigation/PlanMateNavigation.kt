package `in`.syncboard.planmate.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import `in`.syncboard.planmate.presentation.ui.screens.auth.LoginScreen
import `in`.syncboard.planmate.presentation.ui.screens.auth.RegisterScreen
import `in`.syncboard.planmate.presentation.ui.screens.dashboard.DashboardScreen
import `in`.syncboard.planmate.presentation.ui.screens.budget.BudgetScreen
import `in`.syncboard.planmate.presentation.ui.screens.expense.ExpenseListScreen
import `in`.syncboard.planmate.presentation.ui.screens.expense.AddExpenseScreen
import `in`.syncboard.planmate.presentation.ui.screens.profile.ProfileScreen
import `in`.syncboard.planmate.presentation.ui.screens.reminder.ReminderScreen

/**
 * Navigation Routes
 * Define all screen destinations in the app
 */
object PlanMateDestinations {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val BUDGET = "budget"
    const val EXPENSES = "expenses"
    const val ADD_EXPENSE = "add_expense"
    const val PROFILE = "profile"
    const val REMINDERS = "reminders"
}

/**
 * Main Navigation Component
 * Handles navigation between all screens in the app
 */
@Composable
fun PlanMateNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = PlanMateDestinations.LOGIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Authentication Screens
        composable(PlanMateDestinations.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(PlanMateDestinations.REGISTER)
                },
                onLoginSuccess = {
                    // Clear back stack and navigate to dashboard
                    navController.navigate(PlanMateDestinations.DASHBOARD) {
                        popUpTo(PlanMateDestinations.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(PlanMateDestinations.REGISTER) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    // Navigate to dashboard after successful registration
                    navController.navigate(PlanMateDestinations.DASHBOARD) {
                        popUpTo(PlanMateDestinations.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Main App Screens
        composable(PlanMateDestinations.DASHBOARD) {
            DashboardScreen(
                onNavigateToBudget = {
                    navController.navigate(PlanMateDestinations.BUDGET)
                },
                onNavigateToExpenses = {
                    navController.navigate(PlanMateDestinations.EXPENSES)
                },
                onNavigateToAddExpense = {
                    navController.navigate(PlanMateDestinations.ADD_EXPENSE)
                },
                onNavigateToProfile = {
                    navController.navigate(PlanMateDestinations.PROFILE)
                },
                onNavigateToReminders = {
                    navController.navigate(PlanMateDestinations.REMINDERS)
                }
            )
        }

        composable(PlanMateDestinations.BUDGET) {
            BudgetScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(PlanMateDestinations.EXPENSES) {
            ExpenseListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAddExpense = {
                    navController.navigate(PlanMateDestinations.ADD_EXPENSE)
                }
            )
        }

        composable(PlanMateDestinations.ADD_EXPENSE) {
            AddExpenseScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onExpenseSaved = {
                    // Navigate back to expenses list after saving
                    navController.popBackStack()
                }
            )
        }

        composable(PlanMateDestinations.PROFILE) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    // Clear all back stack and navigate to login
                    navController.navigate(PlanMateDestinations.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(PlanMateDestinations.REMINDERS) {
            ReminderScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}