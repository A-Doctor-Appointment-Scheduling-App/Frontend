package com.example.doccur


import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.doccur.api.RetrofitClient
import com.example.doccur.navigation.DocNavGraph
import com.example.doccur.navigation.PatientNavGraph
import com.example.doccur.repositories.AppointmentRepository
import com.example.doccur.repositories.HomeRepository
import com.example.doccur.repositories.NotificationRepository
import com.example.doccur.ui.components.DocBottomBar
import com.example.doccur.ui.components.PatientBottomBar
import com.example.doccur.ui.theme.DoccurTheme


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create repository and view model factory
        val notificationRepository = NotificationRepository(RetrofitClient.apiService)
        val homeRepository = HomeRepository(RetrofitClient.apiService)
        val appointmentRepository = AppointmentRepository(RetrofitClient.apiService)


        setContent {
            DoccurTheme{
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen(notificationRepository,homeRepository,appointmentRepository)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    notificationRepository: NotificationRepository,
    homeRepository: HomeRepository,
    appointmentRepository: AppointmentRepository
) {
    val navController = rememberNavController()
    val userType = "doctor"

    if (userType === "patient"){
        Scaffold(
            bottomBar = {
                PatientBottomBar(navController)
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                PatientNavGraph(navController, notificationRepository,homeRepository)
            }
        }
    }else if (userType === "doctor"){
        Scaffold(
            bottomBar = {
                DocBottomBar(navController)
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                DocNavGraph(navController, notificationRepository, homeRepository,appointmentRepository)
            }
        }
    }


}

