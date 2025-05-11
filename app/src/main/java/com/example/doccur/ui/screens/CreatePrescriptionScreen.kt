package com.example.doccur.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.doccur.model.Medication
import com.example.doccur.ui.components.DocCurButton
import com.example.doccur.ui.components.DocCurTextField
import com.example.doccur.ui.components.LoadingIndicator
import com.example.doccur.ui.theme.Red
import com.example.doccur.util.Resource
import com.example.doccur.viewmodel.PrescriptionViewModel

@Composable
fun CreatePrescriptionScreen(
    viewModel: PrescriptionViewModel,
    onBackClick: () -> Unit,
    onPrescriptionCreated: () -> Unit
) {
    val createPrescriptionState by viewModel.createPrescriptionState.collectAsState()
    
    var appointmentId by remember { mutableStateOf("") }
    var appointmentIdError by remember { mutableStateOf<String?>(null) }
    
    var medicationName by remember { mutableStateOf("") }
    var medicationDosage by remember { mutableStateOf("") }
    var medicationFrequency by remember { mutableStateOf("") }
    var medicationInstructions by remember { mutableStateOf("") }
    
    var medications = remember { mutableStateListOf<Medication>() }
    
    val scrollState = rememberScrollState()
    
    LaunchedEffect(createPrescriptionState) {
        if (createPrescriptionState is Resource.Success) {
            onPrescriptionCreated()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Prescription") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Appointment ID
                DocCurTextField(
                    value = appointmentId,
                    onValueChange = { 
                        appointmentId = it
                        appointmentIdError = null
                    },
                    label = "Appointment ID",
                    isError = appointmentIdError != null,
                    errorMessage = appointmentIdError,
                    keyboardType = KeyboardType.Number,
                    leadingIcon = Icons.Default.Event
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Medications section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Add Medications",
                            style = MaterialTheme.typography.h5
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Medication name
                        OutlinedTextField(
                            value = medicationName,
                            onValueChange = { medicationName = it },
                            label = { Text("Medication Name") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.MedicalServices, contentDescription = null)
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Medication dosage
                        OutlinedTextField(
                            value = medicationDosage,
                            onValueChange = { medicationDosage = it },
                            label = { Text("Dosage (e.g., 500mg)") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.Timer, contentDescription = null)
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Medication frequency
                        OutlinedTextField(
                            value = medicationFrequency,
                            onValueChange = { medicationFrequency = it },
                            label = { Text("Frequency (e.g., twice daily)") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.Repeat, contentDescription = null)
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Medication instructions
                        OutlinedTextField(
                            value = medicationInstructions,
                            onValueChange = { medicationInstructions = it },
                            label = { Text("Instructions (optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.Info, contentDescription = null)
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Add medication button
                        Button(
                            onClick = {
                                if (medicationName.isNotBlank() && medicationDosage.isNotBlank() && medicationFrequency.isNotBlank()) {
                                    medications.add(
                                        Medication(
                                            name = medicationName,
                                            dosage = medicationDosage,
                                            frequency = medicationFrequency,
                                            instructions = medicationInstructions.ifBlank { null }
                                        )
                                    )
                                    // Clear fields after adding
                                    medicationName = ""
                                    medicationDosage = ""
                                    medicationFrequency = ""
                                    medicationInstructions = ""
                                }
                            },
                            enabled = medicationName.isNotBlank() && medicationDosage.isNotBlank() && medicationFrequency.isNotBlank(),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Medication")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // List of added medications
                if (medications.isNotEmpty()) {
                    Text(
                        text = "Added Medications",
                        style = MaterialTheme.typography.h6
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    medications.forEachIndexed { index, medication ->
                        MedicationListItem(
                            medication = medication,
                            index = index,
                            onRemove = { medications.removeAt(index) }
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (createPrescriptionState is Resource.Error) {
                    Text(
                        text = (createPrescriptionState as Resource.Error).message,
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Create prescription button
                DocCurButton(
                    text = "Create Prescription",
                    onClick = {

                            viewModel.createPrescription(
                                appointmentId = appointmentId.toInt(),
                                medications = medications.toList()
                            )

                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = appointmentId.isNotBlank() && medications.isNotEmpty(),
                    isLoading = createPrescriptionState is Resource.Loading
                )
            }
            
            if (createPrescriptionState is Resource.Loading) {
                LoadingIndicator()
            }
        }
    }
    
    fun validateForm(): Boolean {
        var isValid = true
        
        if (appointmentId.isBlank()) {
            appointmentIdError = "Appointment ID is required"
            isValid = false
        } else {
            try {
                appointmentId.toInt()
            } catch (e: NumberFormatException) {
                appointmentIdError = "Appointment ID must be a number"
                isValid = false
            }
        }
        
        if (medications.isEmpty()) {
            isValid = false
            // Show a snackbar or other feedback
        }
        
        return isValid
    }
}

@Composable
fun MedicationListItem(
    medication: Medication,
    index: Int,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(MaterialTheme.colors.primary, shape = RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${index + 1}",
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medication.name,
                    style = MaterialTheme.typography.subtitle1
                )
                
                Text(
                    text = "${medication.dosage}, ${medication.frequency}",
                    style = MaterialTheme.typography.body2
                )
                
                if (!medication.instructions.isNullOrBlank()) {
                    Text(
                        text = medication.instructions,
                        style = MaterialTheme.typography.caption
                    )
                }
            }
            
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = Red
                )
            }
        }
    }
}