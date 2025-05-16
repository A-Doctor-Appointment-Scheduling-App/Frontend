package com.example.doccur.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doccur.entities.AppointmentDetailsResponse
import com.example.doccur.repositories.AppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppointmentViewModel(private val repository: AppointmentRepository) : ViewModel() {

    private val _appointmentDetails = MutableStateFlow<AppointmentDetailsResponse?>(null)
    val appointmentDetails: StateFlow<AppointmentDetailsResponse?> = _appointmentDetails

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _confirmationMessage = MutableStateFlow<String?>(null)
    val confirmationMessage: StateFlow<String?> = _confirmationMessage

    fun fetchAppointmentDetails(appointmentId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val details = repository.getAppointmentDetails(appointmentId)
                _appointmentDetails.value = details
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun confirmAppointment(appointmentId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _confirmationMessage.value = null
            try {
                val message = repository.confirmAppointment(appointmentId)
                _confirmationMessage.value = message
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun rejectAppointment(appointmentId: Int, reason: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _confirmationMessage.value = null
            try {
                val message = repository.rejectAppointment(appointmentId, reason)
                _confirmationMessage.value = message
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }


    fun clearConfirmationMessage() {
        _confirmationMessage.value = null
    }

}
