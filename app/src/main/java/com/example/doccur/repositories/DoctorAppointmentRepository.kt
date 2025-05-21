package com.example.doccur.repositories

import android.util.Log
import com.example.doccur.api.ApiService
import com.example.doccur.entities.AppointmentResponse
class DoctorAppointmentRepository(private val apiService: ApiService) {
    suspend fun getFullAppointmentsForDoctor(doctorId: Int): List<AppointmentResponse> {
        Log.d("DOCTOR_REPO", "Fetching appointments for doctor: $doctorId")
        return try {
            val response = apiService.getFullAppointmentsByDoctor(doctorId)
            Log.d("DOCTOR_REPO", "API response: ${response.size} items")
            response
        } catch (e: Exception) {
            Log.e("DOCTOR_REPO", "API call failed", e)
            emptyList()
        }
    }
}