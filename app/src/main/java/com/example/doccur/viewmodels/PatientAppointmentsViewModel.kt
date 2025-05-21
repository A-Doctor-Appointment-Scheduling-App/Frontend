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
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val appointments: StateFlow<List<AppointmentWithDoctor>> = _appointments
    val isLoading: StateFlow<Boolean> = _isLoading
    val error: StateFlow<String?> = _error

    fun fetchAppointments(patientId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val rawAppointments = repository.getFullAppointmentsForPatient(patientId)

                val fullAppointments = rawAppointments.mapNotNull { appointment ->
                    try {
                        val doctorResponse = RetrofitClient.apiService.getDoctorById(appointment.doctor_id)
                        if (doctorResponse.isSuccessful) {
                            doctorResponse.body()?.let { doctor ->
                                AppointmentWithDoctor(appointment, doctor)
                            }
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }
                }

                _appointments.value = fullAppointments
            } catch (e: Exception) {
                _error.value = "Failed to load appointments: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}


