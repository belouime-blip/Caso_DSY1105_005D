package com.example.dsy1105_005d_2026.ui.solicitud

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dsy1105_005d_2026.ui.components.QrScanner
import com.example.dsy1105_005d_2026.util.CameraPermissionHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScannerScreen(
    onEncontrado: (Int) -> Unit,
    vm: SolicitudViewModel = viewModel()
) {
    val context = LocalContext.current
    var permiso by remember { mutableStateOf(CameraPermissionHelper.hasCameraPermission(context)) }
    var codigoNoRegistrado by remember { mutableStateOf<String?>(null) }

    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        permiso = it
    }

    LaunchedEffect(Unit) {
        if (!permiso) pedirPermiso.launch(Manifest.permission.CAMERA)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Leer código QR") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val noRegistrado = codigoNoRegistrado

            when {
                !permiso -> {
                    Text(
                        "Se necesita permiso de cámara para leer el QR",
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Button(onClick = { pedirPermiso.launch(Manifest.permission.CAMERA) }) {
                        Text("Permitir cámara")
                    }
                }

                noRegistrado != null -> {
                    Text(
                        "Código no registrado: $noRegistrado",
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Button(
                        onClick = { codigoNoRegistrado = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Escanear de nuevo")
                    }
                }

                else -> {
                    Text(
                        "Apunta la cámara al QR de una solicitud",
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    QrScanner(
                        onQrCodeScanned = { valor ->
                            vm.buscarPorCodigo(valor) { id ->
                                if (id != null) {
                                    onEncontrado(id)
                                } else {
                                    codigoNoRegistrado = valor
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(360.dp)
                    )
                }
            }
        }
    }
}
