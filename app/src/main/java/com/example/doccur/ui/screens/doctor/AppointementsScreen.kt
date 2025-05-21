import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.doccur.ui.screens.doctor.AppointmentDetailsScreen
import com.example.doccur.viewmodels.AppointmentViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AppointementsScreen(
    navController: NavHostController,
    viewModel: AppointmentViewModel
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val sheetHeight = screenHeight * 0.5f

    var showContent by remember { mutableStateOf(false) }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetBackgroundColor = Color.White,
        backgroundColor = Color.White,
        sheetContent = {
            if (showContent) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(sheetHeight)
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    AppointmentDetailsScreen(
                        appointmentId = 8,
                        viewModel = viewModel,
                        navController = navController
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(1.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Button(onClick = {
                coroutineScope.launch {
                    showContent = false // Reset before showing again
                    scaffoldState.bottomSheetState.collapse()
                    showContent = true
                    scaffoldState.bottomSheetState.expand()
                }
            }) {
                Text("Test Appointment Details")
            }
        }
    }
}
