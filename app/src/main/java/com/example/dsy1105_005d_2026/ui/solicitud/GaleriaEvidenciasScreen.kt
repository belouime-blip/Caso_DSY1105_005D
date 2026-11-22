package com.example.dsy1105_005d_2026.ui.solicitud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.dsy1105_005d_2026.viewmodel.SolicitudViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Galería interna: muestra las evidencias asociadas a solicitudes visibles para el usuario. */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun GaleriaEvidenciasScreen(
    onVolver: () -> Unit,
    vm: SolicitudViewModel = viewModel()
) {
    val solicitudes by vm.solicitudes.collectAsState()
    val escaneos by vm.escaneos().collectAsState(initial = emptyList())
    val evidencias = solicitudes.filter { !it.evidenciaUri.isNullOrBlank() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Galería de evidencias") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (evidencias.isEmpty() && escaneos.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Todavía no hay fotografías asociadas a tus solicitudes.")
                Text("Cuando tomes una foto y guardes la solicitud, aparecerá aquí.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(evidencias, key = { it.id }) { solicitud ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(solicitud.titulo, style = MaterialTheme.typography.titleMedium)
                            Text("Código: ${solicitud.codigo} · ${solicitud.estado}")
                            AsyncImage(
                                model = solicitud.evidenciaUri,
                                contentDescription = "Evidencia de ${solicitud.codigo}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth().height(210.dp).padding(top = 8.dp)
                            )
                            Text(
                                "Guardada en el almacenamiento privado de la aplicación (files/evidencias).",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                if (escaneos.isNotEmpty()) {
                    item {
                        Text("Historial de escaneos QR", style = MaterialTheme.typography.titleLarge)
                        Text("Los códigos leídos se guardan en la tabla escaneos de Room.")
                    }
                    items(escaneos, key = { "qr-${it.id}" }) { escaneo ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Código leído", style = MaterialTheme.typography.titleMedium)
                                Text(escaneo.contenido)
                                Text(
                                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                        .format(Date(escaneo.fecha)),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
