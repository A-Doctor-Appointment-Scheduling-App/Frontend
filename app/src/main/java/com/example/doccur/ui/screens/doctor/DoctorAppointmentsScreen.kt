package com.example.doccur.ui.screens.doctor

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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.doccur.R
import com.example.doccur.entities.Appointment
import com.example.doccur.ui.screens.patient.AppointmentCard
import com.example.doccur.viewmodel.DoctorViewModel
// DoctorAppointmentsScreen.kt
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoctorAppointmentsScreen(appointmentList: List<Appointment>) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Header with date
        Text(
            text = "Appointment Calendar",
            style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Date display
        Text(
            text = "<17 March 2025>",
            style = MaterialTheme.typography.subtitle1,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (appointmentList.isEmpty()) {
            Text("No appointments for today.")
        } else {
            LazyColumn {
                items(appointmentList) { appointment ->
                    if (appointment.status.equals("Available", ignoreCase = true)) {
                        // Show simplified card for available slot
                        AppointmentCard(
                            time = appointment.time,
                            patientName = null,
                            appointmentType = null,
                            status = "Available",
                            statusColor = Color(0xFFE8F5E9), // light green for availability
                            profileImage = null
                        )
                    } else {
                        // Show detailed card for booked appointment
                        val statusColor = when (appointment.status) {
                            "Scheduled" -> Color(0xFFFFF59D) // light yellow
                            "Confirmed" -> Color(0xFFC8E6C9) // light green
                            "Completed" -> Color(0xFFBBDEFB) // light blue
                            "Cancelled" -> Color(0xFFE0E0E0) // grey
                            else -> Color(0xFFFFFFFF)        // default white
                        }

                        AppointmentCard(
                            time = appointment.time,
                            patientName = "${appointment.patient.firstName} ${appointment.patient.lastName}",
                            appointmentType = appointment.status,
                            status = appointment.status,
                            statusColor = statusColor,
                            profileImage = R.drawable.woman // Use dynamic image if available
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
        // Show green "Available" bar
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
                color = Color(0xFF2E7D32) // dark green
            )
        }
    } else {
        // Regular appointment card with details
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
                color = if (status == "Confirmed") Color(0xFFB2D6FF) else Color(0xFFAAFFBD),
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
