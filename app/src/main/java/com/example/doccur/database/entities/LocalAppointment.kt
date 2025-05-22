package com.example.doccur.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class LocalAppointment(
    @PrimaryKey val id: Int,
    val date: String,
    val time: String,
    val status: String,
    val qrCode: String?,
    val doctorId: Int,
    val doctorName: String,
    val doctorSpeciality: String,
    val doctorImage: String,
    val patientId: Int,
    val patientName: String,
    val hasPrescription: Boolean,
    val lastUpdated: Long = System.currentTimeMillis(),
    val isSynced: Boolean = true
)