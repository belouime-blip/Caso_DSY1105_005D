package com.example.dsy1105_005d_2026.ui.solicitud

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.dsy1105_005d_2026.viewmodel.SolicitudViewModel
import java.io.File

val categorias = listOf("Reclamo", "Consulta", "Sugerencia", "Soporte")
private val servicios = listOf("Instalación", "Mantención", "Soporte técnico", "Facturación")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudFormScreen(
    id: Int,
    onGuardado: () -> Unit,
    vm: SolicitudViewModel = viewModel()
) {
    val state by vm.uiState.collectAsState()
    val context = LocalContext.current
    var fotoPendiente by rememberSaveable { mutableStateOf<String?>(null) }

    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        if (ok) vm.onEvidenciaChange(fotoPendiente)
    }

    val tomarFoto = {
        val carpeta = File(context.filesDir, "evidencias").apply { mkdirs() }
        val archivo = File(carpeta, "ev_${System.currentTimeMillis()}.jpg")
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivo
        )
        fotoPendiente = uri.toString()
        camara.launch(uri)
    }

    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) tomarFoto()
    }

    LaunchedEffect(id) { vm.cargar(id) }
    LaunchedEffect(state.guardadoExitosamente) {
        if (state.guardadoExitosamente) onGuardado()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (id == 0) "Nueva solicitud" else "Editar solicitud") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (state.codigo.isNotEmpty()) {
                Text("Código: ${state.codigo}")
                Text("Estado: ${state.estado}")
                Text("Fecha: ${formatoFecha(state.fecha)}")
            }

            OutlinedTextField(
                value = state.titulo,
                onValueChange = vm::onTituloChange,
                label = { Text("Título") },
                isError = state.tituloError != null,
                supportingText = { Text(state.tituloError ?: "") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.descripcion,
                onValueChange = vm::onDescripcionChange,
                label = { Text("Descripción") },
                isError = state.descripcionError != null,
                supportingText = { Text(state.descripcionError ?: "") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Selector("Categoría", state.categoria, categorias, state.categoriaError, vm::onCategoriaChange)
            Selector("Servicio relacionado", state.servicio, servicios, state.servicioError, vm::onServicioChange)

            OutlinedButton(
                onClick = {
                    val concedido = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                    if (concedido) tomarFoto() else pedirPermiso.launch(Manifest.permission.CAMERA)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.evidenciaUri == null) "Tomar foto de evidencia" else "Cambiar foto")
            }

            state.evidenciaUri?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "Evidencia",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(200.dp).padding(top = 8.dp)
                )
            }

            Button(
                onClick = vm::guardar,
                enabled = !state.guardando,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text(if (state.guardando) "Guardando..." else "Guardar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Selector(
    label: String,
    valor: String,
    opciones: List<String>,
    error: String?,
    onElegir: (String) -> Unit
) {
    var abierto by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = abierto, onExpandedChange = { abierto = !abierto }) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = abierto) },
            isError = error != null,
            supportingText = { Text(error ?: "") },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            opciones.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onElegir(it)
                        abierto = false
                    }
                )
            }
        }
    }
}
