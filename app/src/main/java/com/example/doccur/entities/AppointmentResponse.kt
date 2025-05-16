package com.example.doccur.entities

import com.google.gson.annotations.SerializedName

data class AppointmentResponse(
    val doctor_id: Int,
    val patient_id: Int,
    val date: String,
    val time: String,
    val status: String,
    @SerializedName("qr_code") val qr_code_url: String?
)
data class AppointmentWithDoctor(
    val appointment: AppointmentResponse,
    val doctor: Doctor
)




