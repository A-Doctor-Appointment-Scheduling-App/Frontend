package com.example.doccur.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.doccur.model.Prescription
import com.example.doccur.ui.components.ErrorMessage
import com.example.doccur.ui.components.LoadingIndicator
import com.example.doccur.ui.theme.Blue
import com.example.doccur.ui.theme.CardShape
import com.example.doccur.ui.theme.LightGray
import com.example.doccur.util.Resource
import com.example.doccur.viewmodel.PrescriptionViewModel

@Composable
fun PrescriptionDetailScreen(
    viewModel: PrescriptionViewModel,
    prescriptionId: Int,
    onBackClick: () -> Unit,
    onDownloadClick: (Int) -> Unit
) {
    val prescriptionState by viewModel.prescriptionState.collectAsState()
    val scrollState = rememberScrollState()
    
    LaunchedEffect(prescriptionId) {
        viewModel.getPrescription(prescriptionId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prescription Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onDownloadClick(prescriptionId) }) {
                        Icon(Icons.Default.Download, contentDescription = "Download PDF")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (prescriptionState) {
                is Resource.Loading -> {
                    LoadingIndicator()
                }
                
                is Resource.Error -> {
                    ErrorMessage(
                        message = (prescriptionState as Resource.Error).message,
                        onRetry = { viewModel.getPrescription(prescriptionId) }
                    )
                }
                
                is Resource.Success -> {
                    val prescription = (prescriptionState as Resource.Success<Prescription>).data
                    PrescriptionContent(prescription = prescription, scrollState = scrollState)
                }
            }
        }
    }
}

@Composable
fun PrescriptionContent(prescription: Prescription, scrollState: androidx.compose.foundation.ScrollState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Prescription header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = CardShape,
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Prescription #${prescription.id}",
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                    color = Blue
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Date: ${prescription.issued_date}",
                    style = MaterialTheme.typography.body2
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Appointment ID: ${prescription.appointment}",
                    style = MaterialTheme.typography.body2
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Medications
        Text(
            text = "Medications",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (prescription.medications.isEmpty()) {
            Text(
                text = "No medications prescribed",
                style = MaterialTheme.typography.body1,
                fontStyle = FontStyle.Italic
            )
        } else {
            prescription.medications.forEachIndexed { index, medication ->
                MedicationItem(
                    index = index + 1,
                    name = medication.name,
                    dosage = medication.dosage,
                    frequency = medication.frequency,
                    instructions = medication.instructions
                )
                
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun MedicationItem(
    index: Int,
    name: String,
    dosage: String,
    frequency: String,
    instructions: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp,
        shape = CardShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Blue, shape = RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = index.toString(),
                        color = Color.White,
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = name,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Divider()
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Dosage",
                        style = MaterialTheme.typography.caption,
                        color = Color.Gray
                    )
                    
                    Text(
                        text = dosage,
                        style = MaterialTheme.typography.body2
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Frequency",
                        style = MaterialTheme.typography.caption,
                        color = Color.Gray
                    )
                    
                    Text(
                        text = frequency,
                        style = MaterialTheme.typography.body2
                    )
                }
            }
            
            if (!instructions.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Instructions",
                    style = MaterialTheme.typography.caption,
                    color = Color.Gray
                )
                
                Text(
                    text = instructions,
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}