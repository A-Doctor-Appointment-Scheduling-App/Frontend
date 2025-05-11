package com.example.doccur.model

data class Patient(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val email: String,
    val phone_number: String,
    val address: String,
    val date_of_birth: String
)

data class PatientsList(
    val patients: List<Patient>
)