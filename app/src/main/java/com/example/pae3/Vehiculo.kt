package com.example.pae3

// Clase que representa el objeto Vehículo en nuestro sistema
data class Vehiculo(
    val id: Int? = null,
    val marca: String,
    val modelo: String,
    val placa: String,
    val precio: Double
)