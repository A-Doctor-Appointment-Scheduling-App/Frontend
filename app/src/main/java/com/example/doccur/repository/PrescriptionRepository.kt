package com.example.doccur.repository

import android.util.Log
import com.example.doccur.api.ApiService
import com.example.doccur.dao.PrescriptionDao
import com.example.doccur.model.CreatePrescriptionRequest
import com.example.doccur.model.Medication
import com.example.doccur.model.Prescription
import com.example.doccur.model.PrescriptionEntity

import com.example.doccur.util.Resource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PrescriptionRepository(private val apiService: ApiService, private val prescriptionDao: PrescriptionDao) {

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
                Log.d("requst in repo","requst in repo$request")
                val response = apiService.createPrescription(request)
                Log.d("response in repo","response in repo$response")

                if (response.isSuccessful) {
                    // Save to local database
                    val prescriptionEntity = PrescriptionEntity(
                        appointment = appointmentId,
                        medications = Gson().toJson(medications),
                        issued_date = response.body()!!.issued_date,
                        isSynced = true
                    )
                    prescriptionDao.insert(prescriptionEntity)
                    Resource.Success(response.body()!!)
                } else {
                    // Save to local database as unsynced
                    val prescriptionEntity = PrescriptionEntity(
                        appointment = appointmentId,
                        medications = Gson().toJson(medications),
                        issued_date = "Pending", // You might want to set a proper date
                        isSynced = false
                    )
                    prescriptionDao.insert(prescriptionEntity)
                    Resource.Error("Failed to create prescription: ${response.message()}")
                }
            } catch (e: Exception) {
                // Save to local database as unsynced
                val prescriptionEntity = PrescriptionEntity(
                    appointment = appointmentId,
                    medications = Gson().toJson(medications),
                    issued_date = "Pending", // You might want to set a proper date
                    isSynced = false
                )
                prescriptionDao.insert(prescriptionEntity)
                Resource.Error("Failed to create prescription: ${e.message}")
            }
        }
    }

    suspend fun syncUnsyncedPrescriptions() {
        val unsyncedPrescriptions = prescriptionDao.getUnsyncedPrescriptions()
        unsyncedPrescriptions.forEach { prescription ->
            try {
                val medications = Gson().fromJson(prescription.medications, Array<Medication>::class.java).toList()
                val request = CreatePrescriptionRequest(
                    appointment = prescription.appointment,
                    medications = medications
                )
                val response = apiService.createPrescription(request)
                if (response.isSuccessful) {
                    // Update the local database to mark as synced
                    prescriptionDao.update(prescription.copy(isSynced = true))
                }
            } catch (e: Exception) {
                // Handle error
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
    suspend fun downloadPrescriptionPdf(prescriptionId: Int): Resource<ByteArray> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.downloadPrescriptionPdf(prescriptionId)
                if (response.isSuccessful) {
                    val pdfBytes = response.body()?.bytes()
                    if (pdfBytes != null) {
                        Resource.Success(pdfBytes)
                    } else {
                        Resource.Error("Failed to download prescription PDF: Invalid response")
                    }
                } else {
                    Resource.Error("Failed to download prescription PDF: ${response.message()}")
                }
            } catch (e: Exception) {
                Resource.Error("Failed to download prescription PDF: ${e.message}")
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
                    response.body()?.forEach { prescription ->
                        val existingPrescription = prescriptionDao.getPrescriptionById(prescription.id)
                        if (existingPrescription == null) {
                            val prescriptionEntity = PrescriptionEntity(
                                id = prescription.id,
                                appointment = prescription.appointment,
                                medications = Gson().toJson(prescription.medications),
                                issued_date = prescription.issued_date,
                                isSynced = true
                            )
                            prescriptionDao.insert(prescriptionEntity)
                        }
                    }
                    // Récupère les prescriptions locales non synchronisées
                    val unsyncedEntities = prescriptionDao.getUnsyncedPrescriptions()
                    val unsyncedPrescriptions = unsyncedEntities.map { entity ->
                        Prescription(
                            id = entity.id, // Attention, peut être 0 si local seulement
                            appointment = entity.appointment,
                            medications = Gson().fromJson(entity.medications, Array<Medication>::class.java).toList(),
                            issued_date = entity.issued_date
                        )
                    }
                    // Combine le serveur et le local (non synchronisé)
                    val result = response.body()!!.toMutableList()
                    result.addAll(unsyncedPrescriptions)
                    Resource.Success(result)
                } else {
                    // Fetch from local database if online fetch fails
                    val localPrescriptions = prescriptionDao.getAllPrescriptions()
                    if (localPrescriptions.isNotEmpty()) {
                        val prescriptions = localPrescriptions.map { entity ->
                            Prescription(
                                id = entity.id,
                                appointment = entity.appointment,
                                medications = Gson().fromJson(entity.medications, Array<Medication>::class.java).toList(),
                                issued_date = entity.issued_date
                            )
                        }
                        Resource.Success(prescriptions)
                    } else {
                        Resource.Error("Failed to get prescriptions: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                // Fetch from local database if there's an exception (e.g., no internet)
                val localPrescriptions = prescriptionDao.getAllPrescriptions()
                if (localPrescriptions.isNotEmpty()) {
                    val prescriptions = localPrescriptions.map { entity ->
                        Prescription(
                            id = entity.id,
                            appointment = entity.appointment,
                            medications = Gson().fromJson(entity.medications, Array<Medication>::class.java).toList(),
                            issued_date = entity.issued_date
                        )
                    }
                    Resource.Success(prescriptions)
                } else {
                    Resource.Error("Failed to get prescriptions: ${e.message}")
                }
            }
        }
    }


    suspend fun getPatientPrescriptions(patientId: Int): Resource<List<Prescription>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPatientPrescriptions(patientId)
                if (response.isSuccessful) {
                    Resource.Success(response.body()!!)
                } else {
                    Resource.Error("Failed to fetch prescriptions: ${response.message()}")
                }
            } catch (e: Exception) {
                Resource.Error("Failed to fetch prescriptions: ${e.message}")
            }
        }
    }
}