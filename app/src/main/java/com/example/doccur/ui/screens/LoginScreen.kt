package com.example.doccur.ui.screens

// ... (other imports remain the same)
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
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

// --- Accent for compact top ---
val SubtleTopAccentBrush = Brush.linearGradient(
    colors = listOf(Color(0xFF89CFF0).copy(alpha = 0.5f), Color(0xFFB0E0E6).copy(alpha = 0.1f)),
    start = Offset(0f, 0f),
    end = Offset(Float.POSITIVE_INFINITY, 0f)
)
val SoftLogoBackground = Color(0xFFE6F7FF) // Very pale blue, almost white

// --- Refined Color Palette (same as previous Behance attempt, good for clean look) ---
val BehancePrimaryBlue = Color(0xFF007AFF)
val BehanceTextPrimary = Color(0xFF1D1D1F)
val BehanceTextSecondary = Color(0xFF86868B)
val BehanceBackground = Color.White
val BehanceInputBorder = Color(0xFFDCDCE0)
val BehanceError = Color(0xFFFF3B30)


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

    fun performLogin(emailValue: String, passwordValue: String) {
        // ... (validation logic)
        var isValid = true; emailError = null; passwordError = null
        if (emailValue.isBlank()) { emailError = "Email is required"; isValid = false }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) { emailError = "Enter a valid email"; isValid = false }
        if (passwordValue.isBlank()) { passwordError = "Password is required"; isValid = false }
        else if (passwordValue.length < 6) { passwordError = "Password must be at least 6 characters"; isValid = false }
        if (isValid) viewModel.login(emailValue, passwordValue)
    }

    LaunchedEffect(loginState) {
        if (loginState is Resource.Success) onLoginSuccess()
    }

    Surface(
        color = BehanceBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        // Main column for the entire screen, allowing scroll
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState) // Scroll the whole content if it overflows
                .padding(top = 40.dp, start = 28.dp, end = 28.dp, bottom = 20.dp), // Consistent padding
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- Compact Top Section ---
            Row( // Logo and a potential small accent
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp, top = 24.dp), // Space after logo row
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // Puts logo left, accent right (or vice-versa)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo), // YOUR BLUE LOGO
                    contentDescription = "DocCur Logo",
                    modifier = Modifier
                        .height(30.dp) // Clean, not too large logo
                        .weight(1f),
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center
                )


            }


            Text(
                text = "Welcome Back \uD83D\uDC4B",
                style = MaterialTheme.typography.h6.copy( // Or h5 for slightly smaller
                    fontWeight = FontWeight.Bold, // Strong but not overly heavy
                    color = BehanceTextPrimary,
                    fontSize = 30.sp
                ),
                modifier = Modifier
                    .fillMaxWidth() // Take full width to allow textAlign
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Center// Align to start
            )

            Text(
                text = "Please sign in to access your account.",
                style = MaterialTheme.typography.subtitle1.copy(
                    color = BehanceTextSecondary,
                    fontSize = 17.sp
                ),
                modifier = Modifier
                    .fillMaxWidth() // Take full width
                    .padding(bottom = 36.dp), // Good space before form
                textAlign = TextAlign.Center // Align to start
            )

            // --- Form Elements ---
            DocCurTextField(
                value = email,
                onValueChange = { email = it; emailError = null },
                label = "Email Address",
                isError = emailError != null,
                errorMessage = emailError,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                leadingIcon = Icons.Outlined.Email
            )

            Spacer(modifier = Modifier.height(22.dp))

            DocCurPasswordField(
                value = password,
                onValueChange = { password = it; passwordError = null },
                label = "Password",
                isError = passwordError != null,
                errorMessage = passwordError,
                imeAction = ImeAction.Done,
                onImeAction = { performLogin(email, password) }
            )

            AnimatedVisibility(
                visible = loginState is Resource.Error && (emailError == null && passwordError == null),
                enter = fadeIn(), exit = fadeOut()
            ) {
                Text(
                    text = (loginState as? Resource.Error)?.message ?: "Login failed. Please check your credentials.",
                    color = BehanceError,
                    style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp).fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }


            Spacer(modifier = Modifier.height(if (loginState is Resource.Error && (emailError == null && passwordError == null)) 8.dp else 32.dp))

            DocCurButton(
                text = "Sign In",
                onClick = { performLogin(email, password) },
                modifier = Modifier.fillMaxWidth(),
                isLoading = loginState is Resource.Loading
            )

            Spacer(modifier = Modifier.height(30.dp))

            val annotatedText = buildAnnotatedString {
                withStyle(style = SpanStyle(color = BehanceTextSecondary, fontSize = 15.sp)) {
                    append("Don't have an account? ")
                }
                pushStringAnnotation(tag = "REGISTER", annotation = "register")
                withStyle(style = SpanStyle(color = BehancePrimaryBlue, fontWeight = FontWeight.Medium, fontSize = 15.sp)) {
                    append("Register")
                }
                pop()
            }
            ClickableText(
                text = annotatedText,
                onClick = { offset ->
                    annotatedText.getStringAnnotations(tag = "REGISTER", start = offset, end = offset)
                        .firstOrNull()?.let { onNavigateToRegister() }
                },
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Optional: "OR" separator - can be removed for ultimate minimalism
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Divider(modifier = Modifier.weight(1f), color = BehanceInputBorder.copy(alpha = 0.5f))
                Text("  or  ", style = MaterialTheme.typography.caption.copy(color = BehanceTextSecondary))
                Divider(modifier = Modifier.weight(1f), color = BehanceInputBorder.copy(alpha = 0.5f))
            }

            GoogleLoginButton(
                viewModel = viewModel,
                onLoginSuccess = onLoginSuccess,
            )

            Spacer(modifier = Modifier.height(20.dp)) // Space at the very bottom
        }

        if (loginState is Resource.Loading) {
            LoadingIndicator()
        }
    }
}