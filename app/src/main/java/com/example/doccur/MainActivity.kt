package com.example.doccur


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.doccur.api.RetrofitClient
import com.example.doccur.navigation.DocNavGraph
import com.example.doccur.navigation.PatientNavGraph
import com.example.doccur.repositories.NotificationRepository
import com.example.doccur.ui.components.DocBottomBar
import com.example.doccur.ui.components.PatientBottomBar
import com.example.doccur.ui.theme.DoccurTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create repository and view model factory
        val repository = NotificationRepository(RetrofitClient.apiService)

        setContent {
            DoccurTheme{
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen(repository)
                }
            }
        }
    }
}

@Composable
fun MainScreen(repository: NotificationRepository) {
    val navController = rememberNavController()
    val userType = "doctor"

    if (userType === "patient"){
        Scaffold(
            bottomBar = {
                PatientBottomBar(navController)
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                PatientNavGraph(navController, repository)
            }
        }
    }else if (userType === "doctor"){
        Scaffold(
            bottomBar = {
                DocBottomBar(navController)
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                DocNavGraph(navController, repository)
            }
        }
    }


}

