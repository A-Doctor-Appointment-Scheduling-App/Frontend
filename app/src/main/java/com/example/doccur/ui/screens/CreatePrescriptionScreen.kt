package com.example.doccur.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.doccur.model.Medication
import com.example.doccur.ui.components.DocCurButton
import com.example.doccur.ui.components.LoadingIndicator
import com.example.doccur.ui.theme.Red

import com.example.doccur.util.Resource
import com.example.doccur.viewmodel.PrescriptionViewModel

@Composable
fun CreatePrescriptionScreen(
    viewModel: PrescriptionViewModel,
    appointmentId: Int, // Changed appointmentId to Int
    onBackClick: () -> Unit,
    onPrescriptionCreated: () -> Unit
) {
    val createPrescriptionState by viewModel.createPrescriptionState.collectAsState()

    var medicationName by remember { mutableStateOf("") }
    var medicationDosage by remember { mutableStateOf("") }
    var medicationFrequency by remember { mutableStateOf("") }
    var medicationInstructions by remember { mutableStateOf("") }
    val medications = remember { mutableStateListOf<Medication>() }

    val scrollState = rememberScrollState()

    LaunchedEffect(createPrescriptionState) {
        if (createPrescriptionState is Resource.Success) {
            onPrescriptionCreated()
            viewModel.resetCreatePrescriptionState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create Prescription",
                        style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                        color = TrendyDeepBlue
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TrendyDeepBlue)
                    }
                },
                backgroundColor = TrendyCardBackground,
                elevation = 0.dp
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TrendyBackground)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Appointment ID Display
                Card(
                    shape = RoundedCornerShape(12.dp),
                    backgroundColor = TrendyCardBackground,
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Appointment ID: $appointmentId",
                            style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
                            color = TrendyDeepBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Medications Section
                Card(
                    shape = RoundedCornerShape(12.dp),
                    backgroundColor = TrendyCardBackground,
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
                            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                            color = TrendyDeepBlue
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Medication Name
                        OutlinedTextField(
                            value = medicationName,
                            onValueChange = { medicationName = it },
                            label = { Text("Medication Name") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.MedicalServices, contentDescription = null, tint = TrendyDeepBlue)
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Medication Dosage
                        OutlinedTextField(
                            value = medicationDosage,
                            onValueChange = { medicationDosage = it },
                            label = { Text("Dosage (e.g., 500mg)") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = TrendyDeepBlue)
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Medication Frequency
                        OutlinedTextField(
                            value = medicationFrequency,
                            onValueChange = { medicationFrequency = it },
                            label = { Text("Frequency (e.g., twice daily)") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.Repeat, contentDescription = null, tint = TrendyDeepBlue)
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Medication Instructions
                        OutlinedTextField(
                            value = medicationInstructions,
                            onValueChange = { medicationInstructions = it },
                            label = { Text("Instructions (optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.Info, contentDescription = null, tint = TrendyDeepBlue)
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Add Medication Button
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

                // List of Medications
                if (medications.isNotEmpty()) {
                    Text(
                        text = "Added Medications",
                        style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                        color = TrendyDeepBlue
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

                // Error Message
                if (createPrescriptionState is Resource.Error) {
                    Text(
                        text = (createPrescriptionState as Resource.Error).message,
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // Create Prescription Button
                DocCurButton(
                    text = "Create Prescription",
                    onClick = {
                        viewModel.createPrescription(
                            appointmentId = appointmentId,
                            medications = medications.toList()
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = medications.isNotEmpty(),
                    isLoading = createPrescriptionState is Resource.Loading
                )
            }

            // Full-Screen Loading Indicator
            if (createPrescriptionState is Resource.Loading) {
                LoadingIndicator()
            }
        }
    }
}

@Composable
fun MedicationListItem(
    medication: Medication,
    index: Int,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        backgroundColor = TrendyCardBackground,
        elevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
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
                    .background(TrendyDeepBlue, shape = RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${index + 1}",
                    color = Color.White,
                    style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medication.name,
                    style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
                    color = TrendyDeepBlue
                )

                Text(
                    text = "${medication.dosage}, ${medication.frequency}",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )

                if (!medication.instructions.isNullOrBlank()) {
                    Text(
                        text = medication.instructions,
                        style = MaterialTheme.typography.caption,
                        color = Color.Gray
                    )
                }
            }

            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Delete, contentDescription = "Remove", tint = Red
                )
            }
        }
    }
}