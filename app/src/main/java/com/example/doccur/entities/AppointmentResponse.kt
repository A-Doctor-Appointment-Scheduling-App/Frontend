package com.example.doccur.entities

import com.google.gson.annotations.SerializedName


data class AppointmentResponse(
    val id: Int,
    val date: String,
    val time: String,
    val status: String,
    @SerializedName("qr_code")
    val qrCode: String?,
    val doctor: Int,
    val patient: Int
)

data class AppointmentWithDoctor(
    val appointment: AppointmentResponse,
    val doctor: Doctor
)




