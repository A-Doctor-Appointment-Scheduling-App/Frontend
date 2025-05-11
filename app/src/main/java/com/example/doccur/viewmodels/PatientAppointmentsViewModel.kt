package com.example.doccur.viewmodels

import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import com.example.doccur.entities.Appointment
import com.example.doccur.entities.Doctor
import com.example.doccur.entities.Patient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
class PatientAppointmentsViewModel : ViewModel() {
    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments

    init {
        loadAppointments()
    }

    private fun loadAppointments() {
        viewModelScope.launch {
            val doctor = Doctor(id = 7, firstName = "Karim", lastName = "Brahimi")
            val patient = Patient(id = 2, firstName = "Yasmine", lastName = "Dali")

            _appointments.value = listOf(
                Appointment(1, doctor, patient, "2025-05-20", "14:00:00", "Confirmed", null),
                Appointment(2, doctor, patient, "2025-05-22", "10:00:00", "Pending", null)
            )
        }
    }
}
