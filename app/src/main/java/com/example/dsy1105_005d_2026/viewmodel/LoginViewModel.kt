package com.example.dsy1105_005d_2026.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_005d_2026.data.model.Rol
import com.example.dsy1105_005d_2026.data.repository.AuthRepository
import com.example.dsy1105_005d_2026.data.session.SesionManager
import com.example.dsy1105_005d_2026.ui.login.LoginUiState
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repo: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onCorreoChange(value: String) {
        uiState = uiState.copy(correo = value, error = null)
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value, error = null)
    }

    /**
     * Valida contra Room. Si es correcto guarda la sesión y entrega el [Rol],
     * con el que la pantalla decide a qué página (cliente / admin / técnico) navegar.
     */
    fun submit(onSuccess: (Rol) -> Unit) {
        val actual = uiState
        if (actual.correo.isBlank() || actual.password.isBlank()) {
            uiState = actual.copy(error = "Ingresa correo y contraseña")
            return
        }
        uiState = actual.copy(isLoading = true, error = null)

        viewModelScope.launch {
            repo.sembrarUsuariosDemo()
            val usuario = repo.login(actual.correo, actual.password)
            if (usuario == null) {
                uiState = uiState.copy(isLoading = false, error = "Credenciales inválidas")
            } else {
                SesionManager.iniciar(usuario)
                uiState = uiState.copy(isLoading = false)
                onSuccess(Rol.desde(usuario.rol))
            }
        }
    }

    /**
     * Se llama después de que BiometricPrompt confirmó la identidad en el dispositivo.
     * Abre la cuenta local asociada al correo escrito, sin pedir la contraseña.
     */
    fun biometricLogin(correo: String, onSuccess: (Rol) -> Unit) {
        if (correo.isBlank()) {
            uiState = uiState.copy(error = "Escribe tu correo para usar el acceso biométrico")
            return
        }
        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val usuario = repo.usuarioPorCorreo(correo)
            if (usuario == null) {
                uiState = uiState.copy(isLoading = false, error = "No existe una cuenta con ese correo")
            } else {
                SesionManager.iniciar(usuario)
                uiState = uiState.copy(isLoading = false)
                onSuccess(Rol.desde(usuario.rol))
            }
        }
    }
}
