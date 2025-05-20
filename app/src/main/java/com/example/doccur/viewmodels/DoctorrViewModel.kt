package com.example.doccur.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doccur.entities.Doctorr
import com.example.doccur.repositories.DoctorRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DoctorrViewModel(private val doctorRepository: DoctorRepository) : ViewModel() {
    var doctor by mutableStateOf<Doctorr?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun getDoctorDetails(doctorId: Int) {
        isLoading = true
        viewModelScope.launch {
            try {
                doctor = doctorRepository.getDoctorDetails(doctorId)
                error = null
            } catch (e: Exception) {
                error = e.message ?: "Unknown error occurred"
            } finally {
                isLoading = false
            }
        }
    }
}