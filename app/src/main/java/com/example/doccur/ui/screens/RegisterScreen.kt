package com.example.doccur.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.doccur.ui.components.DocCurButton
import com.example.doccur.ui.components.DocCurPasswordField
import com.example.doccur.ui.components.DocCurTextField
import com.example.doccur.ui.components.LoadingIndicator
import com.example.doccur.ui.theme.Blue
import com.example.doccur.util.Resource
import com.example.doccur.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val registrationState by viewModel.registrationState.collectAsState()
    
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneNumberError by remember { mutableStateOf<String?>(null) }
    var addressError by remember { mutableStateOf<String?>(null) }
    var dateOfBirthError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    
    val scrollState = rememberScrollState()
    fun isValidDateFormat(date: String): Boolean {
        // Simple regex for YYYY-MM-DD
        val regex = Regex("\\d{4}-\\d{2}-\\d{2}")
        return regex.matches(date)
    }
    fun validateAndRegister() {
        var isValid = true

        // Validate First Name
        if (firstName.isBlank()) {
            firstNameError = "First name is required"
            isValid = false
        }

        // Validate Last Name
        if (lastName.isBlank()) {
            lastNameError = "Last name is required"
            isValid = false
        }

        // Validate Email
        if (email.isBlank()) {
            emailError = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Enter a valid email"
            isValid = false
        }

        // Validate Phone Number
        if (phoneNumber.isBlank()) {
            phoneNumberError = "Phone number is required"
            isValid = false
        }

        // Validate Address
        if (address.isBlank()) {
            addressError = "Address is required"
            isValid = false
        }

        // Validate Date of Birth
        if (dateOfBirth.isBlank()) {
            dateOfBirthError = "Date of birth is required"
            isValid = false
        } else if (!isValidDateFormat(dateOfBirth)) {
            dateOfBirthError = "Use format YYYY-MM-DD"
            isValid = false
        }

        // Validate Password
        if (password.isBlank()) {
            passwordError = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Password should be at least 6 characters"
            isValid = false
        }

        // Validate Confirm Password
        if (confirmPassword.isBlank()) {
            confirmPasswordError = "Please confirm your password"
            isValid = false
        } else if (confirmPassword != password) {
            confirmPasswordError = "Passwords don't match"
            isValid = false
        }

        if (isValid) {
            viewModel.registerPatient(
                firstName = firstName,
                lastName = lastName,
                email = email,
                phoneNumber = phoneNumber,
                address = address,
                dateOfBirth = dateOfBirth,
                password = password
            )
        }
    }


    LaunchedEffect(registrationState) {
        if (registrationState is Resource.Success) {
            onRegisterSuccess()
        }
    }
    
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Create Patient Account",
                    style = MaterialTheme.typography.h2,
                    color = Blue
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // First Name
                        DocCurTextField(
                            value = firstName,
                            onValueChange = { 
                                firstName = it
                                firstNameError = null
                            },
                            label = "First Name",
                            isError = firstNameError != null,
                            errorMessage = firstNameError,
                            leadingIcon = Icons.Default.Person
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Last Name
                        DocCurTextField(
                            value = lastName,
                            onValueChange = { 
                                lastName = it
                                lastNameError = null
                            },
                            label = "Last Name",
                            isError = lastNameError != null,
                            errorMessage = lastNameError,
                            leadingIcon = Icons.Default.Person
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Email
                        DocCurTextField(
                            value = email,
                            onValueChange = { 
                                email = it
                                emailError = null
                            },
                            label = "Email",
                            isError = emailError != null,
                            errorMessage = emailError,
                            keyboardType = KeyboardType.Email,
                            leadingIcon = Icons.Default.Email
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Phone Number
                        DocCurTextField(
                            value = phoneNumber,
                            onValueChange = { 
                                phoneNumber = it
                                phoneNumberError = null
                            },
                            label = "Phone Number",
                            isError = phoneNumberError != null,
                            errorMessage = phoneNumberError,
                            keyboardType = KeyboardType.Phone,
                            leadingIcon = Icons.Default.Phone
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Address
                        DocCurTextField(
                            value = address,
                            onValueChange = { 
                                address = it
                                addressError = null
                            },
                            label = "Address",
                            isError = addressError != null,
                            errorMessage = addressError,
                            leadingIcon = Icons.Default.Home
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Date of Birth (YYYY-MM-DD)
                        DocCurTextField(
                            value = dateOfBirth,
                            onValueChange = { 
                                dateOfBirth = it
                                dateOfBirthError = null
                            },
                            label = "Date of Birth (YYYY-MM-DD)",
                            isError = dateOfBirthError != null,
                            errorMessage = dateOfBirthError,
                            leadingIcon = Icons.Default.DateRange
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Password
                        DocCurPasswordField(
                            value = password,
                            onValueChange = { 
                                password = it
                                passwordError = null
                            },
                            label = "Password",
                            isError = passwordError != null,
                            errorMessage = passwordError
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Confirm Password
                        DocCurPasswordField(
                            value = confirmPassword,
                            onValueChange = { 
                                confirmPassword = it
                                confirmPasswordError = null
                            },
                            label = "Confirm Password",
                            isError = confirmPasswordError != null,
                            errorMessage = confirmPasswordError,
                            imeAction = ImeAction.Done,
                            onImeAction = { validateAndRegister() }
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        AnimatedVisibility(
                            visible = registrationState is Resource.Error,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Text(
                                text = (registrationState as? Resource.Error)?.message ?: "An error occurred",
                                color = MaterialTheme.colors.error,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        DocCurButton(
                            text = "Register",
                            onClick = { validateAndRegister() },
                            modifier = Modifier.fillMaxWidth(),
                            isLoading = registrationState is Resource.Loading
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Already have an account? ",
                        style = MaterialTheme.typography.body2
                    )
                    TextButton(onClick = onNavigateToLogin) {
                        Text("Login")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            if (registrationState is Resource.Loading) {
                LoadingIndicator()
            }
        }
    }
    

}