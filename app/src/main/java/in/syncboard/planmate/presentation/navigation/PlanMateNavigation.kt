// Path: app/src/main/java/in/syncboard/planmate/presentation/navigation/PlanMateNavigation.kt

package `in`.syncboard.planmate.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import `in`.syncboard.planmate.presentation.ui.screens.auth.LoginScreen
import `in`.syncboard.planmate.presentation.ui.screens.auth.RegisterScreen
import `in`.syncboard.planmate.presentation.ui.screens.auth.ForgotPasswordScreen
import `in`.syncboard.planmate.presentation.ui.screens.dashboard.DashboardScreen
import `in`.syncboard.planmate.presentation.ui.screens.budget.BudgetScreen
import `in`.syncboard.planmate.presentation.ui.screens.expense.ExpenseListScreen
import `in`.syncboard.planmate.presentation.ui.screens.expense.AddExpenseScreen
import `in`.syncboard.planmate.presentation.ui.screens.profile.ProfileScreen
import `in`.syncboard.planmate.presentation.ui.screens.reminder.ReminderScreen
import `in`.syncboard.planmate.presentation.ui.screens.splash.SplashScreen
import `in`.syncboard.planmate.presentation.viewmodel.AuthViewModel

/**
 * Navigation Routes
 */
object PlanMateDestinations {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val DASHBOARD = "dashboard"
    const val BUDGET = "budget"
    const val EXPENSES = "expenses"
    const val ADD_EXPENSE = "add_expense"
    const val PROFILE = "profile"
    const val REMINDERS = "reminders"
}

/**
 * Main Navigation Component with Authentication Flow
 */
@Composable
fun PlanMateNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState = authViewModel.uiState

    // Determine start destination based on auth state
    val startDestination = when {
        authState.isLoggedIn -> PlanMateDestinations.DASHBOARD
        else -> PlanMateDestinations.SPLASH
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash Screen
        composable(PlanMateDestinations.SPLASH) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(PlanMateDestinations.LOGIN) {
                        popUpTo(PlanMateDestinations.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(PlanMateDestinations.DASHBOARD) {
                        popUpTo(PlanMateDestinations.SPLASH) { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }

        // Authentication Screens
        composable(PlanMateDestinations.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(PlanMateDestinations.REGISTER)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(PlanMateDestinations.FORGOT_PASSWORD)
                },
                onLoginSuccess = {
                    navController.navigate(PlanMateDestinations.DASHBOARD) {
                        popUpTo(PlanMateDestinations.LOGIN) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable(PlanMateDestinations.REGISTER) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(PlanMateDestinations.DASHBOARD) {
                        popUpTo(PlanMateDestinations.LOGIN) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable(PlanMateDestinations.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = authViewModel
            )
        }

        // Main App Screens (Protected)
        composable(PlanMateDestinations.DASHBOARD) {
            // Check authentication
            LaunchedEffect(authState.isLoggedIn) {
                if (!authState.isLoggedIn) {
                    navController.navigate(PlanMateDestinations.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

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
                    authViewModel.logout()
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