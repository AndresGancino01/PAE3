package com.example.pae3

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dbHelper = AyudanteBaseDatos(this)

        // Referencias a los componentes del XML
        val etMarca = findViewById<EditText>(R.id.et_marca)
        val etModelo = findViewById<EditText>(R.id.et_modelo)
        val etPlaca = findViewById<EditText>(R.id.et_placa)
        val etPrecio = findViewById<EditText>(R.id.et_precio)
        val btnGuardar = findViewById<Button>(R.id.btn_guardar)

        btnGuardar.setOnClickListener {
            val marca = etMarca.text.toString()
            val modelo = etModelo.text.toString()
            val placa = etPlaca.text.toString()
            val precio = etPrecio.text.toString().toDoubleOrNull()

            if (marca.isNotEmpty() && modelo.isNotEmpty() && placa.isNotEmpty() && precio != null) {
                val nuevoVehiculo = Vehiculo(marca = marca, modelo = modelo, placa = placa, precio = precio)
                val resultado = dbHelper.insertarVehiculo(nuevoVehiculo)

                if (resultado != -1L) {
                    Toast.makeText(this, "¡Vehículo guardado con éxito!", Toast.LENGTH_SHORT).show()
                    // Limpiar campos
                    etMarca.text.clear()
                    etModelo.text.clear()
                    etPlaca.text.clear()
                    etPrecio.text.clear()
                } else {
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}