package com.example.doccur.navigation


import PatientHomeScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.doccur.repositories.HomeRepository
import com.example.doccur.repositories.NotificationRepository
import com.example.doccur.ui.screens.NotificationsScreen
import com.example.doccur.viewmodels.HomeViewModel
import com.example.doccur.viewmodels.HomeViewModelFactory
import com.example.doccur.viewmodels.NotificationViewModel
import com.example.doccur.viewmodels.NotificationViewModelFactory

// Screen objects for navigation
sealed class PatientScreen(val route: String, val title: String, val icon: ImageVector) {
    object Home : PatientScreen("home", "Home", Icons.Filled.Home)
    object Notifications : PatientScreen("notifications", "Notifications", Icons.Filled.Notifications)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PatientNavGraph(
    navController: NavHostController,
    notificationRepository: NotificationRepository,
    homeRepository: HomeRepository,
) {

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(homeRepository)
    )

    val notificationViewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(
            notificationRepository,
            context = LocalContext.current,
            wsBaseUrl = "ws://172.20.10.4:8000")
    )

    NavHost(navController, startDestination = PatientScreen.Home.route) {
        composable(PatientScreen.Home.route) {
            val userId = 2
            PatientHomeScreen(
                viewModel = homeViewModel,
                patientId = userId
            )
        }
        composable(PatientScreen.Notifications.route) {
            val userId = 2
            NotificationsScreen(
                viewModel = notificationViewModel,
                userId = userId,
                userType = "patient"
            )
        }
    }
}