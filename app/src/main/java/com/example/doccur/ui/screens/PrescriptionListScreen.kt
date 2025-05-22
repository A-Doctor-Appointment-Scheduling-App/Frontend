package com.example.doccur.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.FileCopy
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doccur.R
import com.example.doccur.model.Prescription
import com.example.doccur.model.Patient
import com.example.doccur.ui.components.LoadingIndicator // Keep your original LoadingIndicator
import com.example.doccur.util.Resource
import com.example.doccur.util.TokenManager
import com.example.doccur.viewmodel.PrescriptionViewModel
import com.example.doccur.viewmodel.AuthViewModel

val ModernBluePrimary = Color(0xFF007BFF)
val ModernBlueSecondary = Color(0xFF58AFFF)
val ModernTextPrimary = Color(0xFF1A202C)
val ModernTextSecondary = Color(0xFF4A5568)
val ModernBackground = Color(0xFFF8F9FA)
val ModernCardBackground = Color.White
val ModernErrorColor = Color(0xFFE53E3E)


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PrescriptionListScreen(
    viewModel: PrescriptionViewModel,
    authViewModel: AuthViewModel,
    tokenManager: TokenManager,
    onPrescriptionClick: (Int) -> Unit,
    onCreatePrescriptionClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val prescriptionsListState by viewModel.prescriptionsListState.collectAsState()
    val patientState by authViewModel.patientState.collectAsState()
    val userId = tokenManager.getUserId()
    val isDoctor = tokenManager.isDoctor()

    val doctorId = if (isDoctor) userId else 6
    val patientId = if (!isDoctor) userId else 2

    LaunchedEffect(doctorId, patientId) {
        viewModel.getPrescriptionsByDoctorAndPatient(doctorId, patientId)
        authViewModel.getPatient(patientId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Patient Prescriptions",
                        fontWeight = FontWeight.SemiBold,
                        color = ModernTextPrimary,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = ModernBluePrimary
                        )
                    }
                },
                backgroundColor = ModernBackground,
                elevation = 0.dp
            )

        },
        floatingActionButton = {
            if (isDoctor) {
                FloatingActionButton(
                    onClick = onCreatePrescriptionClick,
                    backgroundColor = ModernBluePrimary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Outlined.EditNote, contentDescription = "Create Prescription")
                }
            }
        },
        backgroundColor = ModernBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedProfileSection(patientState)

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedContent(
                targetState = prescriptionsListState,
                transitionSpec = {
                    if (targetState is Resource.Success && initialState is Resource.Loading) {
                        slideInVertically { height -> height } + fadeIn() with
                                slideOutVertically { height -> -height } + fadeOut()
                    } else {
                        fadeIn(animationSpec = spring()) with fadeOut(animationSpec = spring())
                    }.using(SizeTransform(clip = false))
                }
            ) { state ->
                when (state) {
                    is Resource.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { LoadingIndicator() }
                    is Resource.Error -> AmazingErrorMessage(
                        message = state.message,
                        onRetry = { viewModel.getPrescriptionsByDoctorAndPatient(doctorId, patientId) }
                    )
                    is Resource.Success -> {
                        val prescriptions = state.data
                        if (prescriptions.isEmpty()) {
                            EmptyPrescriptionsView()
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                contentPadding = PaddingValues(bottom = 72.dp)
                            ) {
                                items(prescriptions, key = { it.id }) { prescription ->
                                    AnimatedPrescriptionListItem(
                                        prescription = prescription,
                                        onClick = { onPrescriptionClick(prescription.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedProfileSection(patientState: Resource<Patient>) {
    var expanded by remember { mutableStateOf(false) }
    val elevation by animateDpAsState(if (expanded) 8.dp else 2.dp)

    AnimatedContent(
        targetState = patientState,

    ) { state ->
        when (state) {
            is Resource.Loading -> Box(
                Modifier
                    .fillMaxWidth()
                    .height(150.dp), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ModernBluePrimary, strokeWidth = 2.dp)
            }
            is Resource.Error -> Box(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Could not load patient details.", color = ModernErrorColor, textAlign = TextAlign.Center)
            }
            is Resource.Success -> {
                val patient = state.data
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(ModernCardBackground)
                        .clickable { expanded = !expanded }
                        .padding(top = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_default_avatar), // Replace with actual avatar logic
                            contentDescription = "Patient Avatar",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .border(2.dp, ModernBlueSecondary, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${patient.first_name} ${patient.last_name}",
                                style = MaterialTheme.typography.h6.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ModernTextPrimary,
                                    fontSize = 20.sp
                                )
                            )
                            Text(
                                text = "Patient ID: P${patient.id.toString().padStart(4, '0')}",
                                style = MaterialTheme.typography.caption.copy(
                                    color = ModernTextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                        Icon(
                            imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                            contentDescription = if (expanded) "Collapse" else "Expand",
                            tint = ModernBluePrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    AnimatedVisibility(visible = expanded) {
                        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp)) {
                            Divider(color = ModernBluePrimary.copy(alpha = 0.1f), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(12.dp))
                            ProfileDetailItem(Icons.Outlined.MailOutline, "Email", patient.email)
                            ProfileDetailItem(Icons.Outlined.Phone, "Phone", patient.phone_number)
                            ProfileDetailItem(Icons.Outlined.LocationOn, "Address", patient.address)
                            ProfileDetailItem(Icons.Outlined.CalendarToday, "D.O.B", patient.date_of_birth)
                        }
                    }
                    if (!expanded) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileDetailItem(icon: ImageVector, label: String, value: String?) {
    value?.let {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = ModernBluePrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.overline.copy(
                        color = ModernTextSecondary,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    )
                )
                Text(
                    text = it,
                    style = MaterialTheme.typography.body2.copy(
                        color = ModernTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}


 val ChicBluePrimary = Color(0xFF007BFF)
 val ChicBlueSubtle = Color(0xFFE8EFFF)
 val ChicSurface = Color.White
 val ChicTextHeading = Color(0xFF1F2937)
 val ChicTextBody = Color(0xFF4B5563)
 val ChicTextMuted = Color(0xFF6B7280)
 val ChicBorder = Color(0xFFE5E7EB)

@Composable
fun AnimatedPrescriptionListItem(
    prescription: Prescription,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        backgroundColor = ChicSurface,
        border = BorderStroke(1.dp, ChicBorder.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp) // Increased spacing between cards
            .clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {


            // 2. Main Content Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 18.dp) // Generous padding
            ) {
                // -- Top Section: Title & Navigation Icon --
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "#${prescription.id.toString().padStart(3, '0')}",
                        style = MaterialTheme.typography.h6.copy( // h6 for good prominence
                            color = Color.Black, // Accent color for the title
                            fontWeight = FontWeight.SemiBold, // Strong but not overly heavy
                            fontSize = 15.sp // Slightly larger
                        )
                    )
                    Icon(
                        imageVector = Icons.Filled.ArrowForwardIos, // Clear navigation cue
                        contentDescription = "View details",
                        tint = ChicTextMuted.copy(alpha = 0.7f), // Subtle, non-distracting
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp)) // Reduced space after title

                // -- Date Information (Integrated, not a separate chip) --
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday, // Outlined for a lighter feel
                        contentDescription = "Issue date",
                        tint = ChicTextMuted,
                        modifier = Modifier.size(15.dp) // Slightly smaller icon
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = prescription.issued_date.substringBefore("T"),
                        style = MaterialTheme.typography.caption.copy( // Caption for metadata
                            color = ChicTextMuted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                // -- Subtle Divider --
                Divider(
                    color = ChicBorder.copy(alpha = 0.5f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 14.dp)
                )

                // -- Medications Section --
                Text(
                    text = "MEDICATIONS", // Clear, uppercase section title
                    style = MaterialTheme.typography.overline.copy( // Overline for chic section headers
                        color = ChicTextBody,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        letterSpacing = 0.8.sp // Added letter spacing
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (prescription.medications.isNotEmpty()) {
                    // Display up to 2 medications, more structured
                    prescription.medications.take(2).forEach { medication ->
                        MedicationItem(medication.name, medication.dosage) // Using the revamped MedicationItem
                        Spacer(modifier = Modifier.height(4.dp)) // Small space between meds
                    }
                    if (prescription.medications.size > 2) {
                        Text(
                            text = "+ ${prescription.medications.size - 2} more medications",
                            style = MaterialTheme.typography.caption.copy(
                                color = ChicBluePrimary, // Actionable blue
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            ),
                            modifier = Modifier.padding(start = 26.dp, top = 8.dp) // Indent under icons
                        )
                    }
                } else {
                    Text(
                        text = "No medications listed for this prescription.",
                        style = MaterialTheme.typography.body2.copy(
                            color = ChicTextMuted,
                            fontSize = 13.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        ),
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }
            }
        }
    }
}




// --- Revamped MedicationItem (More elegant and clear) ---
@Composable
fun MedicationItem(name: String, dosage: String) {
    Row(
        verticalAlignment = Alignment.Top, // Align to top for potentially multi-line names
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp) // A bit more vertical space
    ) {
        Icon(
            imageVector = Icons.Filled.Medication, // Using Filled for a bit more visual weight
            contentDescription = "Medication icon",
            tint = ChicBluePrimary.copy(alpha = 0.85f), // Slightly richer blue
            modifier = Modifier
                .size(20.dp) // Slightly larger icon
                .padding(top = 1.dp) // Fine-tune vertical alignment with text
        )
        Spacer(modifier = Modifier.width(12.dp)) // Increased space for better readability
        Column(modifier = Modifier.weight(1f)) { // Column for name and dosage
            Text(
                text = name,
                style = MaterialTheme.typography.subtitle2.copy( // Subtitle2 for medication name
                    color = ChicTextHeading, // Darker color for prominence
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold // Clearly readable
                ),
                maxLines = 2, // Allow for slightly longer names
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp)) // Small space between name and dosage
            Text(
                text = dosage,
                style = MaterialTheme.typography.body2.copy(
                    color = ChicTextMuted, // Muted color for secondary info
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun EmptyPrescriptionsView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No Prescriptions Yet",
            style = MaterialTheme.typography.h6.copy(
                color = ModernTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "When prescriptions are added, they will appear here.",
            style = MaterialTheme.typography.body1.copy(
                color = ModernTextSecondary,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
fun AmazingErrorMessage(message: String?, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = "Error",
            tint = ModernErrorColor,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Oops! Something went wrong.",
            style = MaterialTheme.typography.h6.copy(
                color = ModernTextPrimary,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message ?: "An unexpected error occurred. Please try again.",
            style = MaterialTheme.typography.body1.copy(color = ModernTextSecondary),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = ModernBluePrimary),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text("Retry", color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}