package com.example.dsy1105_005d_2026

import com.example.dsy1105_005d_2026.data.local.UsuarioDao
import com.example.dsy1105_005d_2026.data.local.UsuarioEntity
import com.example.dsy1105_005d_2026.data.model.Rol
import com.example.dsy1105_005d_2026.data.repository.AuthRepository
import com.example.dsy1105_005d_2026.data.repository.ResultadoRegistro
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot

/**
 * PRUEBA 2 - AuthRepository (login y registro).
 * Se MOCKEA el UsuarioDao (la base de datos Room) para probar solo la lógica del repositorio.
 */
class AuthRepositoryTest : StringSpec({

    val clienteDemo = UsuarioEntity(
        id = 1, nombre = "Camila", apellido = "Rojas", correo = "cliente@duoc.cl",
        clave = "123456", rol = Rol.CLIENTE.name
    )

    "login normaliza el correo (espacios y mayúsculas) y devuelve el usuario" {
        val dao = mockk<UsuarioDao>()
        coEvery { dao.iniciarSesion("cliente@duoc.cl", "123456") } returns clienteDemo

        AuthRepository(dao).login("  CLIENTE@duoc.cl ", "123456") shouldBe clienteDemo
    }

    "login con credenciales incorrectas devuelve null" {
        val dao = mockk<UsuarioDao>()
        coEvery { dao.iniciarSesion(any(), any()) } returns null

        AuthRepository(dao).login("cliente@duoc.cl", "mala") shouldBe null
    }

    "registrar rechaza un correo que ya existe y no inserta nada" {
        val dao = mockk<UsuarioDao>()
        coEvery { dao.obtenerPorCorreo("cliente@duoc.cl") } returns clienteDemo

        val resultado = AuthRepository(dao).registrar(clienteDemo)

        resultado shouldBe ResultadoRegistro.Error("El correo ya está registrado")
        coVerify(exactly = 0) { dao.insertar(any()) }
    }

    "registrar siempre crea perfil CLIENTE aunque se intente registrar como administrador" {
        val dao = mockk<UsuarioDao>()
        val guardado = slot<UsuarioEntity>()
        coEvery { dao.obtenerPorCorreo(any()) } returns null
        coEvery { dao.insertar(capture(guardado)) } returns 7L

        val intento = clienteDemo.copy(id = 0, correo = " NUEVO@Duoc.cl ", rol = Rol.ADMINISTRADOR.name)
        val resultado = AuthRepository(dao).registrar(intento)

        resultado shouldBe ResultadoRegistro.Exito(7L)
        guardado.captured.rol shouldBe Rol.CLIENTE.name
        guardado.captured.correo shouldBe "nuevo@duoc.cl"
    }

    "sembrarUsuariosDemo crea los 3 perfiles solo si la tabla está vacía" {
        val vacio = mockk<UsuarioDao>()
        coEvery { vacio.contar() } returns 0
        coEvery { vacio.insertar(any()) } returns 1L
        AuthRepository(vacio).sembrarUsuariosDemo()
        coVerify(exactly = 3) { vacio.insertar(any()) }

        val conDatos = mockk<UsuarioDao>()
        coEvery { conDatos.contar() } returns 3
        AuthRepository(conDatos).sembrarUsuariosDemo()
        coVerify(exactly = 0) { conDatos.insertar(any()) }
    }
})
