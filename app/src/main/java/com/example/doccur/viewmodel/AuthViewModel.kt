package com.example.doccur.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.example.doccur.model.LoginResponse
import com.example.doccur.model.Patient
import com.example.doccur.model.RegistrationResponse
import com.example.doccur.repository.AuthRepository
import com.example.doccur.util.Resource
import com.example.doccur.util.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<LoginResponse>?>(null)
    val loginState: StateFlow<Resource<LoginResponse>?> = _loginState


    private val _registrationState = MutableStateFlow<Resource<RegistrationResponse>?>(null)
    val registrationState: StateFlow<Resource<RegistrationResponse>?> = _registrationState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading

            val result = repository.login(email, password)

            _loginState.value = result

            if (result is Resource.Success) {

                tokenManager.saveTokens(result.data.access, result.data.refresh)
                tokenManager.saveUserInfo(result.data.role, getUserIdFromJwt(result.data.access))
            }
        }
    }


    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading
            Log.d("in model view idToken"," in model view idToken:$idToken")

            val result = repository.loginWithGoogle(idToken)
            Log.d("result repo","result repo:$result")

            _loginState.value = result
            if (result is Resource.Success) {
                tokenManager.saveTokens(result.data.access, result.data.refresh)
                tokenManager.saveUserInfo(result.data.role, getUserIdFromJwt(result.data.access))
            }
        }
    }

    fun registerPatient(
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        address: String,
        dateOfBirth: String,
        password: String
    ) {
        viewModelScope.launch {

            _registrationState.value = Resource.Loading
            val result = repository.registerPatient(
                firstName,
                lastName,
                email,
                phoneNumber,
                address,
                dateOfBirth,
                password
            )

            _registrationState.value = result
        }
    }

    fun logout() {
        tokenManager.clearTokens()
        _loginState.value = Resource.Loading
    }

    private val _patientState = MutableStateFlow<Resource<Patient>>(Resource.Loading)
    val patientState: StateFlow<Resource<Patient>> = _patientState

    fun getPatient(patientId: Int) {
        viewModelScope.launch {
            _patientState.value = Resource.Loading
            _patientState.value = repository.getPatientDetails(patientId)
        }
    }


    fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()

    fun isDoctor(): Boolean = tokenManager.isDoctor()

    fun isPatient(): Boolean = tokenManager.isPatient()

    private fun getUserIdFromJwt(token: String): Int {
        try {
            // Decode the JWT token
            val jwt: DecodedJWT = JWT.decode(token)


            val userId = jwt.getClaim("user_id").asInt() // or another claim name

            return userId
        } catch (e: Exception) {
            // Handle any exceptions, such as malformed tokens
            Log.e("AuthViewModel", "Error decoding JWT: ${e.message}")
            return -1 // Return a default or error value
        }
    }
}