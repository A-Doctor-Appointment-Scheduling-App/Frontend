package com.example.doccur.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.doccur.entities.Doctor
import com.example.doccur.entities.Patient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionViewModel : ViewModel() {
    private val _patientId = MutableStateFlow<Int?>(null)
    val patientId: StateFlow<Int?> = _patientId.asStateFlow()

    private val _doctorId = MutableStateFlow<Int?>(null)
    val doctorId: StateFlow<Int?> = _doctorId.asStateFlow()

    fun setPatientSession(id: Int) {
        _patientId.value = id
    }

    fun setDoctorSession(id: Int) {
        _doctorId.value = id
    }
}