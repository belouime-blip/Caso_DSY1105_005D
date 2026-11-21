package com.example.dsy1105_005d_2026.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EscaneoDao {
    @Query("SELECT * FROM escaneos WHERE usuarioCorreo = :correo ORDER BY fecha DESC LIMIT 20")
    fun observarPorUsuario(correo: String): Flow<List<EscaneoEntity>>

    @Insert
    suspend fun insertar(escaneo: EscaneoEntity)
}
