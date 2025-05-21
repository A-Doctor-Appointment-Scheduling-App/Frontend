package com.example.doccur.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doccur.entities.AppointmentResponse
import com.example.doccur.repositories.DoctorAppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DoctorAppointmentViewModel(
    private val repository: DoctorAppointmentRepository
) : ViewModel() {

    private val _appointments = MutableStateFlow<List<AppointmentResponse>>(emptyList())
    val appointments: StateFlow<List<AppointmentResponse>> = _appointments

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchAppointments(doctorId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val result = repository.getFullAppointmentsForDoctor(doctorId)
                _appointments.value = result
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
