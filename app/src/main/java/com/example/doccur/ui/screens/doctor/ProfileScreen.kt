package com.example.doccur.ui.screens.doctor

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.doccur.R
import com.example.doccur.entities.Doctorr
import com.example.doccur.viewmodels.DoctorViewModelFactory
import com.example.doccur.viewmodels.DoctorrViewModel
import com.example.doccur.api.RetrofitClient.BASE_URL
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalContext


@Composable
fun ProfileScreen(
    viewModel: DoctorrViewModel = viewModel(factory = DoctorViewModelFactory())
) {
    val doctorId = 1 // ID statique temporaire

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
fun DoctorProfileDetails(doctor: Doctorr) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Photo et nom
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .padding(4.dp),
                shape = CircleShape,
                color = MaterialTheme.colors.surface,
                shadowElevation = 4.dp
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(BASE_URL + doctor.photo_url)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Doctor profile photo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,

                )
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
            doctor.facebook_link?.let { link ->
                SocialMediaIcon(
                    iconRes = R.drawable.ic_facebook,
                    onClick = { openSocialMediaLink(link, context) },
                    contentDescription = "Facebook"
                )
            }
            doctor.instagram_link?.let { link ->
                SocialMediaIcon(
                    iconRes = R.drawable.ic_instagram,
                    onClick = { openSocialMediaLink(link, context) },
                    contentDescription = "Instagram"
                )
            }
            doctor.twitter_link?.let { link ->
                SocialMediaIcon(
                    iconRes = R.drawable.twitter,
                    onClick = { openSocialMediaLink(link, context) },
                    contentDescription = "Twitter"
                )
            }
            doctor.linkedin_link?.let { link ->
                SocialMediaIcon(
                    iconRes = R.drawable.linkedin,
                    onClick = { openSocialMediaLink(link, context) },
                    contentDescription = "LinkedIn"
                )
            }
        }
    }
}

private fun openSocialMediaLink(url: String, context: android.content.Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        // Handle case where no browser is available
        // You could show a toast message here if you want
    }
}

@Composable
fun SocialMediaIcon(
    iconRes: Int,
    onClick: () -> Unit,
    contentDescription: String
) {
    val context = LocalContext.current
    IconButton(onClick = onClick) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp)
        )
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