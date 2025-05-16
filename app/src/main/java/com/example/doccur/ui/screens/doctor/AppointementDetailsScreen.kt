package com.example.doccur.ui.screens.doctor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.doccur.navigation.DoctorScreen
import com.example.doccur.ui.theme.AppColors
import com.example.doccur.ui.theme.Inter
import com.example.doccur.viewmodels.AppointmentViewModel

@Composable
fun AppointmentDetailsScreen(
    appointmentId: Int,
    viewModel: AppointmentViewModel,
    navController: NavController
) {
    val details by viewModel.appointmentDetails.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()


    LaunchedEffect(appointmentId) {
        viewModel.fetchAppointmentDetails(appointmentId)
    }

    val confirmationMessage by viewModel.confirmationMessage.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(confirmationMessage) {
        if (confirmationMessage != null) {
            showDialog = true
        }
    }

    var showRejectDialog by remember { mutableStateOf(false) }
    var rejectReason by remember { mutableStateOf("") }


    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center, ) {
        when {
            loading -> CircularProgressIndicator()

            error != null -> Text("Error: $error", color = MaterialTheme.colorScheme.error)

            details != null -> {
                val data = details!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header with title and close button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Appointment Details",
                            style = androidx.compose.material.MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        when (data.status.lowercase()) {
                            "confirmed" -> {
                                Row(){
                                    Text(
                                        "confirmed",
                                        color = AppColors.Green,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = Inter
                                    )
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = AppColors.Green,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            "rejected" -> {
                                Row(){
                                    Text(
                                        "Rejected",
                                        color = AppColors.Red,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = Inter
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Cancel,
                                        contentDescription = null,
                                        tint = AppColors.Red,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            "completed" -> {
                                Row(){
                                    Text(
                                        "Completed",
                                        color = AppColors.Green,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = Inter
                                    )
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = AppColors.Green,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Patient details
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            data.patient.full_name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Text(
                            "Female, ${data.patient.date_of_birth}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))


                    // Patient details list items
                    DetailItemWithIcon(
                        icon = Icons.Default.DateRange,
                        iconTint = Color(0xFF4285F4),
                        text = data.date,
                        textColor = Color(0xFF4285F4)
                    )

                    DetailItemWithIcon(
                        icon = Icons.Default.AccessTime,
                        iconTint = Color(0xFF4285F4),
                        text = data.time,
                        textColor = Color(0xFF4285F4)
                    )

                    DetailItemWithIcon(
                        icon = Icons.Default.Phone,
                        text = data.patient.phone_number,
                        textColor = Color.Black
                    )

                    DetailItemWithIcon(
                        icon = Icons.Default.Email,
                        text = data.patient.email,
                        textColor = Color.Black
                    )


                    Spacer(modifier = Modifier.height(8.dp))

                    when (data.status.lowercase()) {
                        "pending" -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        showRejectDialog = true
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFFE3B46)),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = Color.White,
                                        contentColor = Color(0xFFFE3B46)
                                    )
                                ) {
                                    Text("Reject", fontWeight = FontWeight.Bold)
                                }


                                // Confirm button
                                Button(
                                    onClick = {
                                        viewModel.confirmAppointment(appointmentId)
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4285F4)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "Confirm",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        "confirmed" -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 2.dp)
                            ) {
                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        // TODO: Implement check-in logic
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4285F4)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Check-In", fontWeight = FontWeight.Bold,color = Color.White )
                                }
                            }
                        }

                        else -> {
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    if (showDialog && confirmationMessage != null) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                viewModel.clearConfirmationMessage()
                viewModel.fetchAppointmentDetails(appointmentId) // Refresh
            },


            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    viewModel.clearConfirmationMessage()
                    viewModel.fetchAppointmentDetails(appointmentId) // Refresh
                }) {
                    Text("OK", color = Color.Blue)
                }
            },
            title = {
                Text("Success", color = Color.Black, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(confirmationMessage!!, color = Color.Black)
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(8.dp)
        )
    }

    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = {
                showRejectDialog = false
            },
            title = {
                Text(
                    text = "Reject Appointment",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Please provide a reason for rejection:",
                        color = Color.DarkGray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        placeholder = { Text("Enter reason", color = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4285F4),
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = Color(0xFF4285F4),
                            unfocusedLabelColor = Color.Gray,
                            cursorColor = Color.Black,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        singleLine = false,
                        maxLines = 4
                    )

                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRejectDialog = false
                        viewModel.rejectAppointment(appointmentId, rejectReason)
                        rejectReason = ""
                    },
                    modifier = Modifier.padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4285F4)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Submit", fontWeight = FontWeight.Medium,color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showRejectDialog = false
                    }
                ) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(12.dp)
        )
    }


}

@Composable
fun DetailItemWithIcon(
    icon: ImageVector,
    text: String,
    iconTint: Color = Color.Gray,
    textColor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = iconTint
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
    }
}