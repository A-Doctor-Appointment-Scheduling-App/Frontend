package com.example.doccur.entities

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class Appointment(
    val id: Int,
    val doctor: Doctor,
    val patient: Patient,
    val date: String, // Format: "YYYY-MM-DD"
    val time: String, // Format: "HH:MM:SS"
    val status: String,
    val qrCodeUrl: String? = null
) {
    val day: String
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
                LocalDate.parse(date, formatter).dayOfMonth.toString()
            } catch (e: Exception) {
                "-"
            }
        } else {
            "-"
        }

    val month: String
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
                LocalDate.parse(date, formatter).month.name.lowercase()
            } catch (e: Exception) {
                "-"
            }
        } else {
            "-"
        }
}

data class Doctor(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val specialty: String? = null,        // Optional if not used now
    val profileImage: String? = null      // Full URL to image if available
) {
    val fullName: String
        get() = "Dr. $firstName $lastName"
}

data class Patient(
    val id: Int,
    val firstName: String,
    val lastName: String
)
