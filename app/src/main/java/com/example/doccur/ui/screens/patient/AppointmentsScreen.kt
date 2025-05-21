package com.example.doccur.ui.screens.patient

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.doccur.R
import com.example.doccur.api.RetrofitClient
import androidx.compose.runtime.produceState
import kotlinx.coroutines.delay
import androidx.compose.runtime.produceState
import androidx.compose.ui.text.style.TextAlign
import com.example.doccur.api.RetrofitClient.BASE_URL
import com.example.doccur.entities.AppointmentPatient
import com.example.doccur.viewmodels.AppointmentViewModel
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


enum class AppointmentTab { UPCOMING, PREVIOUS, CANCELLED }

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QRCodeDialog(
    qrCodeUrl: String,
    appointmentDate: String,
    appointmentTime: String,
    onDismiss: () -> Unit
) {
    // Calculate time remaining until appointment
    val timeRemaining by produceState(initialValue = "Calculating...") {
        while (true) {
            try {
                // Debug: Log input values
                println("Debug: Parsing date: '$appointmentDate', time: '$appointmentTime'")

                // Parse date (expected format: yyyy-MM-dd)
                val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val parsedDate = LocalDate.parse(appointmentDate, dateFormatter)

                // Parse time (try with and without seconds)
                val parsedTime = try {
                    LocalTime.parse(appointmentTime, DateTimeFormatter.ofPattern("HH:mm:ss"))
                } catch (e: DateTimeParseException) {
                    try {
                        LocalTime.parse(appointmentTime, DateTimeFormatter.ofPattern("HH:mm"))
                    } catch (e: DateTimeParseException) {
                        println("Error: Time format not recognized. Expected 'HH:mm:ss' or 'HH:mm'")
                        value = "Invalid time format"
                        delay(1000)
                        continue
                    }
                }

                val appointmentDateTime = parsedDate.atTime(parsedTime)
                val now = LocalDateTime.now()

                // Debug: Log parsed datetime
                println("Debug: Appointment DateTime: $appointmentDateTime, Current DateTime: $now")

                // Check if appointment is in the past
                if (appointmentDateTime.isBefore(now)) {
                    value = "00:00:00"
                    break
                }

                // Calculate remaining time
                val duration = Duration.between(now, appointmentDateTime)
                val totalSeconds = duration.seconds

                // Handle negative time (shouldn't happen due to isBefore check)
                if (totalSeconds < 0) {
                    value = "00:00:00"
                    break
                }

                val hours = totalSeconds / 3600
                val minutes = (totalSeconds % 3600) / 60
                val seconds = totalSeconds % 60

                value = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            } catch (e: Exception) {
                println("Error calculating time remaining: ${e.message}")
                value = "--:--:--"
            }

            delay(1000) // Update every second
        }
    }

    // Rest of the dialog UI remains the same...
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Appointment QR Code",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                AsyncImage(
                    model = qrCodeUrl,
                    contentDescription = "QR Code",
                    modifier = Modifier.size(250.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Show this QR code to receptionist",
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center
                )

                Card(
                    backgroundColor = Color(0xFFEEF5FF),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Time until appointment",
                            color = Color(0xFF1A73E8),
                            style = MaterialTheme.typography.body1
                        )

                        Text(
                            text = timeRemaining,
                            color = Color(0xFF1A73E8),
                            style = MaterialTheme.typography.h4,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1A73E8))
                ) {
                    Text("Back to Appointments", color = Color.White)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentsScreen(
    viewModel: AppointmentViewModel = viewModel(),
    patientId: Int
) {
    val appointments by viewModel.appointmentsForPatient.collectAsState()
    val cancelMessage by viewModel.cancelMessage.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    // State for selected tab
    var selectedTab by remember { mutableStateOf(AppointmentTab.UPCOMING) }
    // State for dialog
    var selectedAppointment by remember { mutableStateOf<AppointmentPatient?>(null) }
    // State for QR code dialog
    var qrCodeDialogData by remember { mutableStateOf<Triple<String, String, String>?>(null) }

    LaunchedEffect(Unit) {
        println("PatientAppointmentsScreen loaded for patientId = $patientId")
        viewModel.fetchAppointmentsForPatient(patientId)
    }

    // Show dialog if an appointment is selected
    selectedAppointment?.let { appointment ->
        AppointmentDetailsDialog(
            viewModel = viewModel,
            appointmentPatient = appointment,
            onDismiss = { selectedAppointment = null },
            onViewQRCode = {
                qrCodeDialogData = Triple(
                    RetrofitClient.BASE_URL1 + appointment.qrCode,
                    appointment.date,
                    appointment.time
                )
            }
        )
    }

    // Show QR code dialog if URL is set
    qrCodeDialogData?.let { (url, date, time) ->
        QRCodeDialog(
            qrCodeUrl = url,
            appointmentDate = date,
            appointmentTime = time,
            onDismiss = { qrCodeDialogData = null }
        )
    }

    // Show snackbar for messages
    val scaffoldState = rememberScaffoldState()

    // Show cancellation message if present
    LaunchedEffect(cancelMessage) {
        cancelMessage?.let {
            scaffoldState.snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            // Clear the message after showing
            viewModel.clearCancelMessage()
        }
    }

    // Show error message if present
    LaunchedEffect(error) {
        error?.let {
            scaffoldState.snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            // Clear the error after showing
            viewModel.clearError()
        }
    }

    Scaffold(
        scaffoldState = scaffoldState
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text("My Appointments", style = MaterialTheme.typography.h5)

                Spacer(modifier = Modifier.height(16.dp))

                TabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    backgroundColor = Color.Transparent,
                    contentColor = MaterialTheme.colors.primary
                ) {
                    AppointmentTab.values().forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            text = { Text(tab.name.lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val today = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

                val filteredAppointments = when (selectedTab) {
                    AppointmentTab.UPCOMING -> appointments.filter {
                        it.status.lowercase() != "cancelled" &&
                                LocalDate.parse(it.date, formatter) >= today
                    }
                    AppointmentTab.PREVIOUS -> appointments.filter {
                        it.status.lowercase() != "cancelled" &&
                                LocalDate.parse(it.date, formatter) < today
                    }
                    AppointmentTab.CANCELLED -> appointments.filter {
                        it.status.lowercase() == "cancelled"
                    }
                }

                if (loading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (filteredAppointments.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No appointments available.")
                    }
                } else {
                    LazyColumn {
                        items(filteredAppointments) { appointmentPatient ->
                            AppointmentCard(
                                viewModel = viewModel,
                                appointmentPatient = appointmentPatient,
                                onClick = { selectedAppointment = appointmentPatient },
                                onViewQRCode = {
                                    qrCodeDialogData = Triple(
                                        RetrofitClient.BASE_URL1 + appointmentPatient.qrCode,
                                        appointmentPatient.date,
                                        appointmentPatient.time
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(
    viewModel: AppointmentViewModel,
    appointmentPatient: AppointmentPatient,
    onClick: () -> Unit,
    onViewQRCode: () -> Unit
) {
    val doctor = appointmentPatient.doctor

    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick) // Add clickable modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.doctor), // Replace with actual doctor photo if available
                    contentDescription = "Doctor Profile",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Doctor: ${doctor.fullName}",
                        fontWeight = FontWeight.Bold
                    )
                    Text(doctor.speciality ?: "Specialty not available",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = appointmentPatient.status,
                    color = if (appointmentPatient.status.equals("Confirmed", ignoreCase = true)) Color(0xFF2ECC71) else Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = "Time",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "${appointmentPatient.date} at ${appointmentPatient.time}",
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("City Hospital, Block A", fontSize = 14.sp) // You can replace with appointment location if available
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onViewQRCode,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF005EFF))
                ) {
                    Text("   View\nQR Code", color = Color.White)
                }
                OutlinedButton(onClick = { /* Reschedule */ }) {
                    Text("Reschedule")
                }
                OutlinedButton(
                    onClick = {
                        // Pass both the appointment ID and patient ID
                        viewModel.cancelAppointment(appointmentPatient.id, appointmentPatient.patient.id)
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

// 3. Also update the AppointmentDetailsDialog
@Composable
fun AppointmentDetailsDialog(
    viewModel: AppointmentViewModel,
    appointmentPatient: AppointmentPatient,
    onDismiss: () -> Unit,
    onViewQRCode: () -> Unit
)  {
    val doctor = appointmentPatient.doctor

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header
                Text(
                    text = "Appointment Details",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Doctor Info Section
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = BASE_URL + doctor.profileImage ?: R.drawable.doctor,
                        contentDescription = "Doctor",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Dr. ${doctor.fullName}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = doctor.speciality ?: "Specialty not available",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Text(
                            text = appointmentPatient.status,
                            color = when(appointmentPatient.status.lowercase()) {
                                "confirmed" -> Color(0xFF2ECC71)
                                "cancelled" -> Color.Red
                                else -> Color.Gray
                            },
                            fontSize = 12.sp,
                            modifier = Modifier
                                .background(
                                    when(appointmentPatient.status.lowercase()) {
                                        "confirmed" -> Color(0xFFE8F5E9)
                                        "cancelled" -> Color(0xFFFBEAEA)
                                        else -> Color(0xFFEEEEEE)
                                    },
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Appointment Time Section
                Text(
                    text = "${appointmentPatient.date} at ${appointmentPatient.time}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "City Hospital, Block A", // Can be dynamic if available
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Doctor Contact Section
                Text(
                    text = "Doctor Contact",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onViewQRCode,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF005EFF)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("View QR Code", color = Color.White)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = { /* Reschedule */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reschedule")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = {
                            viewModel.cancelAppointment(appointmentPatient.id, appointmentPatient.patient.id)
                            onDismiss() // Dismiss the dialog after cancellation
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}