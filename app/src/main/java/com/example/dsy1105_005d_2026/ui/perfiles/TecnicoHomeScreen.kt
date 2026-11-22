package com.example.dsy1105_005d_2026.ui.perfiles

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
<<<<<<< HEAD
import androidx.compose.material.icons.filled.PhotoLibrary
=======
>>>>>>> c1ae68d71e4def1f1aa11c0b38ba3da892369171
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dsy1105_005d_2026.data.model.Rol
import com.example.dsy1105_005d_2026.viewmodel.SolicitudViewModel

/** Página del perfil TÉCNICO: solo ve las solicitudes que le asignaron y registra avances. */
@Composable
fun TecnicoHomeScreen(
    onMisAsignadas: () -> Unit,
    onLectorQr: () -> Unit,
    onPerfil: () -> Unit,
<<<<<<< HEAD
    onGaleria: () -> Unit,
=======
>>>>>>> c1ae68d71e4def1f1aa11c0b38ba3da892369171
    onCerrarSesion: () -> Unit,
    vm: SolicitudViewModel = viewModel()
) {
    val solicitudes by vm.solicitudes.collectAsState()

    PerfilHome(
        titulo = "Inicio · Técnico",
        rol = Rol.TECNICO,
        opciones = listOf(
            OpcionHome("Mis solicitudes asignadas", "Ver antecedentes, registrar avances y evidencias", Icons.Default.Build, onMisAsignadas),
            OpcionHome("Lector QR", "Escanear el código de una solicitud", Icons.Default.QrCodeScanner, onLectorQr),
<<<<<<< HEAD
            OpcionHome("Mi perfil", "Ver y editar mis datos", Icons.Default.Person, onPerfil),
            OpcionHome("Galería de evidencias", "Consultar fotos guardadas en las solicitudes", Icons.Default.PhotoLibrary, onGaleria)
=======
            OpcionHome("Mi perfil", "Ver y editar mis datos", Icons.Default.Person, onPerfil)
>>>>>>> c1ae68d71e4def1f1aa11c0b38ba3da892369171
        ),
        onCerrarSesion = onCerrarSesion,
        contenidoExtra = { ResumenEstados("Mis asignadas", solicitudes) }
    )
}
