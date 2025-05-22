package com.example.doccur.ui.components

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import com.example.doccur.navigation.DoctorScreen

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val doctorBottomNavItems = listOf(
    BottomNavItem("Home", Icons.Default.Home, "doctor_home"),
    BottomNavItem("Appointments", Icons.Default.CalendarToday, "doctor_appointments"),
    BottomNavItem("Notifications", Icons.Filled.Notifications, "notifications"),

            BottomNavItem("Profile", Icons.Default.Person, "doctor_profile")
)

@Composable
fun DocBottomBar(navController: NavController) {
    BottomNavigation {
        val navBackStackEntry = navController.currentBackStackEntry
        val currentRoute = navBackStackEntry?.destination?.route

        doctorBottomNavItems.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Pop up to the start destination so you don't accumulate back stack
                            popUpTo("doctor_home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}