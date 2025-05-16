package com.example.doccur.repositories

import com.example.doccur.api.ApiService
import com.example.doccur.api.RejectReasonRequest
import com.example.doccur.entities.AppointmentDetailsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppointmentRepository(private val apiService: ApiService) {

    suspend fun getAppointmentDetails(appointmentId: Int): AppointmentDetailsResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.getAppointmentDetails(appointmentId)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw Exception("API error: ${response.code()} - ${response.message()}")
            }
        }
    }

    suspend fun confirmAppointment(appointmentId: Int): String {
        return withContext(Dispatchers.IO) {
            val response = apiService.confirmAppointment(appointmentId)
            if (response.isSuccessful) {
                response.body()?.message ?: throw Exception("Empty response message")
            } else {
                throw Exception("API error: ${response.code()} - ${response.message()}")
            }
        }
    }

    suspend fun rejectAppointment(appointmentId: Int, reason: String): String {
        return withContext(Dispatchers.IO) {
            val response = apiService.rejectAppointment(appointmentId, RejectReasonRequest(reason))
            if (response.isSuccessful) {
                response.body()?.message ?: throw Exception("Empty response message")
            } else {
                throw Exception("API error: ${response.code()} - ${response.message()}")
            }
        }
    }


}
