package com.example.doccur.ui.screens.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.doccur.entities.DoctorProfile
import com.example.doccur.viewmodels.ProfileViewModel
import com.example.doccur.viewmodels.ProfileViewModelFactory

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    doctorId: Int
) {
    LaunchedEffect(Unit) {
        viewModel.getDoctorDetails(doctorId)
    }

    Column {
        if (viewModel.isLoading) {
            CircularProgressIndicator()
        } else if (viewModel.error != null) {
            Text("Error: ${viewModel.error}", color = Color.Red)
        } else if (viewModel.doctor != null) {
            DoctorProfileDetails(doctor = viewModel.doctor!!)
        } else {
            Text("No doctor data available")
        }
    }
}

@Composable
fun DoctorProfileDetails(doctor: DoctorProfile) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Photo et nom
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (doctor.photo_url != null) {
                AsyncImage(
                    model = doctor.photo_url,
                    contentDescription = "Doctor photo",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${doctor.first_name.first()}${doctor.last_name.first()}",
                        style = MaterialTheme.typography.h4
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "${doctor.first_name} ${doctor.last_name}",
                    style = MaterialTheme.typography.h5
                )
                Text(
                    text = doctor.specialty,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Informations de contact
        Text(
            text = "Contact Information",
            style = MaterialTheme.typography.h6
        )
        Spacer(modifier = Modifier.height(8.dp))

        InfoRow(icon = Icons.Default.Email, text = doctor.email)
        InfoRow(icon = Icons.Default.Phone, text = doctor.phone_number)

        // Clinique
        doctor.clinic?.let { clinic ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Clinic",
                style = MaterialTheme.typography.h6
            )
            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(icon = Icons.Default.Business, text = clinic.name)
            InfoRow(icon = Icons.Default.LocationOn, text = clinic.address)
        }

        // Réseaux sociaux
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Social Media",
            style = MaterialTheme.typography.h6
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row {
            doctor.facebook_link?.let {
                IconButton(onClick = { /* Ouvrir le lien */ }) {
                    Icon(imageVector = Icons.Default.Facebook, contentDescription = "Facebook")
                }
            }
            // Ajoutez les autres réseaux sociaux de la même manière
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}