package com.example.doccur.ui.screens

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Download
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doccur.model.Prescription
import com.example.doccur.ui.components.ErrorMessage
import com.example.doccur.ui.components.LoadingIndicator
import com.example.doccur.ui.theme.White
// Removed Blue, CardShape, LightGray from ui.theme as they are replaced or unused
import com.example.doccur.util.Resource
import com.example.doccur.viewmodel.PrescriptionViewModel
import java.io.File
import java.io.FileOutputStream

// --- Trendy Colors & Shapes ---
val TrendyDeepBlue = Color(0xFF007BFF) // A more refined, deep blue
val TrendyBackground = Color(0xFFF0F4F8) // A very light, cool gray for the main background
val TrendyCharcoalText = Color(0xFF263238) // Dark gray for primary text
val TrendySlateGrayText = Color(0xFF546E7A) // Lighter gray for secondary text
val TrendyCardBackground = Color.White // White for cards to make them pop
val TrendyHighlightColor = Color(0xFFE0F7FA) // Light cyan for subtle highlights or borders

val TrendyCardShape = RoundedCornerShape(16.dp) // Softer, more modern rounded corners
val PillShape = RoundedCornerShape(50) // For pill-like elements

// --- End Trendy Colors & Shapes ---

@Composable
fun PrescriptionDetailScreen(
    viewModel: PrescriptionViewModel,
    prescriptionId: Int,
    onBackClick: () -> Unit,
    onDownloadClick: (Int) -> Unit // Kept for consistency, though download action is in TopAppBar
) {
    val prescriptionState by viewModel.prescriptionState.collectAsState()
    val downloadState by viewModel.downloadPrescriptionState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(prescriptionId) {
        viewModel.getPrescription(prescriptionId)
    }

    LaunchedEffect(downloadState) {
        if (downloadState is Resource.Success) {
            Log.d("succes download ", "succes download ")
            val pdfBytes = (downloadState as Resource.Success<ByteArray>).data
            Log.d("downloadState ", "downloadState $pdfBytes")
            savePdfToDownloads(context, pdfBytes, "prescription_$prescriptionId.pdf")
        } else if (downloadState is Resource.Error) {
            Toast.makeText(context, "Download failed: ${(downloadState as Resource.Error).message}", Toast.LENGTH_LONG).show()
        }
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
                actions = {
                    IconButton(onClick = { viewModel.downloadPrescriptionPdf(prescriptionId) }) {
                        Icon(Icons.Default.Download, contentDescription = "Download PDF", tint = ModernBluePrimary)
                    }
                },
                elevation = 0.dp,
                backgroundColor = White
            )

        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TrendyBackground) // Use trendy background color
                .padding(padding)
        ) {
            when (val state = prescriptionState) { // Use 'state' for easier access
                is Resource.Loading -> {
                    LoadingIndicator()
                }

                is Resource.Error -> {
                    ErrorMessage(
                        message = state.message,
                        onRetry = { viewModel.getPrescription(prescriptionId) }
                    )
                }

                is Resource.Success -> {
                    val prescription = state.data
                    PrescriptionContent(prescription = prescription, scrollState = scrollState)
                }
            }

            // Show a subtle loading indicator for download
            if (downloadState is Resource.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = TrendyDeepBlue)
                }
            }
        }
    }
}

fun savePdfToDownloads(context: Context, pdfBytes: ByteArray, fileName: String) {
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    if (!downloadsDir.exists()) {
        downloadsDir.mkdirs()
    }
    val file = File(downloadsDir, fileName)

    try {
        FileOutputStream(file).use { outputStream ->
            outputStream.write(pdfBytes)
        }
        Toast.makeText(context, "PDF saved to Downloads folder", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Log.e("SavePdf", "Failed to save PDF", e)
        Toast.makeText(context, "Failed to save PDF: ${e.message}", Toast.LENGTH_LONG).show()
    }
}


@Composable
fun PrescriptionContent(prescription: Prescription, scrollState: androidx.compose.foundation.ScrollState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 20.dp) // Adjusted padding
    ) {
        // Prescription header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = TrendyCardShape,
            backgroundColor = TrendyCardBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp) // Generous padding inside card
            ) {
                Text(
                    text = "Prescription #${prescription.id}",
                    style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold),
                    color = TrendyDeepBlue
                )
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = TrendyBackground, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                InfoRow(label = "Date Issued:", value = prescription.issued_date.substringBefore("T"))
                Spacer(modifier = Modifier.height(10.dp))
                InfoRow(label = "Appointment ID:", value = prescription.appointment.toString())
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Medications Section Title
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 4.dp)) {
            Text(
                text = "Medications",
                style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                color = TrendyDeepBlue
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .weight(1f) // Make underline dynamic
                    .background(TrendyDeepBlue, shape = PillShape)
            )
        }


        Spacer(modifier = Modifier.height(20.dp))

        if (prescription.medications.isEmpty()) {
            EmptyStateCard(
                title = "No Medications Prescribed",
                message = "It seems there are no medications listed for this consultation."
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
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp)) // Extra space at the bottom
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top // Align to top for potentially multi-line values
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.SemiBold),
            color = TrendySlateGrayText,
            modifier = Modifier.weight(0.4f) // Give label fixed proportion
        )
        Text(
            text = value,
            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Medium),
            color = TrendyCharcoalText,
            modifier = Modifier.weight(0.6f) // Give value fixed proportion
        )
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
        shape = TrendyCardShape,
        backgroundColor = TrendyCardBackground,
        border = BorderStroke(1.dp, TrendyHighlightColor.copy(alpha = 0.5f)) // Subtle border
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
                        .size(36.dp)
                        .background(TrendyDeepBlue, shape = PillShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = index.toString(),
                        color = Color.White,
                        style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                    color = TrendyDeepBlue
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = TrendyBackground, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Add space between items
            ) {
                MedicationDetailItem(label = "Dosage", value = dosage, modifier = Modifier.weight(1f))
                MedicationDetailItem(label = "Frequency", value = frequency, modifier = Modifier.weight(1f))
            }

            if (!instructions.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                MedicationDetailItem(label = "Instructions", value = instructions, isBlock = true)
            }
        }
    }
}

@Composable
fun MedicationDetailItem(label: String, value: String, modifier: Modifier = Modifier, isBlock: Boolean = false) {
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(), // Uppercase for distinction
            style = MaterialTheme.typography.overline.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp // Slight letter spacing for overline
            ),
            color = TrendySlateGrayText
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.body2.copy(
                color = TrendyCharcoalText,
                lineHeight = if (isBlock) 20.sp else MaterialTheme.typography.body2.lineHeight
            )
        )
    }
}

@Composable
fun EmptyStateCard(title: String, message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        shape = TrendyCardShape,
        backgroundColor = TrendyCardBackground,
        border = BorderStroke(1.dp, TrendyHighlightColor.copy(alpha = 0.7f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 40.dp), // Increased padding
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.SemiBold),
                color = TrendyDeepBlue,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.body1,
                color = TrendySlateGrayText,
                textAlign = TextAlign.Center
            )
        }
    }
}