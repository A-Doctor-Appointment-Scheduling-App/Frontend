package com.example.doccur.ui.screens.patient

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.doccur.R
import com.example.doccur.api.RetrofitClient.BASE_URL
import com.example.doccur.entities.Timeslot
import com.example.doccur.ui.theme.AppColors
import com.example.doccur.ui.theme.Inter
import com.example.doccur.viewmodels.AppointmentViewModel
import com.example.doccur.viewmodels.UsersViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDetailsScreen(
    appointmentViewModel: AppointmentViewModel,
    viewModel: UsersViewModel,
    doctorId: Int,
    patientId: Int,
    onNavigateBack: () -> Unit = {},
) {
    val doctor by viewModel.selectedDoctor.collectAsState()
    val context = LocalContext.current
    var showSocialMedia by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Add these state variables for appointment booking
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }
    var selectedTimeslotId by remember { mutableStateOf<Int?>(null) }

    // Observe appointment booking result
    val appointmentBookingResult by appointmentViewModel.appointmentBookingResult.collectAsState()
    val isLoading by appointmentViewModel.loading.collectAsState()
    val error by appointmentViewModel.error.collectAsState()

    LaunchedEffect(doctorId) {
        viewModel.loadDoctorDetails(doctorId)
    }

    // Handle booking result
    LaunchedEffect(appointmentBookingResult) {
        appointmentBookingResult?.let { result ->
            if (result.appointmentId > 0) {
                // Show success message and navigate back or refresh
                // You can show a snackbar or dialog here
                onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        "Doctor Profile",
                        fontFamily = Inter,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = AppColors.Blue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            doctor?.let { doc ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // Doctor Header with gradient background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    ) {
                        // Gradient background
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            AppColors.Blue,
                                            Color(0xFF90CAF9)
                                        )
                                    )
                                )
                        )

                        // Profile content
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Profile image with border
                            Surface(
                                modifier = Modifier
                                    .size(120.dp)
                                    .padding(4.dp),
                                shape = CircleShape,
                                color = Color.White,
                                shadowElevation = 4.dp
                            ) {
                                AsyncImage(
                                    model = BASE_URL + doc.photo_url,
                                    contentDescription = "Doctor profile photo",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Name with verification badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 4.dp)
                            ) {
                                Text(
                                    text = "Dr. ${doc.first_name} ${doc.last_name}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = Inter
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Surface(
                                    shape = CircleShape,
                                    color = AppColors.Blue,
                                    contentColor = Color.White,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Verified",
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }

                            // Specialty
                            Text(
                                text = doc.specialty,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.9f),
                                fontFamily = Inter
                            )
                        }
                    }

                    // Contact actions
                    ContactActionsRow(
                        onCallClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${doc.phone_number}")
                            }
                            context.startActivity(intent)
                        },
                        onEmailClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:${doc.email}")
                            }
                            context.startActivity(intent)
                        },
                        onSocialClick = {
                            showSocialMedia = !showSocialMedia
                        }
                    )

                    // Social media links
                    AnimatedVisibility(
                        visible = showSocialMedia,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        SocialMediaLinks(
                            facebookLink = doc.facebook_link,
                            instagramLink = doc.instagram_link,
                            twitterLink = doc.twitter_link,
                            linkedinLink = doc.linkedin_link,
                            context = context
                        )
                    }

                    // Clinic information card
                    doc.clinic?.let { clinic ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Clinic Information",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = Inter,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Business,
                                        contentDescription = null,
                                        tint = AppColors.Blue,
                                        modifier = Modifier.size(24.dp)
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = clinic.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontFamily = Inter
                                    )
                                }

                                Divider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    thickness = 1.dp,
                                    color = Color(0xFFEEEEEE)
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val mapQuery = "${clinic.address}, ${clinic.location}"
                                            val intent = Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("geo:0,0?q=${Uri.encode(mapQuery)}")
                                            )
                                            intent.setPackage("com.google.android.apps.maps")
                                            context.startActivity(intent)
                                        }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = AppColors.Blue,
                                        modifier = Modifier.size(24.dp)
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = clinic.address,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = AppColors.Blue,
                                            fontFamily = Inter
                                        )

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = clinic.location,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = AppColors.Blue,
                                            fontFamily = Inter
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Available appointment slots
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Available Appointments",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Inter,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Group timeslots by date
                            val timeslotsByDate = doc.timeslots
                                .filter { !it.is_booked } // Only show available slots
                                .groupBy { it.date }

                            // Show dates with available slots
                            if (timeslotsByDate.isNotEmpty()) {
                                // Display dates as tabs or buttons
                                val selectedDateState = remember { mutableStateOf(timeslotsByDate.keys.first()) }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    timeslotsByDate.keys.forEach { date ->
                                        val isSelected = date == selectedDateState.value
                                        val displayDate = formatDateForDisplay(date)

                                        if (isSelected) {
                                            Button(
                                                onClick = {
                                                    selectedDateState.value = date
                                                    selectedDate = date
                                                    selectedTime = null // Reset time selection when date changes
                                                    selectedTimeslotId = null
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = AppColors.Blue
                                                ),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text(
                                                    displayDate,
                                                    fontFamily = Inter
                                                )
                                            }
                                        } else {
                                            OutlinedButton(
                                                onClick = {
                                                    selectedDateState.value = date
                                                    selectedDate = date
                                                    selectedTime = null // Reset time selection when date changes
                                                    selectedTimeslotId = null
                                                },
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(1.dp, AppColors.Blue),
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    contentColor = AppColors.Blue
                                                )
                                            ) {
                                                Text(
                                                    displayDate,
                                                    fontFamily = Inter
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Display timeslots for selected date
                                timeslotsByDate[selectedDateState.value]?.let { slots ->
                                    // Group by morning/afternoon
                                    val morningSlots = slots.filter { slot ->
                                        val time = LocalTime.parse(slot.start_time)
                                        time.isBefore(LocalTime.NOON)
                                    }

                                    val afternoonSlots = slots.filter { slot ->
                                        val time = LocalTime.parse(slot.start_time)
                                        !time.isBefore(LocalTime.NOON)
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        if (morningSlots.isNotEmpty()) {
                                            Text(
                                                text = "Morning",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = Color.Gray,
                                                fontFamily = Inter
                                            )

                                            TimeSlotRow(
                                                slots = morningSlots,
                                                selectedTime = selectedTime,
                                                onSlotSelected = { slot ->
                                                    selectedTime = slot.start_time
                                                    selectedTimeslotId = slot.id
                                                }
                                            )
                                        }

                                        if (afternoonSlots.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(8.dp))

                                            Text(
                                                text = "Afternoon",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = Color.Gray,
                                                fontFamily = Inter
                                            )

                                            TimeSlotRow(
                                                slots = afternoonSlots,
                                                selectedTime = selectedTime,
                                                onSlotSelected = { slot ->
                                                    selectedTime = slot.start_time
                                                    selectedTimeslotId = slot.id
                                                }
                                            )
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = "No available timeslots",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    fontFamily = Inter
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Modified Book Appointment Button
                            Button(
                                onClick = {
                                    if (selectedDate != null && selectedTime != null) {
                                        appointmentViewModel.bookAppointment(
                                            patientId = patientId,
                                            doctorId = doctorId,
                                            date = selectedDate!!,
                                            time = selectedTime!!
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = selectedDate != null && selectedTime != null && !isLoading,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AppColors.Blue
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (isLoading) {
                                    androidx.compose.material3.CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Booking...",
                                        fontFamily = Inter
                                    )
                                } else {
                                    Text(
                                        "Book Appointment",
                                        fontFamily = Inter
                                    )
                                }
                            }

                            // Show error if any
                            error?.let { errorMessage ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = errorMessage,
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = Inter
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            } ?: Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator(
                    color = AppColors.Blue
                )
            }
        }
    }
}

@Composable
fun ContactActionsRow(
    onCallClick: () -> Unit,
    onEmailClick: () -> Unit,
    onSocialClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .offset(y = (-20).dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ContactActionButton(
                icon = Icons.Default.Phone,
                text = "Call",
                onClick = onCallClick
            )

            ContactActionButton(
                icon = Icons.Default.Email,
                text = "Email",
                onClick = onEmailClick
            )

            ContactActionButton(
                icon = Icons.Default.Public,
                text = "Social",
                onClick = onSocialClick
            )
        }
    }
}

@Composable
fun ContactActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = AppColors.Blue,
            contentColor = Color.White
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            fontFamily = Inter
        )
    }
}

