package com.example.doccur.api

import com.example.doccur.entities.Appointment
import com.example.doccur.entities.AppointmentResponse
import com.example.doccur.entities.Doctor
import com.example.doccur.entities.Notification
import com.example.doccur.entities.Patient
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("{userId}/{userType}/")
    suspend fun getNotifications(
        @Path("userId") userId: Int,
        @Path("userType") userType: String
    ): Response<NotificationsResponse>
    @GET("doctors/{doctor_id}/")
    suspend fun getDoctorById(@Path("doctor_id") doctorId: Int): Response<Doctor>

    @POST("read/{notificationId}/")
    suspend fun markNotificationAsRead(
        @Path("notificationId") notificationId: Int
    ): Response<MarkReadResponse>

    @GET("appointments/patient/{id}/appointments")
    suspend fun getAppointmentsByPatient(@Path("id") patientId: Int): List<AppointmentResponse>

    @GET("patients/{id}/")
    suspend fun getPatientById(@Path("id") id: Int): Response<Patient>

    @GET("appointments/doctor/{doctor_id}/appointments/")
    suspend fun getDoctorAppointments(@Path("doctor_id") doctorId: Int): Response<List<Appointment>>

    @GET("appointments/patient/{patient_id}/appointments/full/")
    suspend fun getFullAppointmentsByPatient(@Path("patient_id") patientId: Int): List<AppointmentResponse>

    @GET("appointments/doctor/{doctor_id}/appointments/full/")
    suspend fun getFullAppointmentsByDoctor(
        @Path("doctor_id") doctorId: Int
    ): List<AppointmentResponse>
}

data class NotificationsResponse(
    val notifications: List<Notification>
)

data class MarkReadResponse(
    val message: String
)