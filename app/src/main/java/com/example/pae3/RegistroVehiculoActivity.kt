package com.example.pae3

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegistroVehiculoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_vehiculo)

        val db = AyudanteBaseDatos(this)

        val etMarca = findViewById<EditText>(R.id.et_marca)
        val etModelo = findViewById<EditText>(R.id.et_modelo)
        val etPlaca = findViewById<EditText>(R.id.et_placa)
        val etPrecio = findViewById<EditText>(R.id.et_precio)
        val btnGuardar = findViewById<Button>(R.id.btn_guardar_final)

        btnGuardar.setOnClickListener {
            val marca = etMarca.text.toString()
            val modelo = etModelo.text.toString()
            val placa = etPlaca.text.toString()
            val precioStr = etPrecio.text.toString()

            if (marca.isNotEmpty() && modelo.isNotEmpty() && placa.isNotEmpty() && precioStr.isNotEmpty()) {
                val nuevoVehiculo = Vehiculo(0, marca, modelo, placa, precioStr.toDouble(), 1, "", "")
                val res = db.insertarVehiculo(nuevoVehiculo)

                if (res != -1L) {
                    Toast.makeText(this, "Vehículo registrado exitosamente", Toast.LENGTH_SHORT).show()
                    // Limpiar campos
                    etMarca.text.clear()
                    etModelo.text.clear()
                    etPlaca.text.clear()
                    etPrecio.text.clear()
                } else {
                    Toast.makeText(this, "Error al guardar. Verifica la placa.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}