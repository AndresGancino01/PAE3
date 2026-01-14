package com.example.pae3

data class Vehiculo(
    val id: Int? = null,
    val marca: String,
    val modelo: String,
    val placa: String,
    val precio: Double,
    val disponible: Int, // 1 disponible, 0 alquilado
    val cliente: String = "",
    val cedula: String = "",
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val costoTotal: Double = 0.0
)