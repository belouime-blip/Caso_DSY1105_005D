package com.example.dsy1105_005d_2026.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// version 3: usuarios (con apellido y rol), historial de cambios/comentarios y escaneos QR guardados
@Database(
    entities = [
        SolicitudEntity::class,
        UsuarioEntity::class,
        HistorialEntity::class,
        EscaneoEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun solicitudDao(): SolicitudDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun historialDao(): HistorialDao
    abstract fun escaneoDao(): EscaneoDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "solicitudes.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instancia = it }
            }
    }
}
