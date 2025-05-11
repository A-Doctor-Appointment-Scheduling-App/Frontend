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
                    AppointmentCard(
                        time = appointment.time,
                        patientName = "${appointment.patient.firstName} ${appointment.patient.lastName}",
                        appointmentType = appointment.status,
                        status = appointment.status,
                        statusColor = when (appointment.status) {
                            "Confirmed" -> Color(0xFFDFFFE2)
                            "Available" -> Color(0xFFE8F5E9)
                            "New Appt" -> Color(0xFFE3F2FD)
                            else -> Color(0xFFEEEEEE)
                        },
                        profileImage = R.drawable.woman // Just a placeholder
                    )
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(
    time: String,
    patientName: String,
    appointmentType: String,
    status: String,
    statusColor: Color,
    profileImage: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 2.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
        ) {
            // Time and status row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = time.take(5), // Display only hours and minutes
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = status,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            // Divider
            Divider(
                color = Color.LightGray.copy(alpha = 0.5f),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Patient info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = profileImage),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .padding(end = 12.dp)
                )

                Column {
                    Text(
                        text = patientName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = appointmentType,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            // Status indicator at bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(color = statusColor)
            )
        }
    }
}