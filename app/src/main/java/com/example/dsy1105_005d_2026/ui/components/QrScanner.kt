package com.example.dsy1105_005d_2026.ui.components

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

private const val TAG = "QrScanner"

/**
 * Cámara con lectura de códigos QR (CameraX + ML Kit).
 * Llama a [onQrCodeScanned] una sola vez por cada vez que el composable entra en pantalla.
 * Para volver a escanear basta con sacarlo y volver a mostrarlo.
 */
@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun QrScanner(
    onQrCodeScanned: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Mantener actualizado el callback sin reiniciar CameraX
    val currentCallback = rememberUpdatedState(onQrCodeScanned)

    // Una única vista previa
    val previewView = remember(context) {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            // Mayor compatibilidad con emuladores
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier.fillMaxSize()
    )

    // La cámara se gestiona fuera de AndroidView.update
    DisposableEffect(context, lifecycleOwner, previewView) {
        val cameraExecutor = Executors.newSingleThreadExecutor()

        // ML Kit configurado solo para QR
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        val barcodeScanner = BarcodeScanning.getClient(options)

        val disposed = AtomicBoolean(false)
        val detected = AtomicBoolean(false)

        val preview = Preview.Builder().build().apply {
            setSurfaceProvider(previewView.surfaceProvider)
        }

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
            val mediaImage = imageProxy.image

            if (disposed.get() || detected.get() || mediaImage == null) {
                imageProxy.close()
                return@setAnalyzer
            }

            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            try {
                barcodeScanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        val valor = barcodes.firstOrNull {
                            it.format == Barcode.FORMAT_QR_CODE && it.rawValue != null
                        }?.rawValue

                        if (valor != null && !disposed.get() && detected.compareAndSet(false, true)) {
                            imageAnalysis.clearAnalyzer()
                            currentCallback.value(valor)
                            Log.d(TAG, "Código QR detectado")
                        }
                    }
                    .addOnFailureListener { error ->
                        Log.e(TAG, "Error al analizar código QR", error)
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } catch (error: Exception) {
                Log.e(TAG, "Error al procesar imagen", error)
                imageProxy.close()
            }
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        var boundProvider: ProcessCameraProvider? = null

        cameraProviderFuture.addListener({
            if (disposed.get()) return@addListener
            try {
                val cameraProvider = cameraProviderFuture.get()
                if (!disposed.get()) {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )
                    boundProvider = cameraProvider
                    Log.d(TAG, "CameraX vinculado correctamente")
                }
            } catch (error: Exception) {
                Log.e(TAG, "Error al iniciar la cámara", error)
            }
        }, ContextCompat.getMainExecutor(context))

        // Liberar recursos al salir de la pantalla
        onDispose {
            disposed.set(true)
            imageAnalysis.clearAnalyzer()
            boundProvider?.unbind(preview, imageAnalysis)
            barcodeScanner.close()
            cameraExecutor.shutdown()
            Log.d(TAG, "Recursos de cámara liberados")
        }
    }
}
