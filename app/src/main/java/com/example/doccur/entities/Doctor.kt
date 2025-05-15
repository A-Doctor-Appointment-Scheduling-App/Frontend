package com.example.doccur.entities


data class Doctor(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val specialty: String? = null,        // Optional if not used now
    val profileImage: String? = null      // Full URL to image if available
)
{
    val fullName: String
        get() = "Dr. $firstName $lastName"
}
