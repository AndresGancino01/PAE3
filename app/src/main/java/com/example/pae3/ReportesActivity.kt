package com.example.pae3

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ReportesActivity : AppCompatActivity() {
    private lateinit var db: AyudanteBaseDatos
    private lateinit var adaptador: VehiculoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes)

        db = AyudanteBaseDatos(this)

        val rv = findViewById<RecyclerView>(R.id.rvReportes)
        rv.layoutManager = LinearLayoutManager(this)

        // CORRECCIÓN: Se pasan todos los parámetros requeridos por el constructor
        // 1. Lista vacía inicial, 2. Rol ADMIN, 3. Nombre vacío, 4. Instancia db, 5. Acción al actualizar
        adaptador = VehiculoAdapter(mutableListOf(), "ADMIN", "", db) {
            cargarDatosReporte()
        }
        rv.adapter = adaptador

        findViewById<Button>(R.id.btnVolverReportes).setOnClickListener { finish() }

        cargarDatosReporte()
    }

    private fun cargarDatosReporte() {
        // CORRECCIÓN: Usamos la función obtenerTodosLosVehiculos que creamos para la nueva lógica
        val listaCompleta = db.obtenerTodosLosVehiculos()

        // Filtramos para mostrar solo los que NO están disponibles (los rentados)
        val listaRentados = listaCompleta.filter { it.disponible == 0 }

        // CORRECCIÓN: Usamos el método 'actualizar' definido en el adaptador
        adaptador.actualizar(listaRentados)
    }
}