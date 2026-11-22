package com.example.dsy1105_005d_2026.ui.solicitud

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

/**
 * Devuelve una función que pide el permiso de cámara (si falta), abre la cámara y guarda
 * la foto en filesDir/evidencias (por el FileProvider). Al terminar entrega la URI a [onFoto].
 */
@Composable
fun rememberTomarFoto(onFoto: (String) -> Unit): () -> Unit {
    val context = LocalContext.current
    var pendiente by rememberSaveable { mutableStateOf<String?>(null) }
    val onFotoActual by rememberUpdatedState(onFoto)

    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        val uri = pendiente
        if (ok && uri != null) onFotoActual(uri)
    }

    val lanzarCamara: () -> Unit = {
        val carpeta = File(context.filesDir, "evidencias").apply { mkdirs() }
        val archivo = File(carpeta, "ev_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivo
        )
        pendiente = uri.toString()
        camara.launch(uri)
    }

    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) lanzarCamara()
    }

    return {
        val concedido = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (concedido) lanzarCamara() else pedirPermiso.launch(Manifest.permission.CAMERA)
    }
}
