package com.example.doccur.api

import com.example.doccur.entities.AppointmentDetailsResponse
import com.example.doccur.entities.ConfirmAppointmentResponse
import com.example.doccur.entities.DoctorStatisticsResponse
import com.example.doccur.entities.MarkReadResponse
import com.example.doccur.entities.NotificationsResponse
import com.example.doccur.entities.PatientStatisticsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    //Notifications
    @GET("notifications/{userId}/{userType}/")
    suspend fun getNotifications(
        @Path("userId") userId: Int,
        @Path("userType") userType: String
    ): Response<NotificationsResponse>

    @POST("notifications/read/{notificationId}/")
    suspend fun markNotificationAsRead(
        @Path("notificationId") notificationId: Int
    ): Response<MarkReadResponse>

    @GET("appointments/doctor/{doctorId}/statistics/")
    suspend fun getDoctorStatistics(
        @Path("doctorId") doctorId: Int
    ): Response<DoctorStatisticsResponse>

    @GET("appointments/patient/{patientId}/statistics/")
    suspend fun getPatientStatistics(
        @Path("patientId") patientId: Int
    ): Response<PatientStatisticsResponse>

    @GET("appointments/{appointment_id}/")
    suspend fun getAppointmentDetails(
        @Path("appointment_id") appointmentId: Int
    ): Response<AppointmentDetailsResponse>

    @POST("appointments/{appointment_id}/confirm/")
    suspend fun confirmAppointment(
        @Path("appointment_id") appointmentId: Int
    ): Response<ConfirmAppointmentResponse>

    @POST("appointments/{appointment_id}/reject/")
    suspend fun rejectAppointment(
        @Path("appointment_id") appointmentId: Int,
        @Body reasonBody: RejectReasonRequest
    ): Response<ConfirmAppointmentResponse>

}


data class RejectReasonRequest(val reason: String)

