package com.example.dsy1105_005d_2026

import com.example.dsy1105_005d_2026.data.local.SolicitudEntity
import com.example.dsy1105_005d_2026.data.local.UsuarioEntity
import com.example.dsy1105_005d_2026.data.model.Rol
import com.example.dsy1105_005d_2026.util.SolicitudFlujo
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

/**
 * PRUEBA 1 - Reglas de negocio de las solicitudes (lógica pura, no se mockea nada).
 * Valida qué ve cada perfil y qué cambios de estado puede hacer.
 */
class SolicitudFlujoTest : StringSpec({

    fun solicitud(id: Int, cliente: String, tecnico: String?, estado: String = SolicitudFlujo.RECIBIDA) =
        SolicitudEntity(
            id = id,
            codigo = "SOL-$id",
            titulo = "Solicitud $id",
            descripcion = "Descripción de la solicitud $id",
            categoria = "Soporte",
            servicio = "Soporte técnico",
            evidenciaUri = null,
            estado = estado,
            fecha = 0L,
            tecnicoCorreo = tecnico,
            clienteCorreo = cliente
        )

    fun usuario(correo: String, rol: Rol) =
        UsuarioEntity(nombre = "Nombre", apellido = "Apellido", correo = correo, clave = "123456", rol = rol.name)

    val lista = listOf(
        solicitud(1, cliente = "ana@duoc.cl", tecnico = "tec@duoc.cl"),
        solicitud(2, cliente = "ana@duoc.cl", tecnico = null),
        solicitud(3, cliente = "luis@duoc.cl", tecnico = "tec@duoc.cl"),
        solicitud(4, cliente = "luis@duoc.cl", tecnico = "otro@duoc.cl")
    )

    "el cliente solo ve sus propias solicitudes" {
        val vistas = SolicitudFlujo.visiblesPara(lista, usuario("ana@duoc.cl", Rol.CLIENTE))
        vistas.map { it.id }.shouldContainExactly(1, 2)
    }

    "el técnico solo ve las solicitudes que le asignaron" {
        val vistas = SolicitudFlujo.visiblesPara(lista, usuario("tec@duoc.cl", Rol.TECNICO))
        vistas.map { it.id }.shouldContainExactly(1, 3)
    }

    "el administrador ve todas y sin sesión no se ve ninguna" {
        SolicitudFlujo.visiblesPara(lista, usuario("admin@duoc.cl", Rol.ADMINISTRADOR)) shouldBe lista
        SolicitudFlujo.visiblesPara(lista, null).shouldBeEmpty()
    }

    "cada perfil solo puede hacer los cambios de estado que le corresponden" {
        // técnico: Asignada -> En proceso sí, cerrar no
        SolicitudFlujo.puedeCambiar(SolicitudFlujo.ASIGNADA, SolicitudFlujo.EN_PROCESO, Rol.TECNICO) shouldBe true
        SolicitudFlujo.puedeCambiar(SolicitudFlujo.EN_PROCESO, SolicitudFlujo.CERRADA, Rol.TECNICO) shouldBe false
        // cliente: confirma la resolución, pero no puede cancelar
        SolicitudFlujo.puedeCambiar(SolicitudFlujo.RESUELTA, SolicitudFlujo.CERRADA, Rol.CLIENTE) shouldBe true
        SolicitudFlujo.puedeCambiar(SolicitudFlujo.RECIBIDA, SolicitudFlujo.CANCELADA, Rol.CLIENTE) shouldBe false
        // administrador: cierra una resuelta y cancela una recibida
        SolicitudFlujo.puedeCambiar(SolicitudFlujo.RESUELTA, SolicitudFlujo.CERRADA, Rol.ADMINISTRADOR) shouldBe true
        SolicitudFlujo.puedeCambiar(SolicitudFlujo.RECIBIDA, SolicitudFlujo.CANCELADA, Rol.ADMINISTRADOR) shouldBe true
    }

    "solo se puede asignar técnico antes de que la solicitud esté en proceso" {
        SolicitudFlujo.puedeAsignar(SolicitudFlujo.RECIBIDA) shouldBe true
        SolicitudFlujo.puedeAsignar(SolicitudFlujo.EN_REVISION) shouldBe true
        SolicitudFlujo.puedeAsignar(SolicitudFlujo.EN_PROCESO) shouldBe false
        SolicitudFlujo.puedeAsignar(SolicitudFlujo.CERRADA) shouldBe false
    }
})
