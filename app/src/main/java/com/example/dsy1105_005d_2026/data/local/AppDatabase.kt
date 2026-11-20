package com.example.dsy1105_005d_2026.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// version 2: se agregó la tabla usuarios (con apellido y rol) y columnas nuevas en solicitudes
@Database(
    entities = [SolicitudEntity::class, UsuarioEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun solicitudDao(): SolicitudDao
    abstract fun usuarioDao(): UsuarioDao

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
