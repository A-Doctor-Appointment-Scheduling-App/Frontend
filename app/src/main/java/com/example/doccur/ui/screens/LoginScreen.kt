package com.example.doccur.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.doccur.ui.components.DocCurButton
import com.example.doccur.ui.components.DocCurPasswordField
import com.example.doccur.ui.components.DocCurTextField
import com.example.doccur.ui.components.GoogleLoginButton
import com.example.doccur.ui.components.LoadingIndicator
import com.example.doccur.ui.theme.Blue
import com.example.doccur.util.Resource
import com.example.doccur.viewmodel.AuthViewModel
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val loginState by viewModel.loginState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    // Define the performLogin function inside the composable function
    fun performLogin(email: String, password: String) {
        var isValid = true

        if (email.isBlank()) {
            emailError = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Enter a valid email"
            isValid = false
        }

        if (password.isBlank()) {
            passwordError = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Password should be at least 6 characters"
            isValid = false
        }

        if (isValid) {

            viewModel.login(email, password)
        }
    }

    LaunchedEffect(loginState) {
        if (loginState is Resource.Success) {
            onLoginSuccess()
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
                Spacer(modifier = Modifier.height(32.dp))

                // Logo/Icon would go here
                // For now, using a placeholder text
                Text(
                    text = "DocCur",
                    style = MaterialTheme.typography.h1,
                    color = Blue
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Healthcare at your fingertips",
                    style = MaterialTheme.typography.subtitle1
                )

                Spacer(modifier = Modifier.height(48.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Login",
                            style = MaterialTheme.typography.h2
                        )

                        Spacer(modifier = Modifier.height(24.dp))

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
                            imeAction = ImeAction.Next,
                            leadingIcon = Icons.Default.Email
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        DocCurPasswordField(
                            value = password,
                            onValueChange = {
                                password = it
                                passwordError = null
                            },
                            label = "Password",
                            isError = passwordError != null,
                            errorMessage = passwordError,
                            imeAction = ImeAction.Done,
                            onImeAction = { }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        AnimatedVisibility(
                            visible = loginState is Resource.Error,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Text(
                                text = (loginState as? Resource.Error)?.message ?: "An error occurred",
                                color = MaterialTheme.colors.error,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        DocCurButton(
                            text = "Login",
                            onClick = { performLogin(email, password) }, // Call performLogin function here
                            modifier = Modifier.fillMaxWidth(),
                            isLoading = loginState is Resource.Loading
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Don't have an account? ",
                        style = MaterialTheme.typography.body2
                    )
                    TextButton(onClick = onNavigateToRegister) {
                        Text("Register as Patient")
                    }
                }
                GoogleLoginButton(
                    viewModel = viewModel,
                    onLoginSuccess = onLoginSuccess // ou onRegisterSuccess
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (loginState is Resource.Loading) {
                LoadingIndicator()
            }
        }
    }
}

