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

        val db = AyudanteBaseDatos(this)

        val etMarca = findViewById<EditText>(R.id.et_marca)
        val etModelo = findViewById<EditText>(R.id.et_modelo)
        val etPlaca = findViewById<EditText>(R.id.et_placa)
        val etPrecio = findViewById<EditText>(R.id.et_precio)

        findViewById<Button>(R.id.btn_guardar).setOnClickListener {
            val precio = etPrecio.text.toString().toDoubleOrNull()
            if (etMarca.text.isNotEmpty() && etPlaca.text.isNotEmpty() && precio != null) {
                db.insertarVehiculo(Vehiculo(null, etMarca.text.toString(), etModelo.text.toString(), etPlaca.text.toString(), precio))
                Toast.makeText(this, "Guardado correctamente", Toast.LENGTH_SHORT).show()
                etMarca.text.clear(); etModelo.text.clear(); etPlaca.text.clear(); etPrecio.text.clear()
            }
        }

        findViewById<Button>(R.id.btn_ver_catalogo).setOnClickListener {
            startActivity(Intent(this, CatalogoActivity::class.java))
        }

        findViewById<Button>(R.id.btn_ver_reportes).setOnClickListener {
            startActivity(Intent(this, ReportesActivity::class.java))
        }

        findViewById<Button>(R.id.btn_ver_historial).setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
        }



        val prefs = getSharedPreferences("Sesion", MODE_PRIVATE)
        val rol = prefs.getString("rol", "CLIENTE")

        val btnReportes = findViewById<Button>(R.id.btn_ver_reportes)
        val btnGuardar = findViewById<Button>(R.id.btn_guardar)

        if (rol == "OPERADOR") {
            btnReportes.visibility = android.view.View.GONE // No ve dinero
            btnGuardar.visibility = android.view.View.VISIBLE // Sí puede registrar autos nuevos si es necesario
        }
    }

}