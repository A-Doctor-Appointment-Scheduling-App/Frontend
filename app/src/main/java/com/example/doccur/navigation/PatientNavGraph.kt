package com.example.doccur.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.doccur.repositories.NotificationRepository
import com.example.doccur.ui.screens.patient.HomeScreen
import com.example.doccur.ui.screens.NotificationsScreen
import com.example.doccur.ui.screens.patient.PatientAppointmentsScreen
import com.example.doccur.viewmodels.NotificationViewModel
import com.example.doccur.viewmodels.NotificationViewModelFactory
import com.example.doccur.viewmodels.PatientAppointmentsViewModel

// Screen objects for navigation
sealed class PatientScreen(val route: String, val title: String, val icon: ImageVector) {
    object Home : PatientScreen("home", "Home", Icons.Filled.Home)
    object Notifications : PatientScreen("notifications", "Notifications", Icons.Filled.Notifications)
    object PatientAppointments : PatientScreen("patientappointments", "PatientAppointments", Icons.Filled.CalendarToday)

}
@Composable
fun PatientNavGraph(
    navController: NavHostController,
    repository: NotificationRepository
) {
    NavHost(navController, startDestination = PatientScreen.Home.route) {

        // Home screen
        composable(PatientScreen.Home.route) {
            HomeScreen()
        }

        // Notifications screen
        composable(PatientScreen.Notifications.route) {
            val viewModel: NotificationViewModel = viewModel(
                factory = NotificationViewModelFactory(repository)
            )
            val userId = 3
            NotificationsScreen(
                viewModel = viewModel,
                userId = userId,
                userType = "patient"
            )
        }

        // Appointments screen
        composable(PatientScreen.PatientAppointments.route) {
            val viewModel: PatientAppointmentsViewModel = viewModel()
            val appointments by viewModel.appointments.collectAsState()
            PatientAppointmentsScreen(appointmentList = appointments)
        }

    }
}
