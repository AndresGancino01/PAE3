package com.example.pae3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*

class CatalogoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogo)

        val rol = intent.getStringExtra("ROL") ?: "CLIENTE"
        val nombre = intent.getStringExtra("NOMBRE_U") ?: "Usuario"

        val rv = findViewById<RecyclerView>(R.id.rv_vehiculos)
        rv.layoutManager = LinearLayoutManager(this)

        val db = AyudanteBaseDatos(this)
        // Corregido: Enviamos 3 argumentos (lista, rol, nombre)
        rv.adapter = VehiculoAdapter(db.obtenerVehiculos(), rol, nombre)
    }
}