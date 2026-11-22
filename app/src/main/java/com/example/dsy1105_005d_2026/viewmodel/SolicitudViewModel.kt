package com.example.dsy1105_005d_2026.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_005d_2026.util.Notificador
import com.example.dsy1105_005d_2026.data.local.AppDatabase
import com.example.dsy1105_005d_2026.data.local.EscaneoEntity
import com.example.dsy1105_005d_2026.data.local.HistorialEntity
import com.example.dsy1105_005d_2026.data.local.SolicitudEntity
import com.example.dsy1105_005d_2026.data.local.UsuarioEntity
import com.example.dsy1105_005d_2026.data.model.Rol
import com.example.dsy1105_005d_2026.data.repository.SolicitudRepository
import com.example.dsy1105_005d_2026.data.session.SesionManager
import com.example.dsy1105_005d_2026.ui.solicitud.SolicitudUiState
import com.example.dsy1105_005d_2026.util.SolicitudFlujo
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
    private val db = AppDatabase.obtener(app)
    private val repo = SolicitudRepository(db.solicitudDao(), db.historialDao())

    private fun autor(): String = SesionManager.usuario.value?.correo.orEmpty()

    private fun aviso(titulo: String, texto: String) =
        Notificador.avisar(getApplication<Application>(), titulo, texto)

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val _categoriaFiltro = MutableStateFlow("")
    val categoriaFiltro: StateFlow<String> = _categoriaFiltro.asStateFlow()

    // Lista según el perfil: cliente ve las suyas, técnico las asignadas, administrador todas
    val solicitudes: StateFlow<List<SolicitudEntity>> =
        combine(
            repo.solicitudes,
            _busqueda,
            _categoriaFiltro,
            SesionManager.usuario
        ) { lista, texto, categoria, usuario ->
            SolicitudFlujo.visiblesPara(lista, usuario).filter { s ->
                (categoria.isEmpty() || s.categoria == categoria) &&
                    (texto.isBlank() ||
                        s.titulo.contains(texto, ignoreCase = true) ||
                        s.codigo.contains(texto, ignoreCase = true))
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Técnicos disponibles para que el administrador asigne
    val tecnicos: StateFlow<List<UsuarioEntity>> =
        db.usuarioDao().observarPorRol(Rol.TECNICO.name)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(SolicitudUiState())
    val uiState: StateFlow<SolicitudUiState> = _uiState.asStateFlow()

    fun onBusquedaChange(v: String) {
        _busqueda.value = v
    }

    fun onFiltroCategoria(v: String) {
        _categoriaFiltro.value = v
    }

    fun observar(id: Int): Flow<SolicitudEntity?> = repo.observar(id)

    /**
     * Busca la solicitud por el código leído en el QR. El escaneo queda guardado en Room.
     * Solo devuelve el id si la solicitud es visible para el perfil actual.
     */
    fun buscarPorCodigo(codigo: String, onResultado: (Int?) -> Unit) {
        viewModelScope.launch {
            val limpio = codigo.trim()
            db.escaneoDao().insertar(
                EscaneoEntity(
                    contenido = limpio,
                    fecha = System.currentTimeMillis(),
                    usuarioCorreo = autor()
                )
            )
            val encontrada = repo.buscarPorCodigo(limpio)
            val visible = encontrada != null &&
                SolicitudFlujo.visiblesPara(listOf(encontrada), SesionManager.usuario.value).isNotEmpty()
            onResultado(if (visible) encontrada?.id else null)
        }
    }

    fun historial(id: Int): Flow<List<HistorialEntity>> = repo.historial(id)

    /** Historial de códigos QR leídos por el usuario activo, persistido en Room. */
    fun escaneos(): Flow<List<EscaneoEntity>> =
        db.escaneoDao().observarPorUsuario(autor())

    /** Comentario o evidencia (foto de avance) que queda en el historial de la solicitud. */
    fun comentar(id: Int, texto: String, fotoUri: String? = null) {
        if (texto.isBlank() && fotoUri == null) return
        viewModelScope.launch {
            repo.registrarHistorial(id, autor(), texto.trim(), fotoUri)
        }
    }

    fun eliminar(id: Int, onListo: () -> Unit) {
        viewModelScope.launch {
            repo.eliminar(id)
            onListo()
        }
    }

    /** ADMINISTRADOR: define prioridad y asigna un técnico (el estado pasa a "Asignada"). */
    fun asignar(id: Int, tecnicoCorreo: String, prioridad: String) {
        if (SesionManager.rol != Rol.ADMINISTRADOR) return
        viewModelScope.launch {
            val s = repo.obtener(id) ?: return@launch
            if (!SolicitudFlujo.puedeAsignar(s.estado)) return@launch
            repo.guardar(
                s.copy(
                    tecnicoCorreo = tecnicoCorreo,
                    prioridad = prioridad,
                    estado = SolicitudFlujo.ASIGNADA
                )
            )
            repo.registrarHistorial(
                id, autor(), "Asignada a $tecnicoCorreo con prioridad $prioridad"
            )
            aviso("Solicitud asignada", "${s.codigo} fue asignada al técnico $tecnicoCorreo")
        }
    }

    /** Cambia el estado solo si el perfil actual tiene permiso para esa transición. */
    fun cambiarEstado(id: Int, nuevoEstado: String) {
        val rol = SesionManager.rol ?: return
        viewModelScope.launch {
            val s = repo.obtener(id) ?: return@launch
            if (!SolicitudFlujo.puedeCambiar(s.estado, nuevoEstado, rol)) return@launch
            repo.guardar(s.copy(estado = nuevoEstado))
            repo.registrarHistorial(id, autor(), "Estado: ${s.estado} → $nuevoEstado")
            if (nuevoEstado == SolicitudFlujo.RESUELTA) {
                aviso("Solicitud resuelta", "${s.codigo}: pendiente de confirmación del cliente")
            } else {
                aviso("Actualización de estado", "${s.codigo} ahora está en: $nuevoEstado")
            }
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
                    evidenciaUri = s.evidenciaUri,
                    prioridad = s.prioridad,
                    tecnicoCorreo = s.tecnicoCorreo,
                    clienteCorreo = s.clienteCorreo
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
            val idGuardado = repo.guardar(
                SolicitudEntity(
                    id = s.id,
                    codigo = s.codigo.ifEmpty { "SOL-" + UUID.randomUUID().toString().take(8).uppercase() },
                    titulo = s.titulo.trim(),
                    descripcion = s.descripcion.trim(),
                    categoria = s.categoria,
                    servicio = s.servicio,
                    evidenciaUri = s.evidenciaUri,
                    estado = s.estado,
                    fecha = if (s.fecha == 0L) System.currentTimeMillis() else s.fecha,
                    prioridad = s.prioridad,
                    tecnicoCorreo = s.tecnicoCorreo,
                    clienteCorreo = s.clienteCorreo.ifEmpty { SesionManager.usuario.value?.correo.orEmpty() }
                )
            )
            val esNueva = s.id == 0
            repo.registrarHistorial(
                solicitudId = if (esNueva) idGuardado.toInt() else s.id,
                autor = autor(),
                texto = if (esNueva) "Solicitud creada (Recibida)" else "Solicitud editada",
                fotoUri = s.evidenciaUri
            )
            if (esNueva) aviso("Nueva solicitud recibida", s.titulo)
            _uiState.value = _uiState.value.copy(guardando = false, guardadoExitosamente = true)
        }
    }
}
