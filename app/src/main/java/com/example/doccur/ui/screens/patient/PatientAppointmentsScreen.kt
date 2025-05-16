package com.example.doccur.ui.screens.patient

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
import androidx.compose.material.icons.filled.LocationOn
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
import com.example.doccur.R
import com.example.doccur.entities.Appointment
import com.example.doccur.entities.AppointmentResponse
import com.example.doccur.entities.AppointmentWithDoctor
import com.example.doccur.ui.screens.doctor.AppointmentCard
import com.example.doccur.viewmodels.PatientAppointmentsViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

enum class AppointmentTab { UPCOMING, PREVIOUS, CANCELLED }
@Composable
fun PatientAppointmentsScreen(patientId: Int) {
    val viewModel: PatientAppointmentsViewModel = viewModel()
    val appointments by viewModel.appointments.collectAsState()

    // State for selected tab
    var selectedTab by remember { mutableStateOf(AppointmentTab.UPCOMING) }
    // State for dialog
    var selectedAppointment by remember { mutableStateOf<AppointmentWithDoctor?>(null) }

    LaunchedEffect(Unit) {
        println("🚀 PatientAppointmentsScreen loaded for patientId = $patientId")
        viewModel.fetchAppointments(patientId)
    }

    // Show dialog if an appointment is selected
    selectedAppointment?.let { appointment ->
        AppointmentDetailsDialog(
            appointmentWithDoctor = appointment,
            onDismiss = { selectedAppointment = null }
        )
    }

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
                it.appointment.status.lowercase() != "cancelled" &&
                        LocalDate.parse(it.appointment.date, formatter) >= today
            }
            AppointmentTab.PREVIOUS -> appointments.filter {
                it.appointment.status.lowercase() != "cancelled" &&
                        LocalDate.parse(it.appointment.date, formatter) < today
            }
            AppointmentTab.CANCELLED -> appointments.filter {
                it.appointment.status.lowercase() == "cancelled"
            }
            else -> emptyList() // Add this to make when exhaustive
        }

        if (filteredAppointments.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No appointments available.")
            }
        } else {
            LazyColumn {
                items(filteredAppointments) { appointmentWithDoctor ->
                    AppointmentCard(
                        appointmentWithDoctor = appointmentWithDoctor,
                        onClick = { selectedAppointment = appointmentWithDoctor }
                    )
                }
            }
        }
    }
}
@Composable
fun AppointmentCard(
    appointmentWithDoctor: AppointmentWithDoctor,
    onClick: () -> Unit
) {
    val appointment = appointmentWithDoctor.appointment
    val doctor = appointmentWithDoctor.doctor

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
                    painter = painterResource(id = R.drawable.doctorwoman), // Replace with actual doctor photo if available
                    contentDescription = "Doctor Profile",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Doctor: ${doctor.firstName} ${doctor.lastName}",
                    fontWeight = FontWeight.Bold
                    )
                    Text(doctor.specialty ?: "Specialty not available",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = appointment.status,
                    color = if (appointment.status.equals("Confirmed", ignoreCase = true)) Color(0xFF2ECC71) else Color.Gray,
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
                    "${appointment.date} at ${appointment.time}",
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
                    onClick = { /* Show QR Code */ },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF005EFF))
                ) {
                    Text("   View\nQR Code", color = Color.White)
                }
                OutlinedButton(onClick = { /* Reschedule */ }) {
                    Text("Reschedule")
                }
                OutlinedButton(
                    onClick = { /* Cancel */ },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
fun AppointmentDetailsDialog(
    appointmentWithDoctor: AppointmentWithDoctor,
    onDismiss: () -> Unit
) {
    val appointment = appointmentWithDoctor.appointment
    val doctor = appointmentWithDoctor.doctor

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header
                Text("Appointment Details", style = MaterialTheme.typography.h6)

                Spacer(modifier = Modifier.height(16.dp))

                // Doctor Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.doctorwoman),
                        contentDescription = "Doctor",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Dr. ${doctor.firstName} ${doctor.lastName}",
                            fontWeight = FontWeight.Bold)
                        Text(doctor.specialty ?: "Specialty not available")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Appointment Time
                Text("Appointment Time", fontWeight = FontWeight.Bold)
                Text("${appointment.date} at ${appointment.time}")

                Spacer(modifier = Modifier.height(8.dp))

                // Location
                Text("Location", fontWeight = FontWeight.Bold)
                Text("City Hospital, Block A")

                Spacer(modifier = Modifier.height(16.dp))

                // Status
                Text(
                    text = "Status: ${appointment.status}",
                    color = when(appointment.status.lowercase()) {
                        "confirmed" -> Color.Green
                        "cancelled" -> Color.Red
                        else -> Color.Gray
                    },
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary
                    )
                ) {
                    Text("Close", color = Color.White)
                }
            }
        }
    }
}