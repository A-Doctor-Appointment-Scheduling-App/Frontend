package com.example.doccur.viewmodels

// viewmodels/DoctorViewModelFactory.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.doccur.api.ApiService
import com.example.doccur.repositories.DoctorRepository
import com.example.doccur.api.RetrofitClient

class DoctorViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorrViewModel::class.java)) {
            return DoctorrViewModel(
                doctorRepository = DoctorRepository(
                    RetrofitClient.apiService // Changed from instance to apiService
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}