package com.example.dsy1105_005d_2026.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_005d_2026.data.local.EscaneoEntity
import com.example.dsy1105_005d_2026.data.model.QrResult
import com.example.dsy1105_005d_2026.data.repository.QrRepository
import com.example.dsy1105_005d_2026.data.session.SesionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QrViewModel(
    private val repository: QrRepository
) : ViewModel() {

    private val correo: String = SesionManager.usuario.value?.correo.orEmpty()

    private val _qrResult = MutableStateFlow<QrResult?>(null)
    val qrResult: StateFlow<QrResult?> = _qrResult.asStateFlow()

    // Escaneos guardados en Room de este usuario
    val historial: StateFlow<List<EscaneoEntity>> =
        repository.historial(correo)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onQrDetected(content: String) {
        viewModelScope.launch {
            _qrResult.value = repository.processQrContent(content, correo)
        }
    }

    fun clearResult() {
        _qrResult.value = null
    }
}
