package com.example.dsy1105_005d_2026.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dsy1105_005d_2026.data.session.SesionManager
import com.example.dsy1105_005d_2026.ui.catalogo.PostScreen
import com.example.dsy1105_005d_2026.ui.perfiles.AdminHomeScreen
import com.example.dsy1105_005d_2026.ui.perfiles.ClienteHomeScreen
import com.example.dsy1105_005d_2026.ui.perfiles.TecnicoHomeScreen
import com.example.dsy1105_005d_2026.ui.perfil.PerfilScreen
import com.example.dsy1105_005d_2026.ui.qr.LectorQrScreen
import com.example.dsy1105_005d_2026.ui.registro.RegistroScreen
import com.example.dsy1105_005d_2026.ui.solicitud.DetalleSolicitudScreen
import com.example.dsy1105_005d_2026.ui.solicitud.ListaSolicitudesScreen
<<<<<<< HEAD
import com.example.dsy1105_005d_2026.ui.solicitud.GaleriaEvidenciasScreen
=======
>>>>>>> c1ae68d71e4def1f1aa11c0b38ba3da892369171
import com.example.dsy1105_005d_2026.ui.solicitud.QrScannerScreen
import com.example.dsy1105_005d_2026.ui.solicitud.SolicitudFormScreen
import com.example.dsy1105_005d_2026.ui.theme.HomeScreen

// Cierra la sesión y vuelve al login limpiando toda la pila de navegación
private fun cerrarSesion(navController: NavController) {
    SesionManager.cerrar()
    navController.navigate("login") {
        popUpTo(navController.graph.id) { inclusive = true }
        launchSingleTop = true
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        // ---------- Login y registro ----------
        composable("login") {
            HomeScreen(navController = navController)
        }

        composable("registro") {
            RegistroScreen(onRegistrado = { navController.popBackStack() })
        }

        // ---------- Una página por perfil (el login navega aquí según el rol) ----------
        composable("cliente_home") {
            ClienteHomeScreen(
                onNuevaSolicitud = { navController.navigate("form/0") },
                onMisSolicitudes = { navController.navigate("lista") },
                onCatalogo = { navController.navigate("catalogo") },
                onLectorQr = { navController.navigate("lector_qr") },
                onPerfil = { navController.navigate("perfil") },
<<<<<<< HEAD
                onGaleria = { navController.navigate("galeria") },
=======
>>>>>>> c1ae68d71e4def1f1aa11c0b38ba3da892369171
                onCerrarSesion = { cerrarSesion(navController) }
            )
        }

        composable("admin_home") {
            AdminHomeScreen(
                onTodasLasSolicitudes = { navController.navigate("lista") },
                onLectorQr = { navController.navigate("lector_qr") },
                onPerfil = { navController.navigate("perfil") },
<<<<<<< HEAD
                onGaleria = { navController.navigate("galeria") },
=======
>>>>>>> c1ae68d71e4def1f1aa11c0b38ba3da892369171
                onCerrarSesion = { cerrarSesion(navController) }
            )
        }

        composable("tecnico_home") {
            TecnicoHomeScreen(
                onMisAsignadas = { navController.navigate("lista") },
                onLectorQr = { navController.navigate("lector_qr") },
                onPerfil = { navController.navigate("perfil") },
<<<<<<< HEAD
                onGaleria = { navController.navigate("galeria") },
=======
>>>>>>> c1ae68d71e4def1f1aa11c0b38ba3da892369171
                onCerrarSesion = { cerrarSesion(navController) }
            )
        }

        composable("perfil") { PerfilScreen() }

<<<<<<< HEAD
        composable("galeria") {
            GaleriaEvidenciasScreen(onVolver = { navController.popBackStack() })
        }

=======
>>>>>>> c1ae68d71e4def1f1aa11c0b38ba3da892369171
        composable("catalogo") { PostScreen() }

        // ---------- Solicitudes (la lista se filtra sola según el perfil) ----------
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
