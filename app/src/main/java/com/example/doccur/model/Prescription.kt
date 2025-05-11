package com.example.doccur.model

data class Medication(
    val name: String,
    val dosage: String,
    val frequency: String,
    val instructions: String?
)

data class Prescription(
    val id: Int,
    val appointment: Int,
    val medications: List<Medication>,
    val issued_date: String
)

data class CreatePrescriptionRequest(
    val appointment: Int,
    val medications: List<Medication>
)