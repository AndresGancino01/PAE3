/**
 * Universidad Indoamérica
 * Desarrollo de Aplicaciones Móviles
 * Autores: Andrés Gancino - Diego García
 * PAE 3
 * Fecha de entrega: 19/01/2026
 */

package com.example.pae3

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

// Actividad Administrativa: Permite el ingreso de nuevas unidades vehiculares a la flota
class RegistroVehiculoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_vehiculo)

        // Inicialización de la base de datos para realizar la inserción
        val db = AyudanteBaseDatos(this)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarVehiculo)

        /**
         * Lógica de Guardado:
         * Captura los datos de la interfaz, los valida y los persiste en SQLite.
         */
        btnGuardar.setOnClickListener {
            // Extracción de datos desde los campos de texto (EditText)
            val ma = findViewById<EditText>(R.id.edtMarca).text.toString()
            val mo = findViewById<EditText>(R.id.edtModelo).text.toString()
            val pl = findViewById<EditText>(R.id.edtPlaca).text.toString()
            val prStr = findViewById<EditText>(R.id.edtPrecio).text.toString()

            // Conversión segura de la tarifa diaria a valor numérico (Double)
            val pr = prStr.toDoubleOrNull() ?: 0.0

            // Validación básica: Marca y Placa son obligatorios para el registro
            if (ma.isNotEmpty() && pl.isNotEmpty()) {
                /**
                 * Creación del objeto Vehiculo:
                 * El ID se envía como 0 (autoincremental) y el estado 'disponible' como 1.
                 */
                val resultado = db.insertarVehiculo(Vehiculo(0, ma, mo, pl, pr, 1))

                // Verificación del éxito de la operación en la base de datos
                if (resultado != -1L) {
                    Toast.makeText(this, "Vehículo Guardado Exitosamente", Toast.LENGTH_SHORT).show()
                    finish() // Cierra la actividad y regresa al Panel Principal
                } else {
                    Toast.makeText(this, "Error al guardar en la base de datos", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Notificación al usuario si faltan datos esenciales
                Toast.makeText(this, "Por favor, complete los campos obligatorios", Toast.LENGTH_SHORT).show()
            }
        }
    }
}