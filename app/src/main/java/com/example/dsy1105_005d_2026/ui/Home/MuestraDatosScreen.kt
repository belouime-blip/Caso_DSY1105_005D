package com.example.dsy1105_005d_2026.ui.Home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuestraDatosScreen(
    username: String,
    navController: NavController
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Menú") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Bienvenido, $username",
                style = MaterialTheme.typography.headlineMedium
            )

            Button(onClick = { navController.navigate("lista") }) {
                Text("Solicitudes")
            }

            Button(onClick = { navController.navigate("lector_qr") }) {
                Text("Lector QR")
            }

            OutlinedButton(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}
