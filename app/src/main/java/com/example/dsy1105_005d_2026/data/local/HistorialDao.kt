package com.example.dsy1105_005d_2026.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistorialDao {
    @Query("SELECT * FROM historial WHERE solicitudId = :solicitudId ORDER BY fecha DESC")
    fun observarPorSolicitud(solicitudId: Int): Flow<List<HistorialEntity>>

    @Insert
    suspend fun insertar(entrada: HistorialEntity)

    @Query("DELETE FROM historial WHERE solicitudId = :solicitudId")
    suspend fun eliminarPorSolicitud(solicitudId: Int)
}
