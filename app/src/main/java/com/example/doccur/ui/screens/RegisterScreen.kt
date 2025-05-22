package com.example.doccur.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doccur.R
import com.example.doccur.ui.components.DocCurButton
import com.example.doccur.ui.components.DocCurPasswordField
import com.example.doccur.ui.components.DocCurTextField
import com.example.doccur.ui.components.GoogleLoginButton
import com.example.doccur.ui.components.LoadingIndicator
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
        val regex = Regex("\\d{4}-\\d{2}-\\d{2}")
        return regex.matches(date)
    }

    fun validateAndRegister() {
        var isValid = true
        firstNameError = null
        lastNameError = null
        emailError = null
        phoneNumberError = null
        addressError = null
        dateOfBirthError = null
        passwordError = null
        confirmPasswordError = null

        if (firstName.isBlank()) {
            firstNameError = "First name is required"
            isValid = false
        }
        if (lastName.isBlank()) {
            lastNameError = "Last name is required"
            isValid = false
        }
        if (email.isBlank()) {
            emailError = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Enter a valid email"
            isValid = false
        }
        if (phoneNumber.isBlank()) {
            phoneNumberError = "Phone number is required"
            isValid = false
        }
        if (address.isBlank()) {
            addressError = "Address is required"
            isValid = false
        }
        if (dateOfBirth.isBlank()) {
            dateOfBirthError = "Date of birth is required"
            isValid = false
        } else if (!isValidDateFormat(dateOfBirth)) {
            dateOfBirthError = "Use YYYY-MM-DD format"
            isValid = false
        }
        if (password.isBlank()) {
            passwordError = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            isValid = false
        }
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
        color = BehanceBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(top = 30.dp, start = 28.dp, end = 28.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "DocCur Logo",
                    modifier = Modifier
                        .height(38.dp)
                        .align(Alignment.CenterHorizontally)
                )



                Text(
                    text = "Join us to manage your health with ease.",
                    style = MaterialTheme.typography.subtitle1.copy(
                        color = BehanceTextSecondary,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp, top = 20.dp),
                    textAlign = TextAlign.Center
                )

                Row(Modifier.fillMaxWidth()) {
                    DocCurTextField(
                        value = firstName,
                        onValueChange = {
                            firstName = it
                            firstNameError = null
                        },
                        label = "First Name",
                        isError = firstNameError != null,
                        errorMessage = firstNameError,
                        leadingIcon = Icons.Outlined.Person,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    DocCurTextField(
                        value = lastName,
                        onValueChange = {
                            lastName = it
                            lastNameError = null
                        },
                        label = "Last Name",
                        isError = lastNameError != null,
                        errorMessage = lastNameError,
                        leadingIcon = Icons.Outlined.PersonOutline,
                        modifier = Modifier.weight(1f)
                    )
                }


                Spacer(modifier = Modifier.height(8.dp))

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
                    leadingIcon = Icons.Outlined.Email
                )

                Spacer(modifier = Modifier.height(8.dp))

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
                    leadingIcon = Icons.Outlined.Phone
                )

                Spacer(modifier = Modifier.height(8.dp))

                DocCurTextField(
                    value = address,
                    onValueChange = {
                        address = it
                        addressError = null
                    },
                    label = "Address",
                    isError = addressError != null,
                    errorMessage = addressError,
                    leadingIcon = Icons.Outlined.Home
                )

                Spacer(modifier = Modifier.height(8.dp))

                DocCurTextField(
                    value = dateOfBirth,
                    onValueChange = {
                        dateOfBirth = it
                        dateOfBirthError = null
                    },
                    label = "Date of Birth (YYYY-MM-DD)",
                    isError = dateOfBirthError != null,
                    errorMessage = dateOfBirthError,
                    leadingIcon = Icons.Outlined.DateRange
                )

                Spacer(modifier = Modifier.height(8.dp))

                DocCurPasswordField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                    },
                    label = "Password"
                )

                Spacer(modifier = Modifier.height(8.dp))

                DocCurPasswordField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPasswordError = null
                    },
                    label = "Confirm Password",
                    imeAction = ImeAction.Done,
                    onImeAction = { validateAndRegister() }
                )

                AnimatedVisibility(
                    visible = registrationState is Resource.Error &&
                            (firstNameError == null && lastNameError == null && emailError == null &&
                                    phoneNumberError == null && addressError == null && dateOfBirthError == null &&
                                    passwordError == null && confirmPasswordError == null),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = (registrationState as? Resource.Error)?.message ?: "Registration failed. Please try again.",
                        color = BehanceError,
                        style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp).fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                }

                Spacer(modifier = Modifier.height(if (registrationState is Resource.Error &&
                    (firstNameError == null && lastNameError == null && emailError == null &&
                            phoneNumberError == null && addressError == null && dateOfBirthError == null &&
                            passwordError == null && confirmPasswordError == null)) 8.dp else 32.dp))

                DocCurButton(
                    text = "Create Account",
                    onClick = { validateAndRegister() },
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = registrationState is Resource.Loading
                )

                Spacer(modifier = Modifier.height(20.dp))

                val annotatedText = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = BehanceTextSecondary, fontSize = 15.sp)) {
                        append("Already have an account? ")
                    }
                    pushStringAnnotation(tag = "LOGIN", annotation = "login")
                    withStyle(style = SpanStyle(color = BehancePrimaryBlue, fontWeight = FontWeight.Medium, fontSize = 15.sp)) {
                        append("Sign In")
                    }
                    pop()
                }
                ClickableText(
                    text = annotatedText,
                    onClick = { offset ->
                        annotatedText.getStringAnnotations(tag = "LOGIN", start = offset, end = offset)
                            .firstOrNull()?.let { onNavigateToLogin() }
                    },
                    modifier = Modifier.padding(bottom = 24.dp)
                )


            }

            if (registrationState is Resource.Loading) {
                LoadingIndicator()
            }
        }
    }
}