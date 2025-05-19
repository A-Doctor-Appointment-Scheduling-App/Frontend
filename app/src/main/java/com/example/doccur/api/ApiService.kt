package com.example.doccur.api

import com.example.doccur.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Authentication endpoints
    @POST("login/")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("patient/register/")
    suspend fun registerPatient(@Body request: PatientRegistrationRequest): Response<RegistrationResponse>

    // Doctor endpoints
    @GET("users/doctors/")
    suspend fun getDoctors(): Response<List<Doctor>>

    @GET("users/doctors/{doctorId}/")
    suspend fun getDoctorDetails(@Path("doctorId") doctorId: Int): Response<Doctor>

    // Patient endpoints
    @GET("users/patients/")
    suspend fun getPatients(): Response<List<Patient>>

    @GET("patients/{patientId}/")
    suspend fun getPatientDetails(@Path("patientId") patientId: Int): Response<Patient>

    @GET("prescriptions/patient/{patient_id}/prescriptions/")
    suspend fun getPatientPrescriptions(@Path("patient_id") patientId: Int): Response<List<Prescription>>
    // Prescription endpoints
    @POST("prescriptions/create/")
    suspend fun createPrescription(@Body request: CreatePrescriptionRequest): Response<Prescription>

    @GET("prescriptions/{prescriptionId}/")
    suspend fun getPrescription(@Path("prescriptionId") prescriptionId: Int): Response<Prescription>

    @GET("prescriptions/doctor/{doctorId}/patient/{patientId}/")
    suspend fun getPrescriptionsByDoctorAndPatient(
        @Path("doctorId") doctorId: Int,
        @Path("patientId") patientId: Int
    ): Response<List<Prescription>>


    @POST("login/google/")
    suspend fun loginWithGoogle(@Body idToken: Map<String, String>): Response<LoginResponse>

    @GET("prescriptions/{prescriptionId}/download/")
    suspend fun downloadPrescriptionPdf(@Path("prescriptionId") prescriptionId: Int): Response<ResponseBody>
}