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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doccur.R
import com.example.doccur.entities.AppointmentResponse
import com.example.doccur.viewmodels.DoctorAppointmentViewModel
import androidx.compose.foundation.shape.CircleShape
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoctorAppointmentsScreen(
    viewModel: DoctorAppointmentViewModel,
    doctorId: Int,
    onAppointmentClick: (AppointmentResponse) -> Unit = {}
) {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
    val appointments by viewModel.appointments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()

    // Trigger fetch when date changes
    LaunchedEffect(currentDate) {
        viewModel.fetchAppointments(doctorId)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Date navigation header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { currentDate = currentDate.minusDays(1) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Previous Day",
                    tint = MaterialTheme.colors.primary
                )
            }

            Text(
                text = currentDate.format(formatter),
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            IconButton(
                onClick = { currentDate = currentDate.plusDays(1) },
                modifier = Modifier.size(48.dp)
            ) {
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
                        Text(
                            text = error ?: "Unknown error occurred",
                            color = MaterialTheme.colors.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            viewModel.fetchAppointments(doctorId)
                        }) {
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
                        Text(
                            text = "No appointments for this day",
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredAppointments) { appointment ->
                            AppointmentCard(
                                appointment = appointment,
                                onClick = { onAppointmentClick(appointment) }
                            )
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
                    painter = painterResource(id = R.drawable.woman),
                    contentDescription = "Patient",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Patient ID: ${appointment.patient}",
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

            Spacer(modifier = Modifier.height(12.dp))

            if (!appointment.qrCode.isNullOrEmpty()) {
                Text(
                    text = "QR Code: Available",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
