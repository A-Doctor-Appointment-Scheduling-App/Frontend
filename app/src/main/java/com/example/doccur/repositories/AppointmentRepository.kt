package com.example.doccur.repositories

import android.content.Context
import android.util.Log
import com.example.doccur.api.ApiResponse
import com.example.doccur.api.ApiService
import com.example.doccur.api.AppointmentBookRequest
import com.example.doccur.api.RejectReasonRequest
import com.example.doccur.api.RescheduleRequest
import com.example.doccur.entities.AppointmentBookResponse
import com.example.doccur.entities.AppointmentDetailsResponse
import com.example.doccur.entities.AppointmentPatient
import com.example.doccur.entities.AppointmentRescheduleResponse
import com.example.doccur.entities.AppointmentResponse
import com.example.doccur.entities.CancelAppointmentResponse
import com.example.doccur.entities.ConfirmAppointmentResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.doccur.database.AppDatabase
import com.example.doccur.database.entities.LocalAppointment
import com.example.doccur.entities.DoctorData
import com.example.doccur.entities.PatientData

class AppointmentRepository(private val apiService: ApiService, context: Context) {

    private val db: AppDatabase = AppDatabase.getDatabase(context)
    suspend fun scanQrCode(appointmentId: Int): ApiResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.scanQrCode(appointmentId)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw Exception("API error: ${response.code()} - ${response.message()}")
            }
        }
    }

    suspend fun getFullAppointmentsForDoctor(doctorId: Int): List<AppointmentPatient> {
        return try {
            val response = apiService.getFullAppointmentsByDoctor(doctorId)
            response
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun cacheAppointments(patientId: Int, appointments: List<AppointmentPatient>) {
        withContext(Dispatchers.IO) {
            val localAppointments = appointments.map { appointment ->
                LocalAppointment(
                    id = appointment.id,
                    date = appointment.date,
                    time = appointment.time,
                    status = appointment.status,
                    qrCode = appointment.qrCode,
                    doctorId = appointment.doctor.id,
                    doctorName = appointment.doctor.fullName,
                    doctorSpeciality = appointment.doctor.speciality,
                    doctorImage = appointment.doctor.profileImage,
                    patientId = appointment.patient.id,
                    patientName = appointment.patient.fullName,
                    hasPrescription = appointment.hasPrescription,
                    lastUpdated = System.currentTimeMillis(),
                    isSynced = true
                )
            }
            localAppointments.forEach {
                db.appointmentDao().insertAppointment(it)
            }

        }
    }

    private suspend fun convertToAppointmentPatient(local: LocalAppointment): AppointmentPatient {
        return AppointmentPatient(
            id = local.id,
            date = local.date,
            time = local.time,
            status = local.status,
            qrCode = local.qrCode,
            doctor = DoctorData(
                id = local.doctorId,
                fullName = local.doctorName,
                speciality = local.doctorSpeciality,
                profileImage = local.doctorImage
            ),
            patient = PatientData(
                id = local.patientId,
                fullName = local.patientName
            ),
            hasPrescription = local.hasPrescription
        )
    }

    suspend fun getFullAppointmentsForPatient(patientId: Int): List<AppointmentPatient> {
        return try {
            // First try to get from network
            val remoteAppointments = apiService.getFullAppointmentsByPatient(patientId)
            // Cache the remote data
            cacheAppointments(patientId, remoteAppointments)
            remoteAppointments
        } catch (e: Exception) {
            Log.e("AppointmentRepository", "Network error, falling back to local data", e)
            // Fall back to local data if network fails
            val localAppointments = db.appointmentDao().getAppointmentsForPatient(patientId)
            localAppointments.map { convertToAppointmentPatient(it) }
        }
    }




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

    suspend fun bookAppointment(request: AppointmentBookRequest): AppointmentBookResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.bookAppointment(request)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw Exception("API error: ${response.code()} - ${response.message()}")
            }
        }
    }


    suspend fun confirmAppointment(appointmentId: Int): ConfirmAppointmentResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.confirmAppointment(appointmentId)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
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

    suspend fun cancelAppointment(appointmentId: Int): CancelAppointmentResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.cancelAppointment(appointmentId)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw Exception("API error: ${response.code()} - ${response.message()}")
            }
        }
    }

    suspend fun rescheduleAppointment(appointmentId: Int, newDate: String?, newTime: String?): AppointmentRescheduleResponse {
        return withContext(Dispatchers.IO) {
            val request = RescheduleRequest(new_date = newDate, new_time = newTime)
            val response = apiService.rescheduleAppointment(appointmentId, request)

            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw Exception("API error: ${response.code()} - ${response.message()}")
            }
        }
    }


}
