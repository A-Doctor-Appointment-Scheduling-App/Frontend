package com.example.doccur.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.doccur.model.Prescription
import com.example.doccur.ui.components.ErrorMessage
import com.example.doccur.ui.components.LoadingIndicator
import com.example.doccur.ui.theme.CardShape
import com.example.doccur.util.Resource
import com.example.doccur.util.TokenManager
import com.example.doccur.viewmodel.PrescriptionViewModel

@Composable
fun PrescriptionListScreen(
    viewModel: PrescriptionViewModel,
    tokenManager: TokenManager,
    onPrescriptionClick: (Int) -> Unit,
    onCreatePrescriptionClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val prescriptionsListState by viewModel.prescriptionsListState.collectAsState()
    val userId = tokenManager.getUserId()
    val isDoctor = tokenManager.isDoctor()
    
    // In a real app, we would load the actual doctor/patient ID
    // For now, using a placeholder for the API call
    val doctorId = if (isDoctor) userId else 6
    val patientId = if (!isDoctor) userId else 1
    
    LaunchedEffect(doctorId, patientId) {
        viewModel.getPrescriptionsByDoctorAndPatient(doctorId, patientId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prescriptions") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (isDoctor) {
                FloatingActionButton(
                    onClick = onCreatePrescriptionClick
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Prescription")
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (prescriptionsListState) {
                is Resource.Loading -> {
                    LoadingIndicator()
                }
                
                is Resource.Error -> {
                    ErrorMessage(
                        message = (prescriptionsListState as Resource.Error).message,
                        onRetry = { viewModel.getPrescriptionsByDoctorAndPatient(doctorId, patientId) }
                    )
                }
                
                is Resource.Success -> {
                    val prescriptions = (prescriptionsListState as Resource.Success<List<Prescription>>).data
                    if (prescriptions.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No prescriptions found",
                                style = MaterialTheme.typography.h6
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            items(prescriptions) { prescription ->
                                PrescriptionListItem(
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

@Composable
fun PrescriptionListItem(
    prescription: Prescription,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = CardShape,
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Prescription #${prescription.id}",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Date: ${prescription.issued_date}",
                style = MaterialTheme.typography.body2
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Divider()
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Medications: ${prescription.medications.size}",
                style = MaterialTheme.typography.body2
            )
            
            if (prescription.medications.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                
                prescription.medications.take(2).forEach { medication ->
                    Text(
                        text = "• ${medication.name} - ${medication.dosage}",
                        style = MaterialTheme.typography.caption,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                if (prescription.medications.size > 2) {
                    Text(
                        text = "• and ${prescription.medications.size - 2} more...",
                        style = MaterialTheme.typography.caption,
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }
    }
}