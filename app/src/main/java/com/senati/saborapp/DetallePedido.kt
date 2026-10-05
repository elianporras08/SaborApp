package com.senati.saborapp

data class DetallePedido(
    val id: Int = 0,
    val idPedido: Int,
    val idPlato: Int,
    val nombrePlato: String = "",
    val cantidad: Int,
    val precioUnit: Double,
    val subtotal: Double
)
