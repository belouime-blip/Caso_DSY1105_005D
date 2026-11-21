package com.example.dsy1105_005d_2026.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dsy1105_005d_2026.data.local.AppDatabase
import com.example.dsy1105_005d_2026.data.repository.AuthRepository
import com.example.dsy1105_005d_2026.data.repository.QrRepository

/**
 * Crea los ViewModels que reciben un repositorio por constructor
 * (Login, Registro, Perfil y Qr), conectando cada uno con su DAO de Room.
 */
class AppViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val db = AppDatabase.obtener(context.applicationContext)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel: ViewModel = when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(AuthRepository(db.usuarioDao()))
            modelClass.isAssignableFrom(RegistroViewModel::class.java) ->
                RegistroViewModel(AuthRepository(db.usuarioDao()))
            modelClass.isAssignableFrom(PerfilViewModel::class.java) ->
                PerfilViewModel(AuthRepository(db.usuarioDao()))
            modelClass.isAssignableFrom(QrViewModel::class.java) ->
                QrViewModel(QrRepository(db.escaneoDao()))
            else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
        return viewModel as T
    }
}
