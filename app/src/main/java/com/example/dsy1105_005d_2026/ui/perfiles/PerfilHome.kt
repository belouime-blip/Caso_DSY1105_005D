package com.example.dsy1105_005d_2026.ui.perfiles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
<<<<<<< HEAD
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
=======
>>>>>>> c1ae68d71e4def1f1aa11c0b38ba3da892369171
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
<<<<<<< HEAD
=======
import androidx.compose.runtime.getValue
>>>>>>> c1ae68d71e4def1f1aa11c0b38ba3da892369171
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dsy1105_005d_2026.data.local.SolicitudEntity
import com.example.dsy1105_005d_2026.data.model.Rol
import com.example.dsy1105_005d_2026.data.session.SesionManager
import com.example.dsy1105_005d_2026.ui.theme.ColoresApp
import com.example.dsy1105_005d_2026.util.SolicitudFlujo

/** Una opción (tarjeta) del menú de inicio de un perfil. */
data class OpcionHome(
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector,
    val onClick: () -> Unit
)

/**
 * Plantilla común de la página de inicio de cada perfil.
 * Cada perfil (cliente, administrador, técnico) la usa con sus propias opciones.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilHome(
    titulo: String,
    rol: Rol,
    opciones: List<OpcionHome>,
    onCerrarSesion: () -> Unit,
    contenidoExtra: @Composable () -> Unit = {}
) {
    val usuario by SesionManager.usuario.collectAsState()
<<<<<<< HEAD
    val context = LocalContext.current
    var confirmarSalida by remember { mutableStateOf(false) }
=======
>>>>>>> c1ae68d71e4def1f1aa11c0b38ba3da892369171

    MaterialTheme(colorScheme = ColoresApp) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = { Text(titulo) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Hola, ${usuario?.nombre.orEmpty()} ${usuario?.apellido.orEmpty()}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Perfil: ${rol.etiqueta}",
                    style = MaterialTheme.typography.bodyLarge
                )

                contenidoExtra()

                opciones.forEach { opcion -> OpcionCard(opcion) }

                OutlinedButton(
                    onClick = onCerrarSesion,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Cerrar sesión")
                }
<<<<<<< HEAD

                OutlinedButton(
                    onClick = { confirmarSalida = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Salir de la aplicación")
                }

                if (confirmarSalida) {
                    AlertDialog(
                        onDismissRequest = { confirmarSalida = false },
                        title = { Text("Salir de la aplicación") },
                        text = { Text("¿Deseas cerrar completamente la aplicación?") },
                        confirmButton = {
                            TextButton(onClick = {
                                confirmarSalida = false
                                (context as? Activity)?.finishAffinity()
                            }) { Text("Salir") }
                        },
                        dismissButton = {
                            TextButton(onClick = { confirmarSalida = false }) { Text("Cancelar") }
                        }
                    )
                }
=======
>>>>>>> c1ae68d71e4def1f1aa11c0b38ba3da892369171
            }
        }
    }
}

@Composable
private fun OpcionCard(opcion: OpcionHome) {
    Card(
        onClick = opcion.onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = opcion.icono,
                contentDescription = opcion.titulo,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = opcion.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = opcion.descripcion,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/** Panel resumen: cuántas solicitudes hay en cada estado. */
@Composable
fun ResumenEstados(titulo: String, solicitudes: List<SolicitudEntity>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "$titulo (${solicitudes.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            SolicitudFlujo.todosLosEstados.forEach { estado ->
                val cantidad = solicitudes.count { it.estado == estado }
                Text("$estado: $cantidad", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
