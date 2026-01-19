/**
 * Universidad Indoamérica
 * Desarrollo de Aplicaciones Móviles
 * Autores: Andrés Gancino - Diego García
 * PAE 3
 * Fecha de entrega: 19/01/2026
 */

package com.example.pae3

/**
 * Clase de Datos (POJO): Representa la entidad Vehículo en el sistema.
 * Esta clase se utiliza para transportar datos entre la base de datos SQLite
 * y los componentes de la interfaz de usuario (RecyclerView).
 */
data class Vehiculo(
    val id: Int,              // Identificador único autoincremental en la base de datos
    val marca: String,        // Marca del fabricante del vehículo
    val modelo: String,       // Modelo o línea del vehículo
    val placa: String,        // Identificación única de la matrícula
    val precio: Double,       // Tarifa diaria de alquiler (precio por día)

    // ESTADO DE DISPONIBILIDAD
    // 1: El vehículo puede ser rentado por cualquier cliente
    // 0: El vehículo se encuentra ocupado actualmente
    val disponible: Int,

    // DATOS DINÁMICOS DE RENTA
    // Estos campos se llenan únicamente cuando el vehículo es alquilado
    val cliente: String = "",  // Nombre del usuario que realizó la reserva
    val fechaIni: String = "", // Fecha en la que inicia el alquiler
    val fechaFin: String = "", // Fecha pactada para la devolución
    val total: Double = 0.0    // Monto final calculado (Precio x días de uso)
)