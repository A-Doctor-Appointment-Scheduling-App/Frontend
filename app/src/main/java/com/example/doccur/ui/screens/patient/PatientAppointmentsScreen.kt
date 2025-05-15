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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.doccur.R
import com.example.doccur.entities.Appointment
import com.example.doccur.viewmodels.PatientAppointmentsViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

enum class AppointmentTab { UPCOMING, PREVIOUS, CANCELLED }
@Composable
fun PatientAppointmentsScreen(appointmentList: List<Appointment>) {
    var selectedTab by remember { mutableStateOf(AppointmentTab.UPCOMING) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
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
            AppointmentTab.UPCOMING -> appointmentList.filter {
                it.status.lowercase() != "cancelled" &&
                        LocalDate.parse(it.date, formatter) >= today
            }
            AppointmentTab.PREVIOUS -> appointmentList.filter {
                it.status.lowercase() != "cancelled" &&
                        LocalDate.parse(it.date, formatter) < today
            }
            AppointmentTab.CANCELLED -> appointmentList.filter {
                it.status.lowercase().equals("cancelled", ignoreCase = true)
            }
        }

        if (filteredAppointments.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No appointments available.")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredAppointments) { appointment ->
                    AppointmentCard(appointment)
                }
            }
        }
    }
}


@Composable
fun AppointmentCard(appointment: Appointment) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.doctorwoman), // Replace with actual image loader
                    contentDescription = "Doctor Profile",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        "Dr. ${appointment.doctor.firstName} ${appointment.doctor.lastName}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        "Cardiologist", // Or appointment.doctor.specialty
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = appointment.status,
                    color = if (appointment.status == "Confirmed") Color(0xFF2ECC71) else Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, contentDescription = "Time", tint = Color.Gray, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("${appointment.date} at ${appointment.time}", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = Color.Gray, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("City Hospital, Block A", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { /* Show QR Code */ }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF005EFF))) {
                    Text("   View\nQR Code", color = Color.White)
                }
                OutlinedButton(onClick = { /* Reschedule */ }) {
                    Text("Reschedule")
                }
                OutlinedButton(onClick = { /* Cancel */ }, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)) {
                    Text("Cancel")
                }
            }
        }
    }
}
