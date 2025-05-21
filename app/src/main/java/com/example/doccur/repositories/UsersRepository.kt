package com.example.doccur.repositories

import com.example.doccur.api.RetrofitClient
import com.example.doccur.entities.Doctor
import com.example.doccur.entities.Doctor2
import com.example.doccur.entities.DoctorDetails
import retrofit2.Response

class UsersRepository {

    suspend fun fetchDoctors(): Response<List<Doctor2>>? {
        return try {
            RetrofitClient.apiService.getDoctors()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun fetchDoctorDetails(id: Int): Response<DoctorDetails>? {
        return try {
            RetrofitClient.apiService.getDoctorDetails(id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
