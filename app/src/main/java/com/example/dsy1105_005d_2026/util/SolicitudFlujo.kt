package com.example.dsy1105_005d_2026.util

import com.example.dsy1105_005d_2026.data.local.SolicitudEntity
import com.example.dsy1105_005d_2026.data.local.UsuarioEntity
import com.example.dsy1105_005d_2026.data.model.Rol

/**
 * Reglas de negocio de las solicitudes (kotlin puro, sin Android, por eso se puede probar fácil).
 * - Qué solicitudes ve cada perfil.
 * - Qué cambios de estado puede hacer cada perfil (diagrama "Estados de la solicitud").
 */
object SolicitudFlujo {
    const val RECIBIDA = "Recibida"
    const val EN_REVISION = "En revisión"
    const val PENDIENTE_INFO = "Pendiente de información"
    const val ASIGNADA = "Asignada"
    const val EN_PROCESO = "En proceso"
    const val RESUELTA = "Resuelta"
    const val CERRADA = "Cerrada"
    const val CANCELADA = "Cancelada"

    val todosLosEstados = listOf(
        RECIBIDA, EN_REVISION, PENDIENTE_INFO, ASIGNADA, EN_PROCESO, RESUELTA, CERRADA, CANCELADA
    )

    val prioridades = listOf("Baja", "Media", "Alta")

    // rol -> (estado actual -> estados a los que puede pasar)
    private val transiciones: Map<Rol, Map<String, Set<String>>> = mapOf(
        Rol.ADMINISTRADOR to mapOf(
            RECIBIDA to setOf(EN_REVISION, CANCELADA),
            EN_REVISION to setOf(PENDIENTE_INFO, CANCELADA),
            PENDIENTE_INFO to setOf(EN_REVISION, CANCELADA),
            ASIGNADA to setOf(CANCELADA),
            EN_PROCESO to setOf(CANCELADA),
            RESUELTA to setOf(CERRADA)
        ),
        Rol.TECNICO to mapOf(
            ASIGNADA to setOf(EN_PROCESO),
            EN_PROCESO to setOf(RESUELTA, PENDIENTE_INFO)
        ),
        Rol.CLIENTE to mapOf(
            PENDIENTE_INFO to setOf(EN_REVISION),
            RESUELTA to setOf(CERRADA, EN_REVISION), // confirma la resolución o pide reapertura
            CERRADA to setOf(EN_REVISION)            // reapertura
        )
    )

    /** Cliente: las suyas. Técnico: las asignadas a él. Administrador: todas. Sin sesión: ninguna. */
    fun visiblesPara(lista: List<SolicitudEntity>, usuario: UsuarioEntity?): List<SolicitudEntity> {
        if (usuario == null) return emptyList()
        return when (Rol.desde(usuario.rol)) {
            Rol.ADMINISTRADOR -> lista
            Rol.CLIENTE -> lista.filter { it.clienteCorreo.equals(usuario.correo, ignoreCase = true) }
            Rol.TECNICO -> lista.filter { it.tecnicoCorreo.equals(usuario.correo, ignoreCase = true) }
        }
    }

    /** Estados a los que el perfil puede mover la solicitud desde [estado]. */
    fun siguientes(estado: String, rol: Rol): Set<String> =
        transiciones[rol]?.get(estado).orEmpty()

    fun puedeCambiar(actual: String, nuevo: String, rol: Rol): Boolean =
        nuevo in siguientes(actual, rol)

    /** El administrador puede asignar (o reasignar) mientras no esté en proceso ni terminada. */
    fun puedeAsignar(estado: String): Boolean =
        estado == RECIBIDA || estado == EN_REVISION || estado == ASIGNADA
}
