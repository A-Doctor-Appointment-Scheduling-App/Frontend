package com.example.doccur.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.doccur.repositories.NotificationRepository
import com.example.doccur.ui.screens.patient.HomeScreen
import com.example.doccur.ui.screens.NotificationsScreen
import com.example.doccur.ui.screens.patient.PatientAppointmentsScreen
import com.example.doccur.viewmodels.NotificationViewModel
import com.example.doccur.viewmodels.NotificationViewModelFactory
import com.example.doccur.viewmodels.SessionViewModel

sealed class PatientScreen(val route: String, val title: String, val icon: ImageVector) {
    object Home : PatientScreen("home", "Home", Icons.Filled.Home)
    object Notifications : PatientScreen("notifications", "Notifications", Icons.Filled.Notifications)
    object PatientAppointments : PatientScreen("patientappointments", "Appointments", Icons.Filled.CalendarToday)
}

@Composable
fun PatientNavGraph(
    navController: NavHostController,
    repository: NotificationRepository,
    sessionViewModel: SessionViewModel
) {
    NavHost(navController, startDestination = PatientScreen.Home.route) {

        composable(PatientScreen.Home.route) {
            HomeScreen()
        }

        composable(PatientScreen.Notifications.route) {
            val viewModel: NotificationViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = NotificationViewModelFactory(repository)
            )
            val userId = sessionViewModel.patientId.value ?: 0 // fallback for safety

            NotificationsScreen(
                viewModel = viewModel,
                userId = userId,
                userType = "patient"
            )
        }

        composable(PatientScreen.PatientAppointments.route) {
            val patientId = sessionViewModel.patientId.value
            if (patientId != null) {
                PatientAppointmentsScreen(patientId = patientId)
            }
        }
    }
}
