package com.example.dsy1105_005d_2026.util

/** Validaciones simples de formularios (kotlin puro, sin Android). */
object Validaciones {
    private val regexCorreo = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun esCorreoValido(correo: String): Boolean = regexCorreo.matches(correo.trim())

    fun esClaveValida(clave: String): Boolean = clave.length >= 6

    fun esNombreValido(texto: String): Boolean = texto.trim().length >= 2
}
