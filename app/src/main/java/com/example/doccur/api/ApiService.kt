package com.example.doccur.api

import com.example.doccur.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Authentication endpoints
    @POST("users/login/")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("users/patient/register/")
    suspend fun registerPatient(@Body request: PatientRegistrationRequest): Response<RegistrationResponse>

    // Doctor endpoints
    @GET("users/doctors/")
    suspend fun getDoctors(): Response<List<Doctor>>

    @GET("users/doctors/{doctorId}/")
    suspend fun getDoctorDetails(@Path("doctorId") doctorId: Int): Response<Doctor>

    // Patient endpoints
    @GET("users/patients/")
    suspend fun getPatients(): Response<List<Patient>>

    @GET("users/patients/{patientId}/")
    suspend fun getPatientDetails(@Path("patientId") patientId: Int): Response<Patient>

    // Prescription endpoints
    @POST("prescriptions/create/")
    suspend fun createPrescription(@Body request: CreatePrescriptionRequest): Response<Prescription>

    @GET("prescriptions/prescriptions/{prescriptionId}/")
    suspend fun getPrescription(@Path("prescriptionId") prescriptionId: Int): Response<Prescription>

    @GET("prescriptions/prescriptions/doctor/{doctorId}/patient/{patientId}/")
    suspend fun getPrescriptionsByDoctorAndPatient(
        @Path("doctorId") doctorId: Int,
        @Path("patientId") patientId: Int
    ): Response<List<Prescription>>

    @GET("prescriptions/prescriptions/{prescriptionId}/download/")
    suspend fun downloadPrescriptionPdf(@Path("prescriptionId") prescriptionId: Int): Response<Any>
}