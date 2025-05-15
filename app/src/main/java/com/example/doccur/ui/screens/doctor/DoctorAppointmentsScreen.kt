package com.example.doccur.ui.screens.doctor

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.example.doccur.entities.Appointment
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoctorAppointmentsScreen(appointmentList: List<Appointment>) {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

    val filteredAppointments = appointmentList.filter {
        it.date == currentDate.toString()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Header with date navigation
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { currentDate = currentDate.minusDays(1) }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Previous Day")
            }
            Text(
                text = currentDate.format(formatter),
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { currentDate = currentDate.plusDays(1) }) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next Day")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredAppointments.isEmpty()) {
            Text("No appointments for this day.")
        } else {
            LazyColumn {
                items(filteredAppointments) { appointment ->
                    if (appointment.status.equals("Available", ignoreCase = true)) {
                        AppointmentCard(
                            time = appointment.time,
                            patientName = null,
                            appointmentType = null,
                            status = "Available",
                            statusColor = Color(0xFFE8F5E9),
                            profileImage = null
                        )
                    } else {
                        val statusColor = when (appointment.status) {
                            "Scheduled" -> Color(0xFFFFF59D)
                            "Confirmed" -> Color(0xFFC8E6C9)
                            "Completed" -> Color(0xFFBBDEFB)
                            "Cancelled" -> Color(0xFFE0E0E0)
                            else -> Color.White
                        }

                        AppointmentCard(
                            time = appointment.time,
                            patientName = "${appointment.patient.firstName} ${appointment.patient.lastName}",
                            appointmentType = appointment.status,
                            status = appointment.status,
                            statusColor = statusColor,
                            profileImage = R.drawable.woman
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(
    time: String,
    patientName: String?,
    appointmentType: String?,
    status: String,
    statusColor: Color,
    profileImage: Int?
) {
    if (status.lowercase() == "available") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(color = Color(0xFFDFFFE2), shape = RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$time - Available",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color(0xFF2E7D32)
            )
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(color = statusColor, shape = RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = time, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (profileImage != null) {
                        Image(
                            painter = painterResource(id = profileImage),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .padding(end = 8.dp)
                        )
                    }
                    Column {
                        Text(text = patientName ?: "", fontWeight = FontWeight.SemiBold)
                        Text(text = appointmentType ?: "", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Surface(
                color = when (status) {
                    "Confirmed" -> Color(0xFFB2D6FF)
                    "Scheduled" -> Color(0xFFFFFF99)
                    "Completed" -> Color(0xFF90CAF9)
                    "Cancelled" -> Color(0xFFBDBDBD)
                    else -> Color.LightGray
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = status,
                    fontSize = 12.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
