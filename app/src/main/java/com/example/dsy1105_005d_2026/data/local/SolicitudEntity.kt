package com.example.dsy1105_005d_2026.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "solicitudes")
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val codigo: String,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val servicio: String,
    val evidenciaUri: String?,
    val estado: String,
    val fecha: Long,
    // Campos nuevos para los 3 perfiles
    val prioridad: String = "Media",          // la define el administrador
    val tecnicoCorreo: String? = null,        // técnico asignado por el administrador
    val clienteCorreo: String = ""            // cliente que creó la solicitud
)
