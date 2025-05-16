package com.example.doccur.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import com.example.doccur.api.RetrofitClient
import com.example.doccur.entities.Appointment
import com.example.doccur.entities.AppointmentResponse
import com.example.doccur.entities.AppointmentWithDoctor
import com.example.doccur.entities.Doctor
import com.example.doccur.entities.Patient
import com.example.doccur.repositories.PatientAppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PatientAppointmentsViewModel : ViewModel() {

    private val repository = PatientAppointmentRepository(RetrofitClient.apiService)

    private val _appointments = MutableStateFlow<List<AppointmentWithDoctor>>(emptyList())
    val appointments: StateFlow<List<AppointmentWithDoctor>> = _appointments

    fun fetchAppointments(patientId: Int) {
        Log.d("PatientAppointmentsVM", "Fetching appointments for patientId = $patientId")
        viewModelScope.launch {
            try {
                val rawAppointments = repository.getAppointmentsForPatient(patientId)
                Log.d("PatientAppointmentsVM", "Fetched ${rawAppointments.size} appointments")

                val fullAppointments = rawAppointments.mapNotNull { appointment ->
                    try {
                        val doctorResponse = RetrofitClient.apiService.getDoctorById(appointment.doctor_id)
                        if (doctorResponse.isSuccessful) {
                            val doctor = doctorResponse.body()
                            doctor?.let {
                                AppointmentWithDoctor(appointment, it)
                            }
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        Log.e("PatientAppointmentsVM", "Error fetching doctor: ${e.message}")
                        null
                    }
                }

                _appointments.value = fullAppointments

            } catch (e: Exception) {
                Log.e("PatientAppointmentsVM", "Error fetching appointments: ${e.message}")
            }
        }
    }
}


