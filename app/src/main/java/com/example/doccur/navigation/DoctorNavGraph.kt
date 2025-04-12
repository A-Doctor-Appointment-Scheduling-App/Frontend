package com.example.doccur.navigation



import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppsOutage
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.doccur.repositories.NotificationRepository
import com.example.doccur.ui.screens.doctor.HomeScreen
import com.example.doccur.ui.screens.NotificationsScreen
import com.example.doccur.ui.screens.doctor.AppointementsScreen
import com.example.doccur.ui.screens.doctor.PatientsScreen
import com.example.doccur.ui.screens.doctor.ProfileScreen
import com.example.doccur.viewmodels.NotificationViewModel
import com.example.doccur.viewmodels.NotificationViewModelFactory

// Screen objects for navigation
sealed class DoctorScreen(val route: String, val title: String, val icon: ImageVector) {
    object Home : DoctorScreen("home", "Home", Icons.Filled.Home)
    object Appointements : DoctorScreen("appointements", "Appointements", Icons.Filled.CalendarToday)
    object Patients : DoctorScreen("patients", "Patients", Icons.Filled.Person)
    object Notifications : DoctorScreen("notifications", "Notifications", Icons.Filled.Notifications)
    object Profile : DoctorScreen("profile", "Profile", Icons.Filled.MedicalServices)

}


@Composable
fun DocNavGraph(
    navController: NavHostController,
    repository: NotificationRepository
) {
    NavHost(navController, startDestination = DoctorScreen.Home.route) {
        composable(DoctorScreen.Home.route) {
            HomeScreen()
        }

        composable(DoctorScreen.Appointements.route) {
            AppointementsScreen()
        }

        composable(DoctorScreen.Patients.route) {
            PatientsScreen()
        }

        composable(DoctorScreen.Notifications.route) {
            val viewModel: NotificationViewModel = viewModel(
                factory = NotificationViewModelFactory(repository)
            )

            val userId = 6

            NotificationsScreen(
                viewModel = viewModel,
                userId = userId,
                userType = "doctor"
            )
        }

        composable(DoctorScreen.Profile.route) {
            ProfileScreen()
        }
    }
}