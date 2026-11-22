package com.example.dsy1105_005d_2026.data.model

/** Elemento del catálogo externo de servicios (llega desde la API REST con Retrofit). */
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)
