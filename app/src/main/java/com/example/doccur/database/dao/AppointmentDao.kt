package com.example.doccur.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.doccur.database.entities.LocalAppointment

@Dao
interface AppointmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: LocalAppointment)

    @Query("SELECT * FROM appointments WHERE patientId = :patientId ORDER BY date DESC, time DESC")
    suspend fun getAppointmentsForPatient(patientId: Int): List<LocalAppointment>

    @Query("DELETE FROM appointments WHERE id = :appointmentId")
    suspend fun deleteAppointment(appointmentId: Int)

    @Query("SELECT * FROM appointments WHERE isSynced = 0")
    suspend fun getUnsyncedAppointments(): List<LocalAppointment>

    @Query("UPDATE appointments SET isSynced = 1 WHERE id = :appointmentId")
    suspend fun markAsSynced(appointmentId: Int)
}