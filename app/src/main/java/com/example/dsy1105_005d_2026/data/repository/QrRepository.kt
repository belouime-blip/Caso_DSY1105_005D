package com.example.dsy1105_005d_2026.data.repository

import com.example.dsy1105_005d_2026.data.local.EscaneoDao
import com.example.dsy1105_005d_2026.data.local.EscaneoEntity
import com.example.dsy1105_005d_2026.data.model.QrResult
import kotlinx.coroutines.flow.Flow

class QrRepository(private val dao: EscaneoDao) {

    fun historial(usuarioCorreo: String): Flow<List<EscaneoEntity>> =
        dao.observarPorUsuario(usuarioCorreo)

    /** Guarda el escaneo en Room y devuelve el resultado para mostrarlo en pantalla. */
    suspend fun processQrContent(content: String, usuarioCorreo: String): QrResult {
        dao.insertar(
            EscaneoEntity(
                contenido = content,
                fecha = System.currentTimeMillis(),
                usuarioCorreo = usuarioCorreo
            )
        )
        return QrResult(content)
    }
}
