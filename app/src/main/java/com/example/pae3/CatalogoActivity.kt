package com.example.pae3

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CatalogoActivity : AppCompatActivity() {
    private lateinit var db: AyudanteBaseDatos
    private lateinit var adapt: VehiculoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogo)

        db = AyudanteBaseDatos(this)
        val rol = intent.getStringExtra("ROL") ?: ""
        val nom = intent.getStringExtra("NOMBRE") ?: ""

        val rv = findViewById<RecyclerView>(R.id.rvVehiculos)
        rv.layoutManager = LinearLayoutManager(this)

        // Enviamos el nombre de usuario al adaptador para comparar quién rentó qué
        adapt = VehiculoAdapter(mutableListOf(), rol, nom, db) { cargar() }
        rv.adapter = adapt

        findViewById<Button>(R.id.btnVolverCatalogo).setOnClickListener { finish() }
        cargar()
    }

    private fun cargar() {
        val rol = intent.getStringExtra("ROL") ?: ""
        val nom = intent.getStringExtra("NOMBRE") ?: ""
        val listaCompleta = db.obtenerTodosLosVehiculos()

        if (rol == "ADMIN") {
            adapt.actualizar(listaCompleta)
        } else {
            // El cliente ve los disponibles O los que él mismo tiene rentados
            val listaFiltrada = listaCompleta.filter { it.disponible == 1 || it.cliente == nom }
            adapt.actualizar(listaFiltrada)
        }
    }
}