package com.example.doccur.repositories

import com.example.doccur.api.ApiService
import com.example.doccur.entities.Doctorr

class DoctorRepository(private val apiService: ApiService) {
    suspend fun getDoctorDetails(doctorId: Int): Doctorr? {
        val response = apiService.getDoctorDetails(doctorId)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }
}