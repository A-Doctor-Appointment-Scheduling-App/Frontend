package com.example.doccur.entities

data class AppointmentDetailsResponse(
    val id: Int,
    val doctor: String,
    val patient: PatientInfo,
    val date: String,
    val time: String,
    val status: String,
    val qr_code: String? // URL or null
)

data class PatientInfo(
    val full_name: String,
    val email: String,
    val phone_number: String,
    val address: String,
    val date_of_birth: String
)

data class ConfirmAppointmentResponse(
    val message: String
)
