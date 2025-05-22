package com.example.doccur.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doccur.R // For Google icon
import com.example.doccur.viewmodel.AuthViewModel


// Use colors defined in LoginScreen or ideally from Theme
val TrendyBlue = Color(0xFF0D6EFD)
val LightBlueGray = Color(0xFFE9F0FA) // For unfocused borders
val SoftText = Color(0xFF5B6B79)
val DarkText = Color(0xFF1D2329)
val ErrorBlue = Color(0xFF0A58CA)
val InputFieldBackground = Color.White // Or Color(0xFFF7F9FC) for a very subtle off-white

val AppButtonShape = RoundedCornerShape(12.dp)
val AppInputShape = RoundedCornerShape(10.dp)

@Composable
fun DocCurButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        backgroundColor = TrendyBlue,
        contentColor = Color.White,
        disabledBackgroundColor = TrendyBlue.copy(alpha = 0.7f)
    )
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = AppButtonShape,
        modifier = modifier.height(52.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        colors = colors,
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            hoveredElevation = 0.dp,
            focusedElevation = 0.dp
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = colors.contentColor(enabled = true).value, // Use button's content color
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.5.dp
                )
            } else {
                Text(text = text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun DocCurTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    leadingIcon: ImageVector? = null,
    onImeAction: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, style = MaterialTheme.typography.body2.copy(color = SoftText)) },
            singleLine = true,
            isError = isError,
            shape = AppInputShape,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                onDone = {
                    focusManager.clearFocus()
                    onImeAction()
                }
            ),
            leadingIcon = leadingIcon?.let {
                { Icon(imageVector = it, contentDescription = null, tint = SoftText) }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = DarkText,
                cursorColor = TrendyBlue,
                focusedBorderColor = TrendyBlue,
                unfocusedBorderColor = LightBlueGray,
                errorBorderColor = ErrorBlue,
                errorLabelColor = ErrorBlue,
                errorLeadingIconColor = ErrorBlue,
                leadingIconColor = SoftText,
                focusedLabelColor = TrendyBlue,
                unfocusedLabelColor = SoftText,
                backgroundColor = InputFieldBackground
            )
        )

        AnimatedVisibility(
            visible = isError && errorMessage != null,
            enter = fadeIn(animationSpec = tween(150)),
            exit = fadeOut(animationSpec = tween(150))
        ) {
            Text(
                text = errorMessage ?: "",
                color = ErrorBlue,
                style = MaterialTheme.typography.caption.copy(fontSize = 12.sp),
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun DocCurPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, style = MaterialTheme.typography.body2.copy(color = SoftText)) },
            singleLine = true,
            isError = isError,
            shape = AppInputShape,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                onDone = {
                    focusManager.clearFocus()
                    onImeAction()
                }
            ),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                val description = if (passwordVisible) "Hide password" else "Show password"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = description, tint = SoftText)
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = DarkText,
                cursorColor = TrendyBlue,
                focusedBorderColor = TrendyBlue,
                unfocusedBorderColor = LightBlueGray,
                errorBorderColor = ErrorBlue,
                errorLabelColor = ErrorBlue,
                errorTrailingIconColor = SoftText,
                trailingIconColor = SoftText,
                focusedLabelColor = TrendyBlue,
                unfocusedLabelColor = SoftText,
                backgroundColor = InputFieldBackground
            )
        )

        AnimatedVisibility(
            visible = isError && errorMessage != null,
            enter = fadeIn(animationSpec = tween(150)),
            exit = fadeOut(animationSpec = tween(150))
        ) {
            Text(
                text = errorMessage ?: "",
                color = ErrorBlue,
                style = MaterialTheme.typography.caption.copy(fontSize = 12.sp),
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Added padding so it doesn't stick to edges if content is small
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = TrendyBlue)
    }
}

@Composable
fun ErrorMessage(message: String, onRetry: () -> Unit) { // Keep if used elsewhere
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = ErrorBlue, // Using the distinct blue for errors
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        DocCurButton( // Using the styled button
            text = "Retry",
            onClick = onRetry
        )
    }
}

@Composable
fun GoogleLoginBut(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = AppButtonShape,
        border = BorderStroke(1.dp, LightBlueGray),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = DarkText,
            backgroundColor = Color.White // Or a very light blue gray
        )
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_google_logo), // Ensure you have this drawable
            contentDescription = "Google logo",
            modifier = Modifier.size(60.dp)
        )
        Text(
            text = "Sign in with Google",
            modifier = Modifier.padding(start = 12.dp),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = DarkText
        )
    }
}