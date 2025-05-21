package com.example.doccur.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doccur.api.RetrofitClient
import com.example.doccur.entities.AppointmentResponse
import com.example.doccur.repositories.DoctorAppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DoctorViewModel : ViewModel() {
    private val repository = DoctorAppointmentRepository(RetrofitClient.apiService)

    private val _appointments = MutableStateFlow<List<AppointmentResponse>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val appointments: StateFlow<List<AppointmentResponse>> = _appointments
    val isLoading: StateFlow<Boolean> = _isLoading
    val error: StateFlow<String?> = _error

    fun fetchAppointments(doctorId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                Log.d("DOCTOR_VM", "Fetching appointments for doctor: $doctorId")
                val appointments = repository.getFullAppointmentsForDoctor(doctorId)
                _appointments.value = appointments
                Log.d("DOCTOR_VM", "Fetched ${appointments.size} appointments")
            } catch (e: Exception) {
                _error.value = "Error: ${e.localizedMessage}"
                Log.e("DOCTOR_VM", "Error: ${e.stackTraceToString()}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}