@Composable
fun SocialMediaLinks(
    facebookLink: String?,
    instagramLink: String?,
    twitterLink: String?,
    linkedinLink: String?,
    context: android.content.Context
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            facebookLink?.let {
                SocialMediaIcon(
                    iconRes = R.drawable.facebook,
                    contentDescription = "Facebook",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                        context.startActivity(intent)
                    }
                )
            }

            instagramLink?.let {
                SocialMediaIcon(
                    iconRes = R.drawable.instagram,
                    contentDescription = "Instagram",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                        context.startActivity(intent)
                    }
                )
            }

            twitterLink?.let {
                SocialMediaIcon(
                    iconRes = R.drawable.twitter,
                    contentDescription = "Twitter",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                        context.startActivity(intent)
                    }
                )
            }

            linkedinLink?.let {
                SocialMediaIcon(
                    iconRes = R.drawable.linkedin,
                    contentDescription = "LinkedIn",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun SocialMediaIcon(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp)
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun formatDateForDisplay(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString)
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)

        when (date) {
            today -> "Today"
            tomorrow -> "Tomorrow"
            else -> {
                val formatter = DateTimeFormatter.ofPattern("MMM d")
                date.format(formatter)
            }
        }
    } catch (e: Exception) {
        dateString // Fallback to raw string if parsing fails
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatTimeForDisplay(timeString: String): String {
    return try {
        val time = LocalTime.parse(timeString)
        val formatter = DateTimeFormatter.ofPattern("h:mm a")
        time.format(formatter)
    } catch (e: Exception) {
        timeString // Fallback to raw string if parsing fails
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeSlotRow(
    slots: List<Timeslot>,
    selectedTime: String? = null,
    onSlotSelected: (Timeslot) -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        slots.forEach { slot ->
            val timeDisplay = formatTimeForDisplay(slot.start_time)
            val isSelected = slot.start_time == selectedTime

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected)
                    AppColors.Blue
                else
                    Color(0xFFE3F2FD),
                contentColor = if (isSelected)
                    Color.White
                else
                    AppColors.Blue
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            onSlotSelected(slot)
                        }
                ) {
                    Text(
                        text = timeDisplay,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = Inter
                    )
                }
            }
        }
    }
}