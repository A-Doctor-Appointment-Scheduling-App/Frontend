package com.example.doccur.entities

data class AppointmentResponse(
    val id: Int,
    val doctor: Doctor,
    val patient: Patient,
    val date: String,
    val time: String,
    val status: String,
    val qr_code_url: String?
)



