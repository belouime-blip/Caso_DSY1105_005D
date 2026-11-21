package com.example.dsy1105_005d_2026.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.dsy1105_005d_2026.data.model.Rol

/** Tabla "usuarios": guarda a los 3 perfiles (cliente, administrador, técnico). */
@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["correo"], unique = true)]
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val clave: String,
    val direccion: String = "",
    val rol: String = Rol.CLIENTE.name
)
