//package com.example.doccur.ui.screens.patient
//
//import android.content.Intent
//import android.net.Uri
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.expandVertically
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.compose.animation.shrinkVertically
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material.icons.rounded.*
//import androidx.compose.material3.*
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedButton
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import coil.compose.AsyncImage
//import com.example.doccur.R
//import com.example.doccur.api.BASE_URL
//import com.example.doccur.viewmodels.DoctorViewModel
//import java.text.SimpleDateFormat
//import java.util.Calendar
//import java.util.Date
//import java.util.Locale
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DoctorDetailsScreen(viewModel: DoctorViewModel, doctorId: Int, onNavigateBack: () -> Unit = {}) {
//    val doctor by viewModel.selectedDoctor.collectAsState()
//    val context = LocalContext.current
//    var showSocialMedia by remember { mutableStateOf(false) }
//    val scrollState = rememberScrollState()
//    val calendar = Calendar.getInstance()
//    val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
//
//    // Get dates for appointment buttons
//    val dates = List(3) { index ->
//        val newDate = Calendar.getInstance()
//        newDate.add(Calendar.DAY_OF_YEAR, index)
//        newDate.time
//    }
//
//    LaunchedEffect(doctorId) {
//        viewModel.loadDoctorDetails(doctorId)
//    }
//
//    Scaffold(
//        topBar = {
//            SmallTopAppBar(
//                title = { Text("Doctor Profile") },
//                navigationIcon = {
//                    IconButton(onClick = onNavigateBack) {
//                        Icon(
//                            imageVector = Icons.Default.ArrowBack,
//                            contentDescription = "Back"
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.smallTopAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
//                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
//                )
//            )
//        }
//    ) { innerPadding ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//        ) {
//            doctor?.let { doc ->
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .verticalScroll(scrollState)
//                ) {
//                    // Doctor Header with gradient background
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(220.dp)
//                    ) {
//                        // Gradient background
//                        Box(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .background(
//                                    brush = Brush.verticalGradient(
//                                        colors = listOf(
//                                            MaterialTheme.colorScheme.primary,
//                                            MaterialTheme.colorScheme.primaryContainer
//                                        )
//                                    )
//                                )
//                        )
//
//                        // Profile content
//                        Column(
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(16.dp)
//                        ) {
//                            // Profile image with border
//                            Surface(
//                                modifier = Modifier
//                                    .size(120.dp)
//                                    .padding(4.dp),
//                                shape = CircleShape,
//                                color = MaterialTheme.colorScheme.surface,
//                                shadowElevation = 4.dp
//                            ) {
//                                AsyncImage(
//                                    model = BASE_URL + doc.photo_url,
//                                    contentDescription = "Doctor profile photo",
//                                    modifier = Modifier
//                                        .fillMaxSize()
//                                        .clip(CircleShape),
//                                    contentScale = ContentScale.Crop
//                                )
//                            }
//
//                            Spacer(modifier = Modifier.height(12.dp))
//
//                            // Name with verification badge
//                            Row(
//                                verticalAlignment = Alignment.CenterVertically,
//                                modifier = Modifier.padding(bottom = 4.dp)
//                            ) {
//                                Text(
//                                    text = "Dr. ${doc.first_name} ${doc.last_name}",
//                                    style = MaterialTheme.typography.headlineSmall,
//                                    color = MaterialTheme.colorScheme.onPrimary,
//                                    fontWeight = FontWeight.Bold
//                                )
//
//                                Spacer(modifier = Modifier.width(8.dp))
//
//                                Surface(
//                                    shape = CircleShape,
//                                    color = MaterialTheme.colorScheme.primaryContainer,
//                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
//                                    modifier = Modifier.size(24.dp)
//                                ) {
//                                    Icon(
//                                        Icons.Default.Check,
//                                        contentDescription = "Verified",
//                                        modifier = Modifier.padding(4.dp)
//                                    )
//                                }
//                            }
//
//                            // Specialty
//                            Text(
//                                text = doc.specialty,
//                                style = MaterialTheme.typography.bodyLarge,
//                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
//                            )
//                        }
//                    }
//
//                    // Contact actions
//                    ContactActionsRow(
//                        onCallClick = {
//                            val intent = Intent(Intent.ACTION_DIAL).apply {
//                                data = Uri.parse("tel:${doc.phone_number}")
//                            }
//                            context.startActivity(intent)
//                        },
//                        onEmailClick = {
//                            val intent = Intent(Intent.ACTION_SENDTO).apply {
//                                data = Uri.parse("mailto:${doc.email}")
//                            }
//                            context.startActivity(intent)
//                        },
//                        onSocialClick = {
//                            showSocialMedia = !showSocialMedia
//                        }
//                    )
//
//                    // Social media links
//                    AnimatedVisibility(
//                        visible = showSocialMedia,
//                        enter = fadeIn() + expandVertically(),
//                        exit = fadeOut() + shrinkVertically()
//                    ) {
//                        SocialMediaLinks(
//                            facebookLink = doc.facebook_link,
//                            instagramLink = doc.instagram_link,
//                            twitterLink = doc.twitter_link,
//                            linkedinLink = doc.linkedin_link,
//                            context = context
//                        )
//                    }
//
//                    // Clinic information card
//                    doc.clinic?.let { clinic ->
//                        Card(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(16.dp),
//                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//                            shape = RoundedCornerShape(16.dp),
//                            colors = CardDefaults.cardColors(
//                                containerColor = MaterialTheme.colorScheme.surface
//                            )
//                        ) {
//                            Column(
//                                modifier = Modifier.padding(16.dp)
//                            ) {
//                                Text(
//                                    text = "Clinic Information",
//                                    style = MaterialTheme.typography.titleMedium,
//                                    fontWeight = FontWeight.Bold,
//                                    modifier = Modifier.padding(bottom = 12.dp)
//                                )
//
//                                Row(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(vertical = 8.dp),
//                                    verticalAlignment = Alignment.CenterVertically
//                                ) {
//                                    Icon(
//                                        Icons.Default.Business,
//                                        contentDescription = null,
//                                        tint = MaterialTheme.colorScheme.primary,
//                                        modifier = Modifier.size(24.dp)
//                                    )
//
//                                    Spacer(modifier = Modifier.width(12.dp))
//
//                                    Text(
//                                        text = clinic.name,
//                                        style = MaterialTheme.typography.bodyLarge
//                                    )
//                                }
//
//                                Divider(
//                                    modifier = Modifier.padding(vertical = 8.dp),
//                                    thickness = 1.dp,
//                                    color = MaterialTheme.colorScheme.outlineVariant
//                                )
//
//                                Row(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .clickable {
//                                            val mapQuery = "${clinic.address}, ${clinic.location}"
//                                            val intent = Intent(
//                                                Intent.ACTION_VIEW,
//                                                Uri.parse("geo:0,0?q=${Uri.encode(mapQuery)}")
//                                            )
//                                            intent.setPackage("com.google.android.apps.maps")
//                                            context.startActivity(intent)
//                                        }
//                                        .padding(vertical = 8.dp),
//                                    verticalAlignment = Alignment.Top
//                                ) {
//                                    Icon(
//                                        Icons.Default.LocationOn,
//                                        contentDescription = null,
//                                        tint = MaterialTheme.colorScheme.primary,
//                                        modifier = Modifier.size(24.dp)
//                                    )
//
//                                    Spacer(modifier = Modifier.width(12.dp))
//
//                                    Column {
//                                        Text(
//                                            text = clinic.address,
//                                            style = MaterialTheme.typography.bodyLarge,
//                                            color = MaterialTheme.colorScheme.primary
//                                        )
//
//                                        Spacer(modifier = Modifier.height(2.dp))
//
//                                        Text(
//                                            text = clinic.location,
//                                            style = MaterialTheme.typography.bodyMedium,
//                                            color = MaterialTheme.colorScheme.primary
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    // Available appointment slots
//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp),
//                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//                        shape = RoundedCornerShape(16.dp),
//                        colors = CardDefaults.cardColors(
//                            containerColor = MaterialTheme.colorScheme.surface
//                        )
//                    ) {
//                        Column(
//                            modifier = Modifier.padding(16.dp)
//                        ) {
//                            Text(
//                                text = "Available Appointments",
//                                style = MaterialTheme.typography.titleMedium,
//                                fontWeight = FontWeight.Bold,
//                                modifier = Modifier.padding(bottom = 16.dp)
//                            )
//
//                            Row(
//                                modifier = Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.spacedBy(8.dp)
//                            ) {
//                                dates.forEachIndexed { index, date ->
//                                    val isSelected = index == 0
//                                    val dateText = when (index) {
//                                        0 -> "Today"
//                                        1 -> "Tomorrow"
//                                        else -> dateFormat.format(date)
//                                    }
//
//                                    if (isSelected) {
//                                        Button(
//                                            onClick = { /* Book this slot */ },
//                                            modifier = Modifier.weight(1f),
//                                            colors = ButtonDefaults.buttonColors(
//                                                containerColor = MaterialTheme.colorScheme.primary
//                                            ),
//                                            shape = RoundedCornerShape(12.dp)
//                                        ) {
//                                            Text(dateText)
//                                        }
//                                    } else {
//                                        OutlinedButton(
//                                            onClick = { /* Select this slot */ },
//                                            modifier = Modifier.weight(1f),
//                                            shape = RoundedCornerShape(12.dp),
//                                            border = ButtonDefaults.outlinedButtonBorder
//                                        ) {
//                                            Text(dateText)
//                                        }
//                                    }
//                                }
//                            }
//
//                            Spacer(modifier = Modifier.height(16.dp))
//
//                            // Time slots
//                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                                // Morning slots
//                                Text(
//                                    text = "Morning",
//                                    style = MaterialTheme.typography.labelLarge,
//                                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                                )
//
//                                TimeSlotRow(
//                                    slots = listOf("9:00 AM", "10:00 AM", "11:30 AM")
//                                )
//
//                                Spacer(modifier = Modifier.height(8.dp))
//
//                                // Afternoon slots
//                                Text(
//                                    text = "Afternoon",
//                                    style = MaterialTheme.typography.labelLarge,
//                                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                                )
//
//                                TimeSlotRow(
//                                    slots = listOf("1:30 PM", "2:45 PM", "4:15 PM")
//                                )
//                            }
//
//                            Spacer(modifier = Modifier.height(16.dp))
//
//                            Button(
//                                onClick = { /* Book appointment */ },
//                                modifier = Modifier.fillMaxWidth(),
//                                colors = ButtonDefaults.buttonColors(
//                                    containerColor = MaterialTheme.colorScheme.primary
//                                ),
//                                shape = RoundedCornerShape(12.dp)
//                            ) {
//                                Text(
//                                    "Book Appointment",
//                                    modifier = Modifier.padding(vertical = 4.dp)
//                                )
//                            }
//                        }
//                    }
//
//                    Spacer(modifier = Modifier.height(16.dp))
//                }
//            } ?: Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                androidx.compose.material3.CircularProgressIndicator(
//                    color = MaterialTheme.colorScheme.primary
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun ContactActionsRow(
//    onCallClick: () -> Unit,
//    onEmailClick: () -> Unit,
//    onSocialClick: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp)
//            .offset(y = (-20).dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surface
//        )
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            ContactActionButton(
//                icon = Icons.Default.Phone,
//                text = "Call",
//                onClick = onCallClick
//            )
//
//            ContactActionButton(
//                icon = Icons.Default.Email,
//                text = "Email",
//                onClick = onEmailClick
//            )
//
//            ContactActionButton(
//                icon = Icons.Default.Public,
//                text = "Social",
//                onClick = onSocialClick
//            )
//        }
//    }
//}
//
//@Composable
//fun ContactActionButton(
//    icon: ImageVector,
//    text: String,
//    onClick: () -> Unit
//) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier
//            .clickable(onClick = onClick)
//            .padding(vertical = 12.dp, horizontal = 16.dp)
//    ) {
//        Surface(
//            modifier = Modifier.size(48.dp),
//            shape = CircleShape,
//            color = MaterialTheme.colorScheme.primaryContainer,
//            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
//        ) {
//            Box(
//                contentAlignment = Alignment.Center,
//                modifier = Modifier.fillMaxSize()
//            ) {
//                Icon(
//                    imageVector = icon,
//                    contentDescription = text,
//                    modifier = Modifier.size(24.dp)
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Text(
//            text = text,
//            style = MaterialTheme.typography.bodyMedium,
//            color = MaterialTheme.colorScheme.onSurface
//        )
//    }
//}
//
//@Composable
//fun SocialMediaLinks(
//    facebookLink: String?,
//    instagramLink: String?,
//    twitterLink: String?,
//    linkedinLink: String?,
//    context: android.content.Context
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 8.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surfaceVariant
//        )
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            facebookLink?.let {
//                SocialMediaIcon(
//                    iconRes = R.drawable.facebook,
//                    contentDescription = "Facebook",
//                    onClick = {
//                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
//                        context.startActivity(intent)
//                    }
//                )
//            }
//
//            instagramLink?.let {
//                SocialMediaIcon(
//                    iconRes = R.drawable.instagram,
//                    contentDescription = "Instagram",
//                    onClick = {
//                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
//                        context.startActivity(intent)
//                    }
//                )
//            }
//
//            twitterLink?.let {
//                SocialMediaIcon(
//                    iconRes = R.drawable.twitter,
//                    contentDescription = "Twitter",
//                    onClick = {
//                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
//                        context.startActivity(intent)
//                    }
//                )
//            }
//
//            linkedinLink?.let {
//                SocialMediaIcon(
//                    iconRes = R.drawable.linkedin,
//                    contentDescription = "LinkedIn",
//                    onClick = {
//                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
//                        context.startActivity(intent)
//                    }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun SocialMediaIcon(
//    iconRes: Int,
//    contentDescription: String,
//    onClick: () -> Unit
//) {
//    Box(
//        modifier = Modifier
//            .size(40.dp)
//            .clip(CircleShape)
//            .background(MaterialTheme.colorScheme.surface)
//            .clickable(onClick = onClick),
//        contentAlignment = Alignment.Center
//    ) {
//        androidx.compose.foundation.Image(
//            painter = painterResource(iconRes),
//            contentDescription = contentDescription,
//            modifier = Modifier.size(24.dp)
//        )
//    }
//}
//
//@Composable
//fun TimeSlotRow(slots: List<String>) {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        slots.forEachIndexed { index, time ->
//            val isAvailable = index != 1 // For demonstration - second slot is unavailable
//
//            Surface(
//                modifier = Modifier
//                    .weight(1f)
//                    .height(40.dp),
//                shape = RoundedCornerShape(8.dp),
//                color = if (isAvailable)
//                    MaterialTheme.colorScheme.surfaceVariant
//                else
//                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
//                contentColor = if (isAvailable)
//                    MaterialTheme.colorScheme.onSurfaceVariant
//                else
//                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
//            ) {
//                Box(
//                    contentAlignment = Alignment.Center,
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .then(
//                            if (isAvailable) {
//                                Modifier.clickable { /* Select this time slot */ }
//                            } else {
//                                Modifier
//                            }
//                        )
//                ) {
//                    Text(
//                        text = time,
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                }
//            }
//        }
//    }
//}