package com.example.doccur.repository

import com.example.doccur.api.ApiService
import com.example.doccur.model.CreatePrescriptionRequest
import com.example.doccur.model.Medication
import com.example.doccur.model.Prescription
import com.example.doccur.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PrescriptionRepository(private val apiService: ApiService) {

    suspend fun createPrescription(
        appointmentId: Int,
        medications: List<Medication>
    ): Resource<Prescription> {
        return withContext(Dispatchers.IO) {
            try {
                val request = CreatePrescriptionRequest(
                    appointment = appointmentId,
                    medications = medications
                )
                val response = apiService.createPrescription(request)
                if (response.isSuccessful) {
                    Resource.Success(response.body()!!)
                } else {
                    Resource.Error("Failed to create prescription: ${response.message()}")
                }
            } catch (e: Exception) {
                Resource.Error("Failed to create prescription: ${e.message}")
            }
        }
    }

    suspend fun getPrescription(prescriptionId: Int): Resource<Prescription> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPrescription(prescriptionId)
                if (response.isSuccessful) {
                    Resource.Success(response.body()!!)
                } else {
                    Resource.Error("Failed to get prescription: ${response.message()}")
                }
            } catch (e: Exception) {
                Resource.Error("Failed to get prescription: ${e.message}")
            }
        }
    }

    suspend fun getPrescriptionsByDoctorAndPatient(
        doctorId: Int,
        patientId: Int
    ): Resource<List<Prescription>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPrescriptionsByDoctorAndPatient(doctorId, patientId)
                if (response.isSuccessful) {
                    Resource.Success(response.body()!!)
                } else {
                    Resource.Error("Failed to get prescriptions: ${response.message()}")
                }
            } catch (e: Exception) {
                Resource.Error("Failed to get prescriptions: ${e.message}")
            }
        }
    }
}