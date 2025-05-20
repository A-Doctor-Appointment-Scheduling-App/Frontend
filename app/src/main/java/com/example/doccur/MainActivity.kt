package com.example.doccur

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.doccur.api.RetrofitClient
import com.example.doccur.navigation.DocNavGraph
import com.example.doccur.navigation.PatientNavGraph
import com.example.doccur.repositories.NotificationRepository
import com.example.doccur.ui.components.DocBottomBar
import com.example.doccur.ui.components.PatientBottomBar
import com.example.doccur.ui.theme.DoccurTheme
import com.example.doccur.viewmodels.PatientAppointmentsViewModel
import com.example.doccur.viewmodels.SessionViewModel
import com.jakewharton.threetenabp.AndroidThreeTen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)

        val repository = NotificationRepository(RetrofitClient.apiService)

        setContent {
            DoccurTheme {
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen(repository)
                }
            }
        }
    }
}

@Composable
fun MainScreen(repository: NotificationRepository) {
    val navController = rememberNavController()
    val sessionViewModel: SessionViewModel = viewModel()
    val viewModel: PatientAppointmentsViewModel = viewModel()
    val appointments by viewModel.appointments.collectAsState()
    LaunchedEffect(Unit) {
        sessionViewModel.setPatientId(2)
    }


    val patientId = sessionViewModel.patientId.value

    // Choose between "patient" and "doctor" based on app state, login, etc.
    val userType = "doctor" // Replace this with a dynamic value when needed

    if (userType == "patient") {
        if (patientId != null) {
            Scaffold(
                bottomBar = {
                    PatientBottomBar(navController)
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    PatientNavGraph(
                        navController = navController,
                        repository = repository,
                        sessionViewModel = sessionViewModel
                    )
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading patient data...")
            }
        }
    } else if (userType == "doctor") {
        Scaffold(
            bottomBar = {
                DocBottomBar(navController)
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                DocNavGraph(
                    navController = navController,
                    repository = repository
                )
            }
        }
    }
}
