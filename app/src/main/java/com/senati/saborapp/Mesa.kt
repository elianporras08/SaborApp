package com.senati.saborapp

data class Mesa(
    val id: Int,
    val numero: Int,
    val capacidad: Int,
    val estado: String // "LIBRE" o "OCUPADA"
)