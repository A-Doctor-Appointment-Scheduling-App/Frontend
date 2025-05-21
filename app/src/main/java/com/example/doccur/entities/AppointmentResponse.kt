package com.example.doccur.entities

import com.google.gson.annotations.SerializedName
data class AppointmentResponse(
    val id: Int,
    val doctor_id: Int,
    val patient_id: Int,
    val date: String,
    val time: String,
    val status: String,
    val qr_code: String?
)
data class AppointmentWithDoctor(
    val appointment: AppointmentResponse,
    val doctor: Doctor,
    val hasPrescription: Boolean = false
)




