package com.example.doccur.ui.screens.patient

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doccur.R
import com.example.doccur.entities.Appointment

@Composable
fun PatientAppointmentsScreen(appointmentList: List<Appointment>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("My Appointments", style = MaterialTheme.typography.h5)

        if (appointmentList.isEmpty()) {
            Text("No appointments available.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(appointmentList) { appointment ->
                    AppointmentCard(appointment)
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(appointment: Appointment) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Dr. ${appointment.doctor.firstName} ${appointment.doctor.lastName}", fontWeight = FontWeight.Bold)
            Text("Date: ${appointment.date} at ${appointment.time}")
            Text("Status: ${appointment.status}")
        }
    }
}
