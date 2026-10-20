package com.example.dsy1105_005d_2026.ui.qr

import androidx.lifecycle.ViewModel
import com.example.dsy1105_005d_2026.data.model.QrResult
import com.example.dsy1105_005d_2026.data.repository.QrRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class QrViewModel(
    private val repository: QrRepository = QrRepository()
) : ViewModel() {

    private val _qrResult = MutableStateFlow<QrResult?>(null)
    val qrResult: StateFlow<QrResult?> = _qrResult.asStateFlow()

    fun onQrDetected(content: String) {
        _qrResult.value = repository.processQrContent(content)
    }

    fun clearResult() {
        _qrResult.value = null
    }
}
