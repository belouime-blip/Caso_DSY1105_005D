package com.example.dsy1105_005d_2026.ui.perfiles

import androidx.compose.material.icons.Icons
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

/** Página del perfil ADMINISTRADOR: ve todas las solicitudes, define prioridad y asigna técnico. */
@Composable
fun AdminHomeScreen(
    onTodasLasSolicitudes: () -> Unit,
    onLectorQr: () -> Unit,
    onPerfil: () -> Unit,
    onGaleria: () -> Unit,
    onCerrarSesion: () -> Unit,
    vm: SolicitudViewModel = viewModel()
) {
    val solicitudes by vm.solicitudes.collectAsState()

    PerfilHome(
        titulo = "Inicio · Administrador",
        rol = Rol.ADMINISTRADOR,
        opciones = listOf(
            OpcionHome("Gestión de solicitudes", "Revisar, priorizar, asignar técnico, cerrar o cancelar", Icons.Default.Description, onTodasLasSolicitudes),
            OpcionHome("Lector QR", "Escanear el código de una solicitud", Icons.Default.QrCodeScanner, onLectorQr),
            OpcionHome("Mi perfil", "Ver y editar mis datos", Icons.Default.Person, onPerfil),
            OpcionHome("Galería de evidencias", "Consultar fotos guardadas en las solicitudes", Icons.Default.PhotoLibrary, onGaleria)
        ),
        onCerrarSesion = onCerrarSesion,
        contenidoExtra = { ResumenEstados("Panel resumen", solicitudes) }
    )
}
