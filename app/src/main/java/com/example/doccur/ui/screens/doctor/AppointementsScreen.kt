package com.example.doccur.ui.screens.doctor

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doccur.R
import com.example.doccur.entities.AppointmentResponse
import com.example.doccur.viewmodels.AppointmentViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AppointmentsScreen(
    navController: NavHostController,
    viewModel: AppointmentViewModel,
    doctorId: Int,

) {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
    val appointments by viewModel.appointments.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    val scaffoldState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val sheetHeight = screenHeight * 0.5f

    var selectedAppointment by remember { mutableStateOf<AppointmentResponse?>(null) }
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(currentDate) {
        viewModel.fetchAppointmentsForDoctor(doctorId)
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            if (showContent && selectedAppointment != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(sheetHeight)
                        .padding(16.dp)
                        .background(Color.White)
                ) {
                    AppointmentDetailsScreen(
                        appointmentId = selectedAppointment!!.id,
                        viewModel)
                }
            } else {
                Spacer(modifier = Modifier.height(1.dp))
            }
        },
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { currentDate = currentDate.minusDays(1) }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Previous Day",
                        tint = MaterialTheme.colors.primary
                    )
                }
                Text(
                    text = currentDate.format(formatter),
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.primary
                )
                IconButton(onClick = { currentDate = currentDate.plusDays(1) }) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next Day",
                        tint = MaterialTheme.colors.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Error",
                                tint = MaterialTheme.colors.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(error ?: "Unknown error occurred", color = MaterialTheme.colors.error)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.fetchAppointmentsForDoctor(doctorId) }) {
                                Text("Retry")
                            }
                        }
                    }
                }

                else -> {
                    val filteredAppointments = appointments
                        .filter { it.date == currentDate.toString() }
                        .sortedBy { it.time }

                    if (filteredAppointments.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No appointments for this day", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredAppointments) { appointment ->
                                AppointmentCard(appointment = appointment) {
                                    coroutineScope.launch {
                                        showContent = false
                                        scaffoldState.bottomSheetState.collapse()
                                        selectedAppointment = appointment
                                        showContent = true
                                        scaffoldState.bottomSheetState.expand()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(
    appointment: AppointmentResponse,
    onClick: () -> Unit
) {
    val statusColor = when (appointment.status.lowercase()) {
        "confirmed" -> Color(0xFFE8F5E9)
        "completed" -> Color(0xFFE3F2FD)
        "cancelled" -> Color(0xFFFFEBEE)
        "pending" -> Color(0xFFFFF8E1)
        else -> Color(0xFFEEEEEE)
    }

    val statusTextColor = when (appointment.status.lowercase()) {
        "confirmed" -> Color(0xFF2E7D32)
        "completed" -> Color(0xFF0D47A1)
        "cancelled" -> Color(0xFFC62828)
        "pending" -> Color(0xFFF57F17)
        else -> Color(0xFF424242)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .background(statusColor)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = appointment.time,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = statusColor,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, statusTextColor)
                ) {
                    Text(
                        text = appointment.status.replaceFirstChar { it.uppercase() },
                        color = statusTextColor,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.doctor),
                    contentDescription = "Patient",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = appointment.patient.fullName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Date: ${appointment.date}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

