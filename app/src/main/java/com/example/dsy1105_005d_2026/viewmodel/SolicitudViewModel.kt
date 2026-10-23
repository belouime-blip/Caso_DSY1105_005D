package com.example.dsy1105_005d_2026.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_005d_2026.data.local.AppDatabase
import com.example.dsy1105_005d_2026.data.local.SolicitudEntity
import com.example.dsy1105_005d_2026.data.repository.SolicitudRepository
import com.example.dsy1105_005d_2026.ui.solicitud.SolicitudUiState
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SolicitudViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = SolicitudRepository(AppDatabase.obtener(app).solicitudDao())

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val _categoriaFiltro = MutableStateFlow("")
    val categoriaFiltro: StateFlow<String> = _categoriaFiltro.asStateFlow()

    val solicitudes: StateFlow<List<SolicitudEntity>> =
        combine(repo.solicitudes, _busqueda, _categoriaFiltro) { lista, texto, categoria ->
            lista.filter { s ->
                (categoria.isEmpty() || s.categoria == categoria) &&
                    (texto.isBlank() ||
                        s.titulo.contains(texto, ignoreCase = true) ||
                        s.codigo.contains(texto, ignoreCase = true))
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(SolicitudUiState())
    val uiState: StateFlow<SolicitudUiState> = _uiState.asStateFlow()

    fun onBusquedaChange(v: String) {
        _busqueda.value = v
    }

    fun onFiltroCategoria(v: String) {
        _categoriaFiltro.value = v
    }

    fun observar(id: Int): Flow<SolicitudEntity?> = repo.observar(id)

    fun buscarPorCodigo(codigo: String, onResultado: (Int?) -> Unit) {
        viewModelScope.launch {
            onResultado(repo.buscarPorCodigo(codigo.trim())?.id)
        }
    }

    fun eliminar(id: Int, onListo: () -> Unit) {
        viewModelScope.launch {
            repo.eliminar(id)
            onListo()
        }
    }

    fun cargar(id: Int) {
        if (id == 0) return
        viewModelScope.launch {
            repo.obtener(id)?.let { s ->
                _uiState.value = SolicitudUiState(
                    id = s.id,
                    codigo = s.codigo,
                    estado = s.estado,
                    fecha = s.fecha,
                    titulo = s.titulo,
                    descripcion = s.descripcion,
                    categoria = s.categoria,
                    servicio = s.servicio,
                    evidenciaUri = s.evidenciaUri
                )
            }
        }
    }

    fun onTituloChange(v: String) {
        _uiState.value = _uiState.value.copy(titulo = v, tituloError = null)
    }

    fun onDescripcionChange(v: String) {
        _uiState.value = _uiState.value.copy(descripcion = v, descripcionError = null)
    }

    fun onCategoriaChange(v: String) {
        _uiState.value = _uiState.value.copy(categoria = v, categoriaError = null)
    }

    fun onServicioChange(v: String) {
        _uiState.value = _uiState.value.copy(servicio = v, servicioError = null)
    }

    fun onEvidenciaChange(uri: String?) {
        _uiState.value = _uiState.value.copy(evidenciaUri = uri)
    }

    fun guardar() {
        val s = _uiState.value
        val tituloError = if (s.titulo.isBlank()) "El título es obligatorio" else null
        val descripcionError = when {
            s.descripcion.isBlank() -> "La descripción es obligatoria"
            s.descripcion.trim().length < 10 -> "Mínimo 10 caracteres"
            else -> null
        }
        val categoriaError = if (s.categoria.isBlank()) "Selecciona una categoría" else null
        val servicioError = if (s.servicio.isBlank()) "Selecciona un servicio" else null

        if (listOf(tituloError, descripcionError, categoriaError, servicioError).any { it != null }) {
            _uiState.value = s.copy(
                tituloError = tituloError,
                descripcionError = descripcionError,
                categoriaError = categoriaError,
                servicioError = servicioError
            )
            return
        }

        _uiState.value = s.copy(guardando = true)
        viewModelScope.launch {
            repo.guardar(
                SolicitudEntity(
                    id = s.id,
                    codigo = s.codigo.ifEmpty { "SOL-" + UUID.randomUUID().toString().take(8).uppercase() },
                    titulo = s.titulo.trim(),
                    descripcion = s.descripcion.trim(),
                    categoria = s.categoria,
                    servicio = s.servicio,
                    evidenciaUri = s.evidenciaUri,
                    estado = s.estado,
                    fecha = if (s.fecha == 0L) System.currentTimeMillis() else s.fecha
                )
            )
            _uiState.value = _uiState.value.copy(guardando = false, guardadoExitosamente = true)
        }
    }
}
