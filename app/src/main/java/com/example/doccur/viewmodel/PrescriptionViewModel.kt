package com.example.doccur.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doccur.model.Medication
import com.example.doccur.model.Prescription
import com.example.doccur.repository.PrescriptionRepository
import com.example.doccur.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PrescriptionViewModel(private val repository: PrescriptionRepository) : ViewModel() {

    private val _createPrescriptionState = MutableStateFlow<Resource<Prescription>>(Resource.Loading)
    val createPrescriptionState: StateFlow<Resource<Prescription>> = _createPrescriptionState

    private val _prescriptionState = MutableStateFlow<Resource<Prescription>>(Resource.Loading)
    val prescriptionState: StateFlow<Resource<Prescription>> = _prescriptionState

    private val _prescriptionsListState = MutableStateFlow<Resource<List<Prescription>>>(Resource.Loading)
    val prescriptionsListState: StateFlow<Resource<List<Prescription>>> = _prescriptionsListState

    fun createPrescription(appointmentId: Int, medications: List<Medication>) {
        viewModelScope.launch {
            _createPrescriptionState.value = Resource.Loading
            val result = repository.createPrescription(appointmentId, medications)
            _createPrescriptionState.value = result
        }
    }

    fun getPrescription(prescriptionId: Int) {
        viewModelScope.launch {
            _prescriptionState.value = Resource.Loading
            val result = repository.getPrescription(prescriptionId)
            _prescriptionState.value = result
        }
    }

    fun getPrescriptionsByDoctorAndPatient(doctorId: Int, patientId: Int) {
        viewModelScope.launch {
            _prescriptionsListState.value = Resource.Loading
            val result = repository.getPrescriptionsByDoctorAndPatient(doctorId, patientId)
            _prescriptionsListState.value = result
        }
    }

    fun resetStates() {
        _createPrescriptionState.value = Resource.Loading
        _prescriptionState.value = Resource.Loading
        _prescriptionsListState.value = Resource.Loading
    }
}