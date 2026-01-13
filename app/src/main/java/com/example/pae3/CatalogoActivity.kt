package com.example.pae3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CatalogoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogo)

        val rv = findViewById<RecyclerView>(R.id.rv_catalogo)
        val db = AyudanteBaseDatos(this)

        rv.layoutManager = LinearLayoutManager(this)
        // Pasamos tanto la lista como la base de datos (db)
        rv.adapter = VehiculoAdapter(db.obtenerVehiculos(), db)
    }
}