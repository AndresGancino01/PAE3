package com.example.pae3

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class RegistroVehiculoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_vehiculo)

        val db = AyudanteBaseDatos(this)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarVehiculo)

        btnGuardar.setOnClickListener {
            val ma = findViewById<EditText>(R.id.edtMarca).text.toString()
            val mo = findViewById<EditText>(R.id.edtModelo).text.toString()
            val pl = findViewById<EditText>(R.id.edtPlaca).text.toString()
            val prStr = findViewById<EditText>(R.id.edtPrecio).text.toString()
            val pr = prStr.toDoubleOrNull() ?: 0.0

            if (ma.isNotEmpty() && pl.isNotEmpty()) {
                val resultado = db.insertarVehiculo(Vehiculo(0, ma, mo, pl, pr, 1))
                // Corregido: Comparación válida con el ID generado (Long)
                if (resultado != -1L) {
                    Toast.makeText(this, "Vehículo Guardado", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                Toast.makeText(this, "Complete los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}