package com.example.dsy1105_005d_2026.ui.solicitud

data class SolicitudUiState(
    val id: Int = 0,
    val codigo: String = "",
    val estado: String = "Recibida",
    val fecha: Long = 0L,
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val servicio: String = "",
    val evidenciaUri: String? = null,
    val prioridad: String = "Media",
    val tecnicoCorreo: String? = null,
    val clienteCorreo: String = "",
    val tituloError: String? = null,
    val descripcionError: String? = null,
    val categoriaError: String? = null,
    val servicioError: String? = null,
    val guardando: Boolean = false,
    val guardadoExitosamente: Boolean = false
)
