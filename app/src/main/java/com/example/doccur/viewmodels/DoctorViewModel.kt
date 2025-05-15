package com.example.doccur.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doccur.entities.Appointment
import com.example.doccur.entities.Doctor
import com.example.doccur.entities.Patient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DoctorViewModel : ViewModel() {

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments

    init {
        loadAppointments()
    }

    private fun loadAppointments() {
        viewModelScope.launch {
            val doctor = Doctor(id = 1, firstName = "Sarah", lastName = "Benali")
            val patient1 = Patient(id = 10, firstName = "Manel", lastName = "Arabi")
            val patient2 = Patient(id = 11, firstName = "Taous", lastName = "Haddad")
            val patient3 = Patient(id =12, firstName = "Lina", lastName = "Haddad")
            val patient4 = Patient(id = 13, firstName = "Lyna", lastName = "Haddad")
            val patient5 = Patient(id = 14, firstName = "Serine", lastName = "Haddad")

            val dummyData = listOf(
                Appointment(
                    id = 1,
                    doctor = doctor,
                    patient = patient1,
                    date = "2025-05-12",
                    time = "09:00:00",
                    status = "Confirmed",
                    qrCodeUrl = "https://example.com/qr/1.png"
                ),
                Appointment(
                    id = 2,
                    doctor = doctor,
                    patient = Patient(id = 0, firstName = "", lastName = ""), // Empty for available slot
                    date = "2025-05-10",
                    time = "09:30:00",
                    status = "Available",
                    qrCodeUrl = null
                ),
                Appointment(
                    id = 3,
                    doctor = doctor,
                    patient = patient2,
                    date = "2025-05-10",
                    time = "10:00:00",
                    status = "Scheduled",
                    qrCodeUrl = null
                ), Appointment(
                        id = 4,
                doctor = doctor,
                patient = patient3,
                date = "2025-05-12",
                time = "10:30:00",
                status = "Confirmed",
                qrCodeUrl = null
            ),
            Appointment(
                id = 5,
                doctor = doctor,
                patient = patient4,
                date = "2025-05-13",
                time = "11:00:00",
                status = "Cancelled",
                qrCodeUrl = null
            ),
            Appointment(
                id = 6,
                doctor = doctor,
                patient = patient5,
                date = "2025-05-11",
                time = "11:30:00",
                status = "Scheduled",
                qrCodeUrl = null
            ), Appointment(
                    id = 2,
                    doctor = doctor,
                    patient = Patient(id = 0, firstName = "", lastName = ""), // Empty for available slot
                    date = "2025-03-10",
                    time = "12:30:00",
                    status = "Available",
                    qrCodeUrl = null
                ),




                Appointment(
                    id = 1,
                    doctor = doctor,
                    patient = patient1,
                    date = "2025-05-11",
                    time = "09:00:00",
                    status = "Confirmed",
                    qrCodeUrl = "https://example.com/qr/1.png"
                ),
                Appointment(
                    id = 2,
                    doctor = doctor,
                    patient = Patient(id = 0, firstName = "", lastName = ""), // Empty for available slot
                    date = "2025-05-11",
                    time = "09:30:00",
                    status = "Available",
                    qrCodeUrl = null
                ),
                Appointment(
                    id = 3,
                    doctor = doctor,
                    patient = patient2,
                    date = "2025-05-11",
                    time = "10:00:00",
                    status = "Scheduled",
                    qrCodeUrl = null
                ), Appointment(
                    id = 4,
                    doctor = doctor,
                    patient = patient3,
                    date = "2025-05-11",
                    time = "10:30:00",
                    status = "Confirmed",
                    qrCodeUrl = null
                ),
                Appointment(
                    id = 5,
                    doctor = doctor,
                    patient = patient4,
                    date = "2025-05-11",
                    time = "11:00:00",
                    status = "Cancelled",
                    qrCodeUrl = null
                ),
            )

            _appointments.value = dummyData
        }
    }
}