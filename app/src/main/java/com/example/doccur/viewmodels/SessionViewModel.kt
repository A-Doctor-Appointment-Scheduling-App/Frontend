package com.example.doccur.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SessionViewModel : ViewModel() {
    private val _patientId = mutableStateOf<Int?>(null)
    val patientId: State<Int?> = _patientId

    fun setPatientId(id: Int) {
        Log.d("SessionViewModel", "Setting patient ID to $id")
        _patientId.value = id
    }
}
