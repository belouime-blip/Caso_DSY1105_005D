package com.example.dsy1105_005d_2026.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Historial de cambios, comentarios y evidencias (fotos de avance) de una solicitud.
 * Cada cambio de estado, asignación o comentario genera una fila.
 */
@Entity(tableName = "historial")
data class HistorialEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val solicitudId: Int,
    val fecha: Long,
    val autor: String,
    val texto: String,
    val fotoUri: String? = null
)
