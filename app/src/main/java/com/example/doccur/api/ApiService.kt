package com.example.doccur.api

import com.example.doccur.entities.Appointment
import com.example.doccur.entities.Notification
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

    @POST("read/{notificationId}/")
    suspend fun markNotificationAsRead(
        @Path("notificationId") notificationId: Int
    ): Response<MarkReadResponse>

    @GET("appointments/patient/{patientId}/appointments/")
    suspend fun getPatientAppointments(
        @Path("patientId") patientId: Int
    ): Response<List<Appointment>>

}

data class NotificationsResponse(
    val notifications: List<Notification>
)

data class MarkReadResponse(
    val message: String
)