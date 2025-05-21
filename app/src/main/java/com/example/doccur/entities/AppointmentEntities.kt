package com.example.doccur.entities

import com.google.gson.annotations.SerializedName
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
        @RequiresApi(Build.VERSION_CODES.O)
        get() = try {
            val formatter = org.threeten.bp.format.DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
            org.threeten.bp.LocalDate.parse(date, formatter).dayOfMonth.toString()
        } catch (e: Exception) {
            "-"
        }


    val month: String
        @RequiresApi(Build.VERSION_CODES.O)
        get() = try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
            LocalDate.parse(date, formatter).month.name.lowercase()
        } catch (e: Exception) {
            "-"
        }

}

data class AppointmentDetailsResponse(
    val id: Int,
    val doctor: String,
    val patient: PatientInfo,
    val date: String,
    val time: String,
    val status: String,
    val qr_code: String?,
    @SerializedName("has_prescription")
    val hasPrescription: Boolean
)

data class PatientInfo(
    val full_name: String,
    val email: String,
    val phone_number: String,
    val address: String,
    val date_of_birth: String
)

data class ConfirmAppointmentResponse(
    val message: String,
    val qr_code: String,
    val qr_data: QrData
)

data class QrData(
    val appointment_id: Int,
    val doctor: String,
    val patient: String,
    val date: String,
    val time: String
)

data class AppointmentResponse(
    val doctor_id: Int,
    val patient_id: Int,
    val date: String,
    val time: String,
    val status: String,
    @SerializedName("qr_code")
    val qr_code_url: String?
)
data class AppointmentWithDoctor(
    val appointment: AppointmentResponse,
    val doctor: Doctor
)


