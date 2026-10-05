package com.senati.saborapp

data class Plato(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val precio: Double,
    val disponible: Int // 1 para disponible, 0 para no disponible
)