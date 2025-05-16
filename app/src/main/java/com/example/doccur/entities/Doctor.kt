package com.example.doccur.entities

import com.google.gson.annotations.SerializedName

data class Doctor(
    val id: Int,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    val specialty: String? = null,

    @SerializedName("photo_url")
    val profileImage: String? = null
) {
    val fullName: String
        get() = "Dr. $firstName $lastName"
}
