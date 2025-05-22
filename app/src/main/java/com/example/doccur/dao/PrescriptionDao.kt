package com.example.doccur.dao

import androidx.room.*
import com.example.doccur.model.PrescriptionEntity

@Dao
interface PrescriptionDao {
    @Insert
    suspend fun insert(prescription: PrescriptionEntity)

    @Update
    suspend fun update(prescription: PrescriptionEntity)

    @Query("SELECT * FROM prescriptions")
    suspend fun getAllPrescriptions(): List<PrescriptionEntity>

    @Query("SELECT * FROM prescriptions WHERE isSynced = 0")
    suspend fun getUnsyncedPrescriptions(): List<PrescriptionEntity>

    @Query("SELECT * FROM prescriptions WHERE id = :prescriptionId")
    suspend fun getPrescriptionById(prescriptionId: Int): PrescriptionEntity?
}
