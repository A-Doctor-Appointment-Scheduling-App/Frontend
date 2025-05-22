package com.example.doccur

import PatientHomeScreen
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.doccur.api.RetrofitClient
import com.example.doccur.navigation.DocNavGraph
import com.example.doccur.navigation.DoctorScreen
import com.example.doccur.navigation.PatientNavGraph
import com.example.doccur.repositories.AppointmentRepository
import com.example.doccur.repositories.HomeRepository
import com.example.doccur.repositories.NotificationRepository
import com.example.doccur.repositories.ProfileRepository
import com.example.doccur.repositories.UsersRepository
import com.example.doccur.repository.AuthRepository
import com.example.doccur.repository.PrescriptionRepository
import com.example.doccur.ui.components.DocBottomBar
import com.example.doccur.ui.components.PatientBottomBar
import com.example.doccur.ui.screens.LoginScreen
import com.example.doccur.ui.screens.NotificationsScreen
import com.example.doccur.ui.screens.RegisterScreen
import com.example.doccur.ui.screens.doctor.AppointmentDetailsScreen
import com.example.doccur.ui.screens.doctor.AppointmentsScreen
import com.example.doccur.ui.screens.doctor.DoctorHomeScreen
import com.example.doccur.ui.theme.DoccurTheme
import com.example.doccur.util.TokenManager
import com.example.doccur.viewmodel.AuthViewModel
import com.example.doccur.viewmodel.AuthViewModelFactory
import com.example.doccur.viewmodels.AppointmentViewModelFactory
import com.example.doccur.viewmodels.HomeViewModelFactory
import com.example.doccur.viewmodels.NotificationViewModelFactory
import com.example.doccur.viewmodels.ProfileViewModelFactory

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object DoctorHome : Screen("doctor_home")
    object DoctorAppointments : Screen("doctor_appointments")
    object AppointmentDetails : Screen(
        "appointmentDetails/{appointmentId}",
    ) {
        fun createRoute(appointmentId: Int) = "appointmentDetails/$appointmentId"
    }
    object Notifications : Screen("notifications")

    object DoctorProfile : Screen("doctor_profile")
    object PatientHome : Screen("patient_home")
    object PatientAppointments : Screen("patient_appointments")
    object PatientProfile : Screen("patient_profile")
    object Prescriptions : Screen("prescriptions")
    object PrescriptionDetail : Screen("prescriptions/{prescriptionId}") {
        fun createRoute(id: Int) = "prescriptions/$id"
    }
    object PrescriptionCreate : Screen("prescriptions/create")
}
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
        val tokenManager = TokenManager(this)
        val authRepository = AuthRepository(RetrofitClient.apiService)
        val prescriptionRepository = PrescriptionRepository(RetrofitClient.apiService)
        val notificationRepository = NotificationRepository(RetrofitClient.apiService)
        val homeRepository = HomeRepository(RetrofitClient.apiService)
        val appointmentRepository = AppointmentRepository(RetrofitClient.apiService)
        val profileRepository = ProfileRepository(RetrofitClient.apiService)
        val usersRepository = UsersRepository(RetrofitClient.apiService)

        setContent {
            DoccurTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    AppMainNav(
                        tokenManager = tokenManager,
                        authRepository = authRepository,
                        prescriptionRepository = prescriptionRepository,
                        notificationRepository = notificationRepository,
                        homeRepository = homeRepository,
                        appointmentRepository = appointmentRepository,
                        profileRepository = profileRepository,
                        usersRepository = usersRepository
                    )
                }
            }
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppMainNav(
    tokenManager: TokenManager,
    authRepository: AuthRepository,
    prescriptionRepository: PrescriptionRepository,
    notificationRepository: NotificationRepository,
    homeRepository: HomeRepository,
    appointmentRepository: AppointmentRepository,
    profileRepository: ProfileRepository,
    usersRepository: UsersRepository
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(authRepository, tokenManager))

    // State for login and user type
    var isLoggedIn by remember { mutableStateOf(tokenManager.isLoggedIn()) }
    var isDoctor by remember { mutableStateOf(if (isLoggedIn) tokenManager.isDoctor() else null) }

    // Determine the start destination based on login state
    val startDestination = if (isLoggedIn) {
        if (isDoctor == true) "doctor_home" else "patient_home"
    } else {
        "login"
    }

// Add logging to verify state updates
    LaunchedEffect(authViewModel.loginState) {
        val currentLoggedIn = tokenManager.isLoggedIn()
        isLoggedIn = currentLoggedIn
        isDoctor = if (currentLoggedIn) tokenManager.isDoctor() else null
        Log.d("MainActivity", "isLoggedIn: $isLoggedIn, isDoctor: $isDoctor")
        if (currentLoggedIn) {
            val homeRoute = if (tokenManager.isDoctor()) "doctor_home" else "patient_home"
            Log.d("MainActivity", "Navigating to $homeRoute")
            navController.navigate(homeRoute) {
                popUpTo("login") { inclusive = true }
                launchSingleTop = true
            }
        } else {
            Log.d("MainActivity", "Navigating to login")
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // Current route for bottom bar logic
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Define your bottom bar route names
    val doctorBarRoutes = listOf("doctor_home", "doctor_appointments", "doctor_profile")
    val patientBarRoutes = listOf("patient_home", "patient_appointments", "patient_profile")

    val showBottomBar = when {
        doctorBarRoutes.contains(currentRoute) -> true
        patientBarRoutes.contains(currentRoute) -> true
        else -> false
    }
    Log.d("MainActivity", "showBottomBar: $showBottomBar, currentRoute: $currentRoute, is doctor : $isDoctor, islogin: $isLoggedIn")
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                if (tokenManager.isDoctor()){
                    Log.d("am in doctor sir "," am in doctor sir ")
                    DocBottomBar(navController)}
                else PatientBottomBar(navController)
            }
        }
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = startDestination // Use the conditional start destination
            ) {
                composable(Screen.Login.route) {
                    LoginScreen(
                        viewModel = authViewModel,
                        onNavigateToRegister = { navController.navigate("register") },
                        onLoginSuccess = {
                            val homeRoute = if (tokenManager.isDoctor()) Screen.DoctorHome.route else Screen.PatientHome.route
                            navController.navigate(homeRoute) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(Screen.Register.route) {
                    RegisterScreen(
                        viewModel = authViewModel,
                        onNavigateToLogin = { navController.navigateUp() },
                        onRegisterSuccess = {
                            Toast.makeText(context, "Inscription réussie, connectez-vous", Toast.LENGTH_SHORT).show()
                            navController.navigate(Screen.Login.route) { popUpTo(Screen.Register.route) { inclusive = true } }
                        }
                    )
                }
                composable(Screen.DoctorHome.route) {
                    DoctorHomeScreen(
                        viewModel = viewModel(factory = HomeViewModelFactory(homeRepository)),
                        profileViewModel = viewModel(factory = ProfileViewModelFactory(profileRepository)),
                        userId = tokenManager.getUserId(),

                        )
                }

                composable(Screen.PatientHome.route) {
                    PatientHomeScreen(
                        viewModel = viewModel(factory = HomeViewModelFactory(homeRepository)),
                        patientId = 2
                    )
                }

                composable(Screen.DoctorAppointments.route) {
                    AppointmentsScreen(
                        navController = navController,

                        doctorId = tokenManager.getUserId(),
                        viewModel = viewModel(factory = AppointmentViewModelFactory(appointmentRepository))
                    )
                }
                composable(
                    route = DoctorScreen.AppointmentDetails.route,
                    arguments = listOf(navArgument("appointmentId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val appointmentId = backStackEntry.arguments?.getInt("appointmentId") ?: return@composable
                    AppointmentDetailsScreen(
                        appointmentId = appointmentId,
                        viewModel = viewModel(factory = AppointmentViewModelFactory(appointmentRepository)),
                    )
                }


                composable(DoctorScreen.Notifications.route) {

                    NotificationsScreen(
                        viewModel = viewModel(factory = NotificationViewModelFactory(notificationRepository,LocalContext.current,"ws://192.168.148.132:8000")),
                        userId = tokenManager.getUserId(),
                        userType = "doctor"
                    )
                }
            }
        }
    }
}