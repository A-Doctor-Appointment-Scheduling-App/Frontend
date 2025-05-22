package com.example.doccur.entities


data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val refresh: String,
    val access: String,
    val role: String
)

data class PatientRegistrationRequest(
    val first_name: String,
    val last_name: String,
    val email: String,
    val phone_number: String,
    val address: String,
    val date_of_birth: String,
    val password: String
)

data class RegistrationResponse(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val email: String,
    val phone_number: String,
    val address: String,
    val date_of_birth: String
)