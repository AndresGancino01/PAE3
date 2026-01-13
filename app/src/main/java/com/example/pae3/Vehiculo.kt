package com.example.pae3

data class Vehiculo(
    val id: Int? = null,
    val marca: String,
    val modelo: String,
    val placa: String,
    val precio: Double,
    var disponible: Int = 1, // 1: Sí, 0: No
    var nombreCliente: String? = null,
    var cedulaCliente: String? = null
)