package com.example.doccur.navigation

import DoctorAppointmentViewModelFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.doccur.repositories.DoctorAppointmentRepository
import com.example.doccur.repositories.NotificationRepository
import com.example.doccur.ui.screens.NotificationsScreen
import com.example.doccur.ui.screens.doctor.DoctorAppointmentsScreen
import com.example.doccur.ui.screens.doctor.HomeScreen
import com.example.doccur.ui.screens.doctor.PatientsScreen
import com.example.doccur.ui.screens.doctor.ProfileScreen
import com.example.doccur.viewmodels.DoctorAppointmentViewModel
import com.example.doccur.viewmodels.NotificationViewModel
import com.example.doccur.viewmodels.NotificationViewModelFactory

sealed class DoctorScreen(val route: String, val title: String, val icon: ImageVector) {
    object Home : DoctorScreen("home", "Home", Icons.Filled.Home)
    object DoctorAppointments : DoctorScreen("doctorappointments", "DoctorAppointments", Icons.Filled.CalendarToday)
    object Patients : DoctorScreen("patients", "Patients", Icons.Filled.Person)
    object Notifications : DoctorScreen("notifications", "Notifications", Icons.Filled.Notifications)
    object Profile : DoctorScreen("profile", "Profile", Icons.Filled.MedicalServices)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DocNavGraph(
    navController: NavHostController,
    repository: NotificationRepository,
    doctorAppointmentRepository: DoctorAppointmentRepository
) {

    val doctorAppointmentViewModel: DoctorAppointmentViewModel = viewModel(
        factory = DoctorAppointmentViewModelFactory(doctorAppointmentRepository)
    )

    NavHost(navController, startDestination = DoctorScreen.Home.route) {
        composable(DoctorScreen.Home.route) {
            HomeScreen()
        }
        composable(DoctorScreen.DoctorAppointments.route) {
            DoctorAppointmentsScreen(
                doctorAppointmentViewModel,
                doctorId = 1,
                onAppointmentClick = { /* navigate or show detail */ }
            )

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