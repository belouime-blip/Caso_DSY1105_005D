package com.example.dsy1105_005d_2026.data.session

import com.example.dsy1105_005d_2026.data.local.UsuarioEntity
import com.example.dsy1105_005d_2026.data.model.Rol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Guarda quién inició sesión. De aquí salen el perfil (rol) y el filtro de solicitudes. */
object SesionManager {
    private val _usuario = MutableStateFlow<UsuarioEntity?>(null)
    val usuario: StateFlow<UsuarioEntity?> = _usuario.asStateFlow()

    val rol: Rol? get() = _usuario.value?.let { Rol.desde(it.rol) }

    fun iniciar(usuario: UsuarioEntity) {
        _usuario.value = usuario
    }

    fun cerrar() {
        _usuario.value = null
    }
}
