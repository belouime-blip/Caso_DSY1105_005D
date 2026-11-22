package com.example.dsy1105_005d_2026.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudDao {
    @Query("SELECT * FROM solicitudes ORDER BY fecha DESC")
    fun obtenerTodas(): Flow<List<SolicitudEntity>>

    @Query("SELECT * FROM solicitudes WHERE id = :id")
    suspend fun obtenerPorId(id: Int): SolicitudEntity?

    @Query("SELECT * FROM solicitudes WHERE id = :id")
    fun observarPorId(id: Int): Flow<SolicitudEntity?>

    @Query("SELECT * FROM solicitudes WHERE codigo = :codigo LIMIT 1")
    suspend fun obtenerPorCodigo(codigo: String): SolicitudEntity?

    @Query("DELETE FROM solicitudes WHERE id = :id")
    suspend fun eliminar(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(solicitud: SolicitudEntity): Long
}
