package com.example.dsy1105_005d_2026.ui.perfiles

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dsy1105_005d_2026.data.model.Rol
import com.example.dsy1105_005d_2026.viewmodel.SolicitudViewModel

/** Página del perfil CLIENTE: crea solicitudes y hace seguimiento de las suyas. */
@Composable
fun ClienteHomeScreen(
    onNuevaSolicitud: () -> Unit,
    onMisSolicitudes: () -> Unit,
    onCatalogo: () -> Unit,
    onLectorQr: () -> Unit,
    onPerfil: () -> Unit,
    onGaleria: () -> Unit,
    onCerrarSesion: () -> Unit,
    vm: SolicitudViewModel = viewModel()
) {
    val solicitudes by vm.solicitudes.collectAsState()

    PerfilHome(
        titulo = "Inicio · Cliente",
        rol = Rol.CLIENTE,
        opciones = listOf(
            OpcionHome("Nueva solicitud", "Crear una solicitud con foto de evidencia", Icons.Default.Add, onNuevaSolicitud),
            OpcionHome("Mis solicitudes", "Seguimiento, estado, historial y comentarios", Icons.Default.Description, onMisSolicitudes),
            OpcionHome("Catálogo de servicios", "Servicios disponibles (API externa)", Icons.Default.Category, onCatalogo),
            OpcionHome("Lector QR", "Escanear el código de una solicitud", Icons.Default.QrCodeScanner, onLectorQr),
            OpcionHome("Mi perfil", "Ver y editar mis datos", Icons.Default.Person, onPerfil),
            OpcionHome("Galería de evidencias", "Consultar fotos guardadas en las solicitudes", Icons.Default.PhotoLibrary, onGaleria)
        ),
        onCerrarSesion = onCerrarSesion,
        contenidoExtra = { ResumenEstados("Mis solicitudes", solicitudes) }
    )
}
