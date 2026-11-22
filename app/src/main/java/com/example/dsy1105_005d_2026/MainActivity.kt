package com.example.dsy1105_005d_2026

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.core.view.WindowCompat
import android.graphics.Color
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.dsy1105_005d_2026.ui.navigation.AppNavigation

class MainActivity : FragmentActivity() {

    // Permiso para los avisos locales (Android 13 o superior)
    private val pedirPermisoAvisos =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No ocultar la barra de navegación del sistema: Android controla Atrás, Inicio y Recientes.
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.navigationBarColor = Color.BLACK
        if (Build.VERSION.SDK_INT >= 33) {
            pedirPermisoAvisos.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent { AppNavigation() }
    }
}
