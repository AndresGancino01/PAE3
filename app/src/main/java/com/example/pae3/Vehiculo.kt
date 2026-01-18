package com.example.pae3

// Clase que define la estructura del objeto Vehículo
data class Vehiculo(
    val id: Int,
    val marca: String,
    val modelo: String,
    val placa: String,
    val precio: Double,
    val disponible: Int, // 1: Disponible, 0: Rentado
    val cliente: String = "",
    val fechaIni: String = "",
    val fechaFin: String = "",
    val total: Double = 0.0
)