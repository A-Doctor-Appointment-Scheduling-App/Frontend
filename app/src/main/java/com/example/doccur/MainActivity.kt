package com.example.doccur

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.doccur.api.RetrofitClient
import com.example.doccur.repository.AuthRepository
import com.example.doccur.repository.PrescriptionRepository
import com.example.doccur.ui.screens.*
import com.example.doccur.ui.theme.DocCurTheme
import com.example.doccur.util.TokenManager
import com.example.doccur.viewmodel.AuthViewModel
import com.example.doccur.viewmodel.AuthViewModelFactory
import com.example.doccur.viewmodel.PrescriptionViewModel
import com.example.doccur.viewmodel.PrescriptionViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize TokenManager and RetrofitClient
        val tokenManager = TokenManager(this)
//        RetrofitClient.initialize(tokenManager)

        setContent {
            DocCurTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    
                    // App's navigation setup
                    DocCurApp(
                        navController = navController,
                        tokenManager = tokenManager
                    )
                }
            }
        }
    }
}

@Composable
fun DocCurApp(
    navController: NavHostController,
    tokenManager: TokenManager
) {
    val context = LocalContext.current
    
    // Set up AuthRepository
    val authRepository = remember { AuthRepository(RetrofitClient.apiService) }
    
    // Set up PrescriptionRepository
    val prescriptionRepository = remember { PrescriptionRepository(RetrofitClient.apiService) }
    
    // Create ViewModels
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(authRepository, tokenManager)
    )
    
    val prescriptionViewModel: PrescriptionViewModel = viewModel(
        factory = PrescriptionViewModelFactory(prescriptionRepository)
    )
    
    // Define the starting destination based on authentication status
    val startDestination = if (tokenManager.isLoggedIn()) {
        if(tokenManager.isDoctor()){
            "prescriptions"
        }else {
            "prescriptions_patient"
        }
    } else {
        "login"
    }
    
    NavHost(navController = navController, startDestination = startDestination) {
        // Login screen
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate("register") },
                onLoginSuccess = { navController.navigate("home") { popUpTo("login") { inclusive = true } } }
            )
        }
        
        // Register screen
        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.navigateUp() },
                onRegisterSuccess = {
                    Toast.makeText(context, "Registration successful! Please log in.", Toast.LENGTH_SHORT).show()
                    navController.navigate("login") { popUpTo("register") { inclusive = true } }
                }
            )
        }
        
        // Home screen - Would be implemented in a real app
        composable("home") {
            // For this demo, we'll just redirect to the prescription list
            LaunchedEffect(Unit) {


                    if(tokenManager.isDoctor()){
                        navController.navigate("prescriptions")
                    }else {
                        navController.navigate("prescriptions_patient")
                    }

            }
        }
        
        // Prescriptions list screen
        composable("prescriptions") {
            PrescriptionListScreen(
                viewModel = prescriptionViewModel,
                tokenManager = tokenManager,
                onPrescriptionClick = { prescriptionId ->
                    navController.navigate("prescriptions/$prescriptionId")
                },
                onCreatePrescriptionClick = {
                    navController.navigate("prescriptions/create")
                },
                onBackClick = {

                    Toast.makeText(context, "This would go back to main menu", Toast.LENGTH_SHORT).show()
                },
                authViewModel = authViewModel,

                )
        }
        
        // Prescription detail screen
        composable(
            route = "prescriptions/{prescriptionId}",
            arguments = listOf(navArgument("prescriptionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val prescriptionId = backStackEntry.arguments?.getInt("prescriptionId") ?: 0
            
            PrescriptionDetailScreen(
                viewModel = prescriptionViewModel,
                prescriptionId = prescriptionId,
                onBackClick = { navController.navigateUp() },
                onDownloadClick = { id ->
                    // In a real app, this would trigger a download
                    Toast.makeText(context, "Downloading prescription PDF", Toast.LENGTH_SHORT).show()
                }
            )
        }
        
        // Create prescription screen
        composable("prescriptions/create") {
            CreatePrescriptionScreen(
                viewModel = prescriptionViewModel,
                onBackClick = { navController.navigateUp() },
                onPrescriptionCreated = {
                    Toast.makeText(context, "Prescription created successfully", Toast.LENGTH_SHORT).show()
                    navController.navigate("prescriptions") {
                        popUpTo("prescriptions") { inclusive = true }
                    }
                }
            )
        }
        composable("prescriptions_patient") {
            PatientPrescriptionsScreen(
                viewModel = prescriptionViewModel,
                tokenManager = tokenManager,
                onPrescriptionClick = { prescriptionId ->
                    navController.navigate("prescriptions/$prescriptionId")
                },
                onBackClick = {

                    Toast.makeText(context, "This would go back to main menu", Toast.LENGTH_SHORT).show()
                },
            )
        }
    }
}