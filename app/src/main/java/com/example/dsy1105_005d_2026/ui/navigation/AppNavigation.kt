package com.example.dsy1105_005d_2026.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dsy1105_005d_2026.ui.Home.MuestraDatosScreen
import com.example.dsy1105_005d_2026.ui.qr.LectorQrScreen
import com.example.dsy1105_005d_2026.ui.solicitud.DetalleSolicitudScreen
import com.example.dsy1105_005d_2026.ui.solicitud.ListaSolicitudesScreen
import com.example.dsy1105_005d_2026.ui.solicitud.QrScannerScreen
import com.example.dsy1105_005d_2026.ui.solicitud.SolicitudFormScreen
import com.example.dsy1105_005d_2026.ui.theme.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            HomeScreen(navController = navController)
        }

        composable(
            route = "muestraDatos/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { entry ->
            val username = entry.arguments?.getString("username").orEmpty()
            MuestraDatosScreen(username = username, navController = navController)
        }

        composable("lista") {
            ListaSolicitudesScreen(
                onAgregar = { navController.navigate("form/0") },
                onSeleccionar = { navController.navigate("detalle/$it") },
                onEscanear = { navController.navigate("qr") }
            )
        }

        composable(
            route = "detalle/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            DetalleSolicitudScreen(
                id = entry.arguments?.getInt("id") ?: 0,
                onEditar = { navController.navigate("form/$it") },
                onEliminado = { navController.popBackStack() }
            )
        }

        composable("qr") {
            QrScannerScreen(
                onEncontrado = { id ->
                    navController.navigate("detalle/$id") {
                        popUpTo("qr") { inclusive = true }
                    }
                }
            )
        }

        composable("lector_qr") {
            LectorQrScreen()
        }

        composable(
            route = "form/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            SolicitudFormScreen(
                id = entry.arguments?.getInt("id") ?: 0,
                onGuardado = { navController.popBackStack() }
            )
        }
    }
}
