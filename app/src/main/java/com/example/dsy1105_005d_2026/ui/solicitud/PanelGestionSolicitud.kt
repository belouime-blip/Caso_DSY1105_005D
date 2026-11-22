package com.example.dsy1105_005d_2026.ui.solicitud

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.dsy1105_005d_2026.data.local.SolicitudEntity
import com.example.dsy1105_005d_2026.data.model.Rol
import com.example.dsy1105_005d_2026.data.session.SesionManager
import com.example.dsy1105_005d_2026.util.SolicitudFlujo
import com.example.dsy1105_005d_2026.viewmodel.SolicitudViewModel

/**
 * Panel de gestión dentro del detalle de una solicitud. Lo que se ve depende del perfil:
 * - Administrador: define prioridad, asigna técnico y cierra/cancela.
 * - Técnico: pasa la solicitud a "En proceso" / "Resuelta" y sube fotos de avance.
 * - Cliente: confirma la resolución, pide reapertura y comparte la referencia por WhatsApp.
 * Todos pueden comentar y ver el historial.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanelGestionSolicitud(s: SolicitudEntity, vm: SolicitudViewModel) {
    val rol = SesionManager.rol
    val context = LocalContext.current
    val tecnicos by vm.tecnicos.collectAsState()
    val historial by remember(s.id) { vm.historial(s.id) }.collectAsState(initial = emptyList())

    var comentario by remember { mutableStateOf("") }
    var prioridad by remember(s.prioridad) { mutableStateOf(s.prioridad) }
    var tecnicoElegido by remember(s.tecnicoCorreo) { mutableStateOf(s.tecnicoCorreo.orEmpty()) }

    val tomarFoto = rememberTomarFoto { uri -> vm.comentar(s.id, "Foto de avance adjunta", uri) }

    val siguientes: List<String> =
        if (rol == null) emptyList<String>() else SolicitudFlujo.siguientes(s.estado, rol).toList()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "Gestión de la solicitud",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text("Prioridad: ${s.prioridad}")
            Text("Técnico asignado: ${s.tecnicoCorreo ?: "Sin asignar"}")
            Text("Cliente: ${s.clienteCorreo.ifEmpty { "-" }}")

            // ---------- Administrador: prioridad + asignar técnico ----------
            if (rol == Rol.ADMINISTRADOR && SolicitudFlujo.puedeAsignar(s.estado)) {
                HorizontalDivider()
                Text("Asignar técnico", fontWeight = FontWeight.Bold)
                SelectorOpciones(
                    etiqueta = "Prioridad",
                    valorMostrado = prioridad,
                    opciones = SolicitudFlujo.prioridades.map { it to it },
                    onElegir = { prioridad = it }
                )
                SelectorOpciones(
                    etiqueta = "Técnico",
                    valorMostrado = tecnicos.firstOrNull { it.correo == tecnicoElegido }
                        ?.let { "${it.nombre} ${it.apellido}" }.orEmpty(),
                    opciones = tecnicos.map { it.correo to "${it.nombre} ${it.apellido} (${it.correo})" },
                    onElegir = { tecnicoElegido = it }
                )
                Button(
                    onClick = { vm.asignar(s.id, tecnicoElegido, prioridad) },
                    enabled = tecnicoElegido.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Asignar y definir prioridad")
                }
            }

            // ---------- Cambios de estado permitidos para este perfil ----------
            if (siguientes.isNotEmpty()) {
                HorizontalDivider()
                Text("Cambiar estado", fontWeight = FontWeight.Bold)
                siguientes.forEach { nuevo ->
                    OutlinedButton(
                        onClick = { vm.cambiarEstado(s.id, nuevo) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(etiquetaAccion(s.estado, nuevo))
                    }
                }
            }

            // ---------- Compartir referencia (cliente) ----------
            if (rol == Rol.CLIENTE) {
                OutlinedButton(
                    onClick = { compartirPorWhatsApp(context, s) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Compartir referencia por WhatsApp")
                }
            }

            // ---------- Comentarios, evidencias e historial ----------
            HorizontalDivider()
            Text("Historial y comentarios", fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = comentario,
                onValueChange = { comentario = it },
                label = { Text("Escribe un comentario") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    vm.comentar(s.id, comentario)
                    comentario = ""
                },
                enabled = comentario.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar comentario")
            }
            OutlinedButton(
                onClick = tomarFoto,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tomar foto de avance")
            }

            if (historial.isEmpty()) {
                Text("Sin movimientos todavía", style = MaterialTheme.typography.bodySmall)
            }
            historial.forEach { h ->
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        "${formatoFecha(h.fecha)} · ${h.autor}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (h.texto.isNotBlank()) Text(h.texto)
                    h.fotoUri?.let { foto ->
                        AsyncImage(
                            model = foto,
                            contentDescription = "Evidencia",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }
        }
    }
}

private fun etiquetaAccion(actual: String, nuevo: String): String = when {
    actual == SolicitudFlujo.RESUELTA && nuevo == SolicitudFlujo.CERRADA -> "Cerrar (confirmar resolución)"
    nuevo == SolicitudFlujo.EN_REVISION && (actual == SolicitudFlujo.RESUELTA || actual == SolicitudFlujo.CERRADA) ->
        "Pedir reapertura"
    nuevo == SolicitudFlujo.EN_REVISION && actual == SolicitudFlujo.PENDIENTE_INFO -> "Enviar información / volver a revisión"
    nuevo == SolicitudFlujo.PENDIENTE_INFO -> "Solicitar información adicional"
    nuevo == SolicitudFlujo.CANCELADA -> "Cancelar solicitud"
    else -> "Pasar a: $nuevo"
}

/** Simula compartir el código de la solicitud por WhatsApp (si no está instalado, abre el selector). */
private fun compartirPorWhatsApp(context: Context, s: SolicitudEntity) {
    val texto = "Solicitud ${s.codigo}: ${s.titulo}. Estado actual: ${s.estado}."
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, texto)
    }
    try {
        context.startActivity(Intent(intent).setPackage("com.whatsapp"))
    } catch (e: ActivityNotFoundException) {
        context.startActivity(Intent.createChooser(intent, "Compartir referencia"))
    }
}

/** Lista desplegable simple. [opciones] son pares (valor, texto a mostrar). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorOpciones(
    etiqueta: String,
    valorMostrado: String,
    opciones: List<Pair<String, String>>,
    onElegir: (String) -> Unit
) {
    var abierto by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = abierto, onExpandedChange = { abierto = !abierto }) {
        OutlinedTextField(
            value = valorMostrado,
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = abierto) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            opciones.forEach { (valor, texto) ->
                DropdownMenuItem(
                    text = { Text(texto) },
                    onClick = {
                        onElegir(valor)
                        abierto = false
                    }
                )
            }
        }
    }
}
