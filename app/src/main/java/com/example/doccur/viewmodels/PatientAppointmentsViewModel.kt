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
import com.example.doccur.repositories.PrescriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
class PatientAppointmentsViewModel : ViewModel() {
    private val repository = PatientAppointmentRepository(RetrofitClient.apiService)
    private val prescriptionRepo = PrescriptionRepository(RetrofitClient.apiService)

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


                val rawAppointments = repository.getFullAppointmentsForPatient(patientId)

            val fullAppointments = rawAppointments.mapNotNull { appointment ->
                try {
                    // Add debug logs for appointment details
                    Log.d("AppointmentDebug", "Processing appointment: ${appointment.id}")
                    Log.v("AppointmentDebug", "Full appointment data: $appointment")

                    val doctorResponse = RetrofitClient.apiService.getDoctorById(appointment.doctor_id)

                    if (doctorResponse.isSuccessful) {
                        doctorResponse.body()?.let { doctor ->
                            // Add prescription check debug
                            Log.d("PrescriptionCheck", "Checking prescription for appointment ID: ${appointment.id}")
                            val hasPrescription = prescriptionRepo.checkPrescriptionExists(appointment.id)

                            Log.d("PrescriptionCheck", "Appointment ID: ${appointment.id} | Has prescription: $hasPrescription")

                            AppointmentWithDoctor(
                                appointment,
                                doctor,
                                hasPrescription = hasPrescription
                            )
                        }
                    } else {
                        Log.w("DoctorFetch", "Failed to fetch doctor for appointment ${appointment.id}")
                        null
                    }
                } catch (e: Exception) {
                    Log.e("MappingError", "Error processing appointment ${appointment.id}", e)
                    null
                }
            }


            _appointments.value = fullAppointments
        }
    }
}

