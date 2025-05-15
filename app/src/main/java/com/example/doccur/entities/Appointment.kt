package com.example.doccur.entities

import android.os.Build
import androidx.annotation.RequiresApi
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
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
        get() = try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
            LocalDate.parse(date, formatter).dayOfMonth.toString()
        } catch (e: Exception) {
            "-"
        }


    val month: String
        get() = try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
            LocalDate.parse(date, formatter).month.name.lowercase()
        } catch (e: Exception) {
            "-"
        }

}
