package com.example.dsy1105_005d_2026

import android.Manifest
import android.os.Build
import android.os.Bundle
<<<<<<< HEAD
import androidx.fragment.app.FragmentActivity
import androidx.core.view.WindowCompat
import android.graphics.Color
=======
import androidx.activity.ComponentActivity
>>>>>>> c1ae68d71e4def1f1aa11c0b38ba3da892369171
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.dsy1105_005d_2026.ui.navigation.AppNavigation

<<<<<<< HEAD
class MainActivity : FragmentActivity() {
=======
class MainActivity : ComponentActivity() {
>>>>>>> c1ae68d71e4def1f1aa11c0b38ba3da892369171

    // Permiso para los avisos locales (Android 13 o superior)
    private val pedirPermisoAvisos =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
<<<<<<< HEAD
        // No ocultar la barra de navegación del sistema: Android controla Atrás, Inicio y Recientes.
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.navigationBarColor = Color.BLACK
=======
>>>>>>> c1ae68d71e4def1f1aa11c0b38ba3da892369171
        if (Build.VERSION.SDK_INT >= 33) {
            pedirPermisoAvisos.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent { AppNavigation() }
    }
}
