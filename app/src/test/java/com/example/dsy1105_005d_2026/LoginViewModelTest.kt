package com.example.dsy1105_005d_2026

import com.example.dsy1105_005d_2026.data.local.UsuarioEntity
import com.example.dsy1105_005d_2026.data.model.Rol
import com.example.dsy1105_005d_2026.data.repository.AuthRepository
import com.example.dsy1105_005d_2026.data.session.SesionManager
import com.example.dsy1105_005d_2026.viewmodel.LoginViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

/**
 * PRUEBA 4 - LoginViewModel (flujo de login de los 3 perfiles).
 * Se MOCKEA el AuthRepository; se comprueba el rol que entrega y la sesión que se guarda.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest : StringSpec({

    beforeSpec { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterSpec { Dispatchers.resetMain() }
    afterTest { SesionManager.cerrar() }

    fun usuario(correo: String, rol: Rol) = UsuarioEntity(
        id = 1, nombre = "Nombre", apellido = "Apellido", correo = correo, clave = "123456", rol = rol.name
    )

    "un login correcto entrega el rol del usuario y guarda la sesión" {
        val admin = usuario("admin@duoc.cl", Rol.ADMINISTRADOR)
        val repo = mockk<AuthRepository>()
        coEvery { repo.sembrarUsuariosDemo() } just Runs
        coEvery { repo.login("admin@duoc.cl", "123456") } returns admin

        val vm = LoginViewModel(repo)
        vm.onCorreoChange("admin@duoc.cl")
        vm.onPasswordChange("123456")

        var rolRecibido: Rol? = null
        vm.submit { rolRecibido = it }

        rolRecibido shouldBe Rol.ADMINISTRADOR
        SesionManager.usuario.value shouldBe admin
        vm.uiState.isLoading shouldBe false
    }

    "el mismo login lleva a la página de cada perfil (cliente, administrador, técnico)" {
        Rol.CLIENTE.rutaInicio shouldBe "cliente_home"
        Rol.ADMINISTRADOR.rutaInicio shouldBe "admin_home"
        Rol.TECNICO.rutaInicio shouldBe "tecnico_home"
    }

    "credenciales inválidas muestran error y no inician sesión" {
        val repo = mockk<AuthRepository>()
        coEvery { repo.sembrarUsuariosDemo() } just Runs
        coEvery { repo.login(any(), any()) } returns null

        val vm = LoginViewModel(repo)
        vm.onCorreoChange("x@duoc.cl")
        vm.onPasswordChange("mala")

        var navego = false
        vm.submit { navego = true }

        navego shouldBe false
        vm.uiState.error shouldBe "Credenciales inválidas"
        SesionManager.usuario.value shouldBe null
    }

    "con campos vacíos no se consulta la base de datos" {
        val repo = mockk<AuthRepository>()

        val vm = LoginViewModel(repo)
        vm.submit { }

        vm.uiState.error shouldBe "Ingresa correo y contraseña"
        coVerify(exactly = 0) { repo.login(any(), any()) }
    }
})
