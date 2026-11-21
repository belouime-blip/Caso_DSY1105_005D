package com.example.dsy1105_005d_2026.data.repository

import com.example.dsy1105_005d_2026.data.local.HistorialDao
import com.example.dsy1105_005d_2026.data.local.HistorialEntity
import com.example.dsy1105_005d_2026.data.local.SolicitudDao
import com.example.dsy1105_005d_2026.data.local.SolicitudEntity
import kotlinx.coroutines.flow.Flow

class SolicitudRepository(
    private val dao: SolicitudDao,
    private val historialDao: HistorialDao
) {
    val solicitudes: Flow<List<SolicitudEntity>> = dao.obtenerTodas()

    fun observar(id: Int): Flow<SolicitudEntity?> = dao.observarPorId(id)

    suspend fun obtener(id: Int): SolicitudEntity? = dao.obtenerPorId(id)

    suspend fun buscarPorCodigo(codigo: String): SolicitudEntity? = dao.obtenerPorCodigo(codigo)

    /** Inserta o actualiza y devuelve el id de la fila (útil al crear una solicitud nueva). */
    suspend fun guardar(solicitud: SolicitudEntity): Long = dao.guardar(solicitud)

    suspend fun eliminar(id: Int) {
        dao.eliminar(id)
        historialDao.eliminarPorSolicitud(id)
    }

    fun historial(solicitudId: Int): Flow<List<HistorialEntity>> =
        historialDao.observarPorSolicitud(solicitudId)

    suspend fun registrarHistorial(
        solicitudId: Int,
        autor: String,
        texto: String,
        fotoUri: String? = null
    ) {
        historialDao.insertar(
            HistorialEntity(
                solicitudId = solicitudId,
                fecha = System.currentTimeMillis(),
                autor = autor,
                texto = texto,
                fotoUri = fotoUri
            )
        )
    }
}
