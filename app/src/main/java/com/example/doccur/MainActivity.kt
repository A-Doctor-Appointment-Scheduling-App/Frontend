package com.example.doccur

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.doccur.api.RetrofitClient
import com.example.doccur.navigation.DocNavGraph
import com.example.doccur.navigation.PatientNavGraph
import com.example.doccur.repositories.DoctorAppointmentRepository
import com.example.doccur.repositories.NotificationRepository
import com.example.doccur.ui.components.DocBottomBar
import com.example.doccur.ui.components.PatientBottomBar
import com.example.doccur.ui.theme.DoccurTheme


class MainActivity : ComponentActivity() {
    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 100
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register permission result handler
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted, notifications will work
            } else {
                // Permission denied
                // You might want to show a message to the user
            }
        }

        // Check and request notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                }
                else -> {
                    // Request the permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }

        val notificationRepository = NotificationRepository(RetrofitClient.apiService)
        val doctorAppointmentRepository = DoctorAppointmentRepository(RetrofitClient.apiService)

        setContent {
            DoccurTheme {
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen(notificationRepository,doctorAppointmentRepository)
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    notificationRepository: NotificationRepository,
    doctorAppointmentRepository: DoctorAppointmentRepository,
) {
    val navController = rememberNavController()
    val userType = "doctor"

    if (userType == "patient") { // Changed from === to ==
        Scaffold(
            bottomBar = {
                PatientBottomBar(navController)
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                PatientNavGraph(navController, notificationRepository)
            }
        }
    } else if (userType == "doctor") { // Changed from === to ==
        Scaffold(
            bottomBar = {
                DocBottomBar(navController)
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                DocNavGraph(
                    navController,
                    notificationRepository,
                    doctorAppointmentRepository
                )
            }
        }
    }
}