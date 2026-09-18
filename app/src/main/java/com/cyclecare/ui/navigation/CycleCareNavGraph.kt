package com.cyclecare.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cyclecare.ui.calendar.CalendarScreen
import com.cyclecare.ui.calendar.CalendarViewModel
import com.cyclecare.ui.home.HomeScreen
import com.cyclecare.ui.home.HomeViewModel
import com.cyclecare.ui.insights.InsightsScreen
import com.cyclecare.ui.insights.InsightsViewModel
import com.cyclecare.ui.onboarding.OnboardingScreen
import com.cyclecare.ui.onboarding.OnboardingViewModel
import com.cyclecare.ui.profile.ProfileScreen
import com.cyclecare.ui.quicklog.QuickLogScreen
import com.cyclecare.ui.quicklog.QuickLogViewModel
import com.cyclecare.ui.symptoms.SymptomsScreen
import com.cyclecare.ui.symptoms.SymptomsViewModel

@Composable
fun CycleCareNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = viewModel,
                onNavigateToQuickLog = { navController.navigate(Screen.QuickLog.route) },
                onNavigateToCalendar = { navController.navigate(Screen.Calendar.route) },
                onNavigateToInsights = { navController.navigate(Screen.Insights.route) },
                onNavigateToSymptoms = { navController.navigate(Screen.Symptoms.route) }
            )
        }

        composable(Screen.Calendar.route) {
            val viewModel: CalendarViewModel = hiltViewModel()
            CalendarScreen(
                viewModel = viewModel,
                onNavigateToQuickLog = { navController.navigate(Screen.QuickLog.route) }
            )
        }

        composable(Screen.QuickLog.route) {
            val viewModel: QuickLogViewModel = hiltViewModel()
            QuickLogScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Insights.route) {
            val viewModel: InsightsViewModel = hiltViewModel()
            InsightsScreen(
                viewModel = viewModel
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToOnboarding = { navController.navigate(Screen.Onboarding.route) }
            )
        }

        composable(Screen.Symptoms.route) {
            val viewModel: SymptomsViewModel = hiltViewModel()
            SymptomsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Onboarding.route) {
            val viewModel: OnboardingViewModel = hiltViewModel()
            OnboardingScreen(
                viewModel = viewModel,
                onFinish = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
