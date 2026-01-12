package com.example.pae3

data class Vehiculo(
    val id: Int? = null,
    val marca: String,
    val modelo: String,
    val placa: String,
    val precio: Double,
    val disponible: Int = 1 // 1 para sí, 0 para no
)