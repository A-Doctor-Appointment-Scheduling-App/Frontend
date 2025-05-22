package com.example.doccur.ui.components

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.doccur.viewmodel.AuthViewModel
import com.example.doccur.util.Resource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun GoogleLoginButton(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("873784815709-450vlodet7bvpe1ds7ibn8fgiv2e7ii0.apps.googleusercontent.com")
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data = result.data
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                viewModel.loginWithGoogle(idToken)
            }
        } catch (e: ApiException) {
            Log.d("GoogleLoginButton", "Google sign-in failed: $e")
        }
    }

    val loginState = viewModel.loginState.collectAsState()
    LaunchedEffect(loginState.value) {
        if (loginState.value is Resource.Success) {
            onLoginSuccess()
        }
    }



    GoogleLoginBut(
onClick ={ launcher.launch(googleSignInClient.signInIntent) } ,
        modifier = Modifier.fillMaxWidth()
    )
}