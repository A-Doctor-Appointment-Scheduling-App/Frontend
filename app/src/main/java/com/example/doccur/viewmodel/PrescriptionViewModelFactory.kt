package com.example.doccur.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.doccur.repository.PrescriptionRepository

class PrescriptionViewModelFactory(
    private val repository: PrescriptionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrescriptionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PrescriptionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}