package com.example.doccur.repository

import android.util.Log
import com.example.doccur.api.ApiService
import com.example.doccur.model.LoginRequest
import com.example.doccur.model.LoginResponse
import com.example.doccur.model.Patient
import com.example.doccur.model.PatientRegistrationRequest
import com.example.doccur.model.RegistrationResponse
import com.example.doccur.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): Resource<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {

                val response = apiService.login(LoginRequest(email, password))

                if (response.isSuccessful) {
                    Resource.Success(response.body()!!)
                } else {
                    Resource.Error("Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                Resource.Error("Login failed: ${e.message}")
            }
        }
    }
    suspend fun getPatientDetails(patientId: Int): Resource<Patient> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPatientDetails(patientId) // Make sure this matches your Retrofit call
                if (response.isSuccessful) {
                    Resource.Success(response.body()!!)
                } else {
                    Resource.Error("Failed to get patient: ${response.message()}")
                }
            } catch (e: Exception) {
                Resource.Error("Failed to get patient: ${e.message}")
            }
        }
    }

    suspend fun loginWithGoogle(idToken: String): Resource<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("idToken in repo ","idToken in repo :$idToken")

                val response = apiService.loginWithGoogle(mapOf("id_token" to idToken))
                Log.d("response lyoum ytir rass  ","response lyoum ytir rass  :$response")

                if (response.isSuccessful) {
                    Resource.Success(response.body()!!)
                } else {
                    Resource.Error("Google login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                Resource.Error("Google login failed: ${e.message}")
            }
        }
    }
    suspend fun registerPatient(
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        address: String,
        dateOfBirth: String,
        password: String
    ): Resource<RegistrationResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = PatientRegistrationRequest(
                    first_name = firstName,
                    last_name = lastName,
                    email = email,
                    phone_number = phoneNumber,
                    address = address,
                    date_of_birth = dateOfBirth,
                    password = password
                )

                val response = apiService.registerPatient(request)

                if (response.isSuccessful) {
                    Resource.Success(response.body()!!)
                } else {
                    Resource.Error("Registration failed: ${response.message()}")
                }
            } catch (e: Exception) {
                Resource.Error("Registration failed: ${e.message}")
            }
        }
    }
}