package com.example.dsy1105_005d_2026.data.model

/** Los 3 perfiles de la aplicación. Cada uno tiene su propia página de inicio tras el login. */
enum class Rol(val etiqueta: String, val rutaInicio: String) {
    CLIENTE("Cliente", "cliente_home"),
    ADMINISTRADOR("Administrador", "admin_home"),
    TECNICO("Técnico", "tecnico_home");

    companion object {
        /** Convierte el texto guardado en Room al enum. Si no coincide, el perfil más restringido (cliente). */
        fun desde(valor: String?): Rol = entries.firstOrNull { it.name == valor } ?: CLIENTE
    }
}
