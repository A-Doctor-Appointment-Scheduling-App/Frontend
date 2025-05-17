package com.example.doccur.ui.screens
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import com.example.doccur.model.Prescription
import com.example.doccur.ui.components.ErrorMessage
import com.example.doccur.ui.components.LoadingIndicator
import com.example.doccur.util.Resource
import com.example.doccur.util.TokenManager
import com.example.doccur.viewmodel.PrescriptionViewModel

@Composable
fun PatientPrescriptionsScreen(
    viewModel: PrescriptionViewModel,
    tokenManager: TokenManager,
    onPrescriptionClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val userId = tokenManager.getUserId()

    val state by viewModel.patientPrescriptionsState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.getPatientPrescriptions(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Prescriptions") },
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
            when (state) {
                is Resource.Loading -> LoadingIndicator()
                is Resource.Error -> ErrorMessage(
                    message = (state as Resource.Error).message,
                    onRetry = { viewModel.getPatientPrescriptions(userId) }
                )
                is Resource.Success -> {
                    val prescriptions = (state as Resource.Success<List<Prescription>>).data
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