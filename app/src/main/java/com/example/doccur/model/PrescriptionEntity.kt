package com.example.doccur.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prescriptions")
data class PrescriptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val appointment: Int,
    val medications: String, // Serialized list of medications
    val issued_date: String,
    val isSynced: Boolean = false // To track if the prescription is synced with the server
)
