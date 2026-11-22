package com.example.dsy1105_005d_2026.data.repository

import com.example.dsy1105_005d_2026.data.local.UsuarioDao
import com.example.dsy1105_005d_2026.data.local.UsuarioEntity
import com.example.dsy1105_005d_2026.data.model.Rol
import kotlinx.coroutines.flow.Flow

/** Resultado del registro (propio, para que sea simple de simular en las pruebas). */
sealed class ResultadoRegistro {
    data class Exito(val id: Long) : ResultadoRegistro()
    data class Error(val mensaje: String) : ResultadoRegistro()
}

class AuthRepository(private val dao: UsuarioDao) {

    /** Devuelve el usuario si correo y clave coinciden en Room; null si son inválidos. */
    suspend fun login(correo: String, clave: String): UsuarioEntity? =
        dao.iniciarSesion(correo.trim().lowercase(), clave)

    /** Busca una cuenta existente para desbloqueo biométrico local del dispositivo. */
    suspend fun usuarioPorCorreo(correo: String): UsuarioEntity? =
        dao.obtenerPorCorreo(correo.trim().lowercase())

    /** El auto-registro siempre crea perfiles CLIENTE. Falla si el correo ya existe. */
    suspend fun registrar(usuario: UsuarioEntity): ResultadoRegistro {
        val correo = usuario.correo.trim().lowercase()
        if (dao.obtenerPorCorreo(correo) != null) {
            return ResultadoRegistro.Error("El correo ya está registrado")
        }
        val id = dao.insertar(usuario.copy(correo = correo, rol = Rol.CLIENTE.name))
        return ResultadoRegistro.Exito(id)
    }

    suspend fun actualizarPerfil(usuario: UsuarioEntity) = dao.actualizar(usuario)

    fun tecnicos(): Flow<List<UsuarioEntity>> = dao.observarPorRol(Rol.TECNICO.name)

    /** Crea los 3 usuarios de prueba (uno por perfil) la primera vez que se abre la app. */
    suspend fun sembrarUsuariosDemo() {
        if (dao.contar() > 0) return
        USUARIOS_DEMO.forEach { dao.insertar(it) }
    }

    companion object {
        const val CLAVE_DEMO = "123456"

        val USUARIOS_DEMO = listOf(
            UsuarioEntity(nombre = "Camila", apellido = "Rojas", correo = "cliente@duoc.cl",
                clave = CLAVE_DEMO, direccion = "Av. Providencia 123", rol = Rol.CLIENTE.name),
            UsuarioEntity(nombre = "Andrés", apellido = "Muñoz", correo = "admin@duoc.cl",
                clave = CLAVE_DEMO, direccion = "Oficina central", rol = Rol.ADMINISTRADOR.name),
            UsuarioEntity(nombre = "Felipe", apellido = "Soto", correo = "tecnico@duoc.cl",
                clave = CLAVE_DEMO, direccion = "Soporte en terreno", rol = Rol.TECNICO.name)
        )
    }
}
