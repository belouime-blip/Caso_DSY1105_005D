package com.example.dsy1105_005d_2026.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_005d_2026.data.local.UsuarioEntity
import com.example.dsy1105_005d_2026.data.model.Rol
import com.example.dsy1105_005d_2026.data.repository.AuthRepository
import com.example.dsy1105_005d_2026.data.repository.ResultadoRegistro
import com.example.dsy1105_005d_2026.util.Validaciones
import kotlinx.coroutines.launch

data class RegistroUiState(
    val nombre: String = "",
    val apellido: String = "",
    val correo: String = "",
    val clave: String = "",
    val direccion: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class RegistroViewModel(
    private val repo: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(RegistroUiState())
        private set

    fun onNombreChange(v: String) { uiState = uiState.copy(nombre = v, error = null) }
    fun onApellidoChange(v: String) { uiState = uiState.copy(apellido = v, error = null) }
    fun onCorreoChange(v: String) { uiState = uiState.copy(correo = v, error = null) }
    fun onClaveChange(v: String) { uiState = uiState.copy(clave = v, error = null) }
    fun onDireccionChange(v: String) { uiState = uiState.copy(direccion = v, error = null) }

    /** Valida los campos y crea un usuario con perfil CLIENTE en Room. */
    fun registrar(onRegistrado: () -> Unit) {
        val s = uiState
        val error = when {
            !Validaciones.esNombreValido(s.nombre) -> "Ingresa tu nombre"
            !Validaciones.esNombreValido(s.apellido) -> "Ingresa tu apellido"
            !Validaciones.esCorreoValido(s.correo) -> "Correo inválido"
            !Validaciones.esClaveValida(s.clave) -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
        if (error != null) {
            uiState = s.copy(error = error)
            return
        }

        uiState = s.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val resultado = repo.registrar(
                UsuarioEntity(
                    nombre = s.nombre.trim(),
                    apellido = s.apellido.trim(),
                    correo = s.correo,
                    clave = s.clave,
                    direccion = s.direccion.trim(),
                    rol = Rol.CLIENTE.name
                )
            )
            when (resultado) {
                is ResultadoRegistro.Exito -> {
                    uiState = uiState.copy(isLoading = false)
                    onRegistrado()
                }
                is ResultadoRegistro.Error -> {
                    uiState = uiState.copy(isLoading = false, error = resultado.mensaje)
                }
            }
        }
    }
}
