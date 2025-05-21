package com.example.doccur.entities


data class Appointment(
    val doctor_id: Int,
    val patient_id: Int,
    val date: String,
    val time: String,
    val status: String,
    val qr_code: String
)

data class AppointmentWithPatient(
    val appointment: AppointmentResponse,
    val patient: Patient
)
