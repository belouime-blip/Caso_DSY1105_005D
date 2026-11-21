package com.example.dsy1105_005d_2026.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_005d_2026.data.model.Rol
import com.example.dsy1105_005d_2026.data.repository.AuthRepository
import com.example.dsy1105_005d_2026.data.session.SesionManager
import com.example.dsy1105_005d_2026.util.Validaciones
import kotlinx.coroutines.launch

data class PerfilUiState(
    val nombre: String = "",
    val apellido: String = "",
    val direccion: String = "",
    val correo: String = "",
    val rol: String = "",
    val mensaje: String? = null,
    val error: String? = null
)

/** Pantalla "Mi perfil": permite editar nombre, apellido y dirección del usuario con sesión. */
class PerfilViewModel(
    private val repo: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(cargarInicial())
        private set

    private fun cargarInicial(): PerfilUiState {
        val u = SesionManager.usuario.value ?: return PerfilUiState()
        return PerfilUiState(
            nombre = u.nombre,
            apellido = u.apellido,
            direccion = u.direccion,
            correo = u.correo,
            rol = Rol.desde(u.rol).etiqueta
        )
    }

    fun onNombreChange(v: String) { uiState = uiState.copy(nombre = v, mensaje = null, error = null) }
    fun onApellidoChange(v: String) { uiState = uiState.copy(apellido = v, mensaje = null, error = null) }
    fun onDireccionChange(v: String) { uiState = uiState.copy(direccion = v, mensaje = null, error = null) }

    fun guardar() {
        val actual = SesionManager.usuario.value ?: return
        val s = uiState
        if (!Validaciones.esNombreValido(s.nombre) || !Validaciones.esNombreValido(s.apellido)) {
            uiState = s.copy(error = "Nombre y apellido son obligatorios")
            return
        }
        viewModelScope.launch {
            val nuevo = actual.copy(
                nombre = s.nombre.trim(),
                apellido = s.apellido.trim(),
                direccion = s.direccion.trim()
            )
            repo.actualizarPerfil(nuevo)
            SesionManager.iniciar(nuevo)
            uiState = uiState.copy(mensaje = "Perfil actualizado", error = null)
        }
    }
}
