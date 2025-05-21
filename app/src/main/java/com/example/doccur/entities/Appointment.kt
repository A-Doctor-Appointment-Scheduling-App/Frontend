package com.example.doccur.entities

import android.os.Build
import androidx.annotation.RequiresApi
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale

data class Appointment(
    val doctor_id: Int,
    val patient_id: Int,
    val date: String,
    val time: String,
    val status: String,
    val qr_code: String
)
 {
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

data class AppointmentWithPatient(
    val appointment: AppointmentResponse,
    val patient: Patient
)
