package com.cyclecare.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Home : Screen("home", "Home", Icons.Outlined.Home)
    object Calendar : Screen("calendar", "Calendar", Icons.Outlined.CalendarToday)
    object QuickLog : Screen("quick_log", "Log", Icons.Default.Add)
    object Insights : Screen("insights", "Insights", Icons.Outlined.BarChart)
    object Profile : Screen("profile", "Profile", Icons.Outlined.Person)
    object Symptoms : Screen("symptoms", "Symptoms")
    object Onboarding : Screen("onboarding", "Onboarding")
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Calendar,
    Screen.QuickLog,
    Screen.Insights,
    Screen.Profile
)
