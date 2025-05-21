package com.example.doccur

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.doccur.api.RetrofitClient
import com.example.doccur.navigation.DocNavGraph
import com.example.doccur.navigation.PatientNavGraph
import com.example.doccur.repositories.NotificationRepository
import com.example.doccur.ui.components.DocBottomBar
import com.example.doccur.ui.components.PatientBottomBar
import com.example.doccur.ui.theme.DoccurTheme
import com.example.doccur.viewmodels.DoctorViewModel
import com.example.doccur.viewmodels.PatientAppointmentsViewModel
import com.example.doccur.viewmodels.SessionViewModel
import com.jakewharton.threetenabp.AndroidThreeTen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)

        Log.d("MainActivity", "Activity created")

        val repository = NotificationRepository(RetrofitClient.apiService)
        Log.d("MainActivity", "Repository initialized")

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
    val context = LocalContext.current

    // Debug logs
    LaunchedEffect(Unit) {
        Log.d("MainScreen", "Composable initialized")
    }

    // Choose between "patient" and "doctor" based on app state, login, etc.
    val userType = "patient" // This should come from your auth system

    LaunchedEffect(userType) {
        Log.d("MainScreen", "User type set to: $userType")
        when (userType) {
            "patient" -> {
                sessionViewModel.setPatientSession(2)
                Log.d("MainScreen", "Patient session set with ID 1")
            }
            "doctor" -> {
                sessionViewModel.setDoctorSession(1)
                Log.d("MainScreen", "Doctor session set with ID 1")
            }
        }
    }

    // Observe session states
    val patientId by sessionViewModel.patientId.collectAsState()
    val doctorId by sessionViewModel.doctorId.collectAsState()

    LaunchedEffect(patientId, doctorId) {
        Log.d("MainScreen", "Current IDs - Patient: $patientId, Doctor: $doctorId")
    }

    when (userType) {
        "patient" -> {
            if (patientId != null) {
                Log.d("MainScreen", "Building patient UI")
                PatientUI(navController, repository, sessionViewModel)
            } else {
                Log.w("MainScreen", "Patient ID is null")
                LoadingUI("Patient data")
            }
        }
        "doctor" -> {
            if (doctorId != null) {
                Log.d("MainScreen", "Building doctor UI")
                DoctorUI(navController, repository)
            } else {
                Log.w("MainScreen", "Doctor ID is null")
                LoadingUI("Doctor data")
            }
        }
        else -> {
            Log.e("MainScreen", "Invalid user type: $userType")
            LoadingUI("User data")
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun PatientUI(
    navController: NavHostController,
    repository: NotificationRepository,
    sessionViewModel: SessionViewModel
) {
    val viewModel: PatientAppointmentsViewModel = viewModel()

    LaunchedEffect(sessionViewModel.patientId.value) {
        sessionViewModel.patientId.value?.let { id ->
            Log.d("PatientUI", "Fetching appointments for patient $id")
            viewModel.fetchAppointments(id)
        }
    }

    Scaffold(
        bottomBar = { PatientBottomBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            PatientNavGraph(
                navController = navController,
                repository = repository,
                sessionViewModel = sessionViewModel
            )
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun DoctorUI(
    navController: NavHostController,
    repository: NotificationRepository
) {
    val viewModel: DoctorViewModel = viewModel()
    val sessionViewModel: SessionViewModel = viewModel()

    LaunchedEffect(sessionViewModel.doctorId.value) {
        sessionViewModel.doctorId.value?.let { id ->
            Log.d("DoctorUI", "Fetching appointments for doctor $id")
            viewModel.fetchAppointments(id)
        }
    }

    Scaffold(
        bottomBar = { DocBottomBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            DocNavGraph(
                navController = navController,
                repository = repository
            )
        }
    }
}

@Composable
private fun LoadingUI(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading $message...")
        }
    }
}