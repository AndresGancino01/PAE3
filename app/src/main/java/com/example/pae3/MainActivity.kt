package com.example.pae3

import android.content.Intent
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

        val etMarca = findViewById<EditText>(R.id.et_marca)
        val etModelo = findViewById<EditText>(R.id.et_modelo)
        val etPlaca = findViewById<EditText>(R.id.et_placa)
        val etPrecio = findViewById<EditText>(R.id.et_precio)
        val btnGuardar = findViewById<Button>(R.id.btn_guardar)
        val btnVerCatalogo = findViewById<Button>(R.id.btn_ver_catalogo)

        btnGuardar.setOnClickListener {
            val marca = etMarca.text.toString()
            val modelo = etModelo.text.toString()
            val placa = etPlaca.text.toString()
            val precio = etPrecio.text.toString().toDoubleOrNull()

            if (marca.isNotEmpty() && modelo.isNotEmpty() && placa.isNotEmpty() && precio != null) {
                val resultado = dbHelper.insertarVehiculo(Vehiculo(null, marca, modelo, placa, precio))
                if (resultado != -1L) {
                    Toast.makeText(this, "Guardado con éxito", Toast.LENGTH_SHORT).show()
                    etMarca.text.clear(); etModelo.text.clear(); etPlaca.text.clear(); etPrecio.text.clear()
                }
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Abre la pantalla del catálogo
        btnVerCatalogo.setOnClickListener {
            startActivity(Intent(this, CatalogoActivity::class.java))
        }
    }
}