package com.example.dsy1105_005d_2026.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Cada código QR escaneado queda guardado aquí, con quién lo leyó y cuándo. */
@Entity(tableName = "escaneos")
data class EscaneoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val contenido: String,
    val fecha: Long,
    val usuarioCorreo: String
)
