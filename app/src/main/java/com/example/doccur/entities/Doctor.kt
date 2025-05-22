package com.example.doccur.entities

data class Doctor(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val specialty: String,
    val photo_url: String,
    val clinic: String,
    val availability: List<String> = emptyList(),
    val facebook_link: String? = null,
    val instagram_link: String? = null,
    val twitter_link: String? = null,
    val linkedin_link: String? = null
)



