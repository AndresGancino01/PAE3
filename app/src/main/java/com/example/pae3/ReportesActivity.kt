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

        // Inicializamos el adaptador en modo "ADMIN" para que no muestre el botón de reserva
        // Pasamos una función vacía {} ya que en reportes no se reserva nada
        adaptador = VehiculoAdapter(mutableListOf(), "ADMIN", db) { }
        rv.adapter = adaptador

        findViewById<Button>(R.id.btnVolverReportes).setOnClickListener { finish() }

        cargarDatosReporte()
    }

    private fun cargarDatosReporte() {
        // Obtenemos TODOS los vehículos (incluyendo los que NO están disponibles)
        // El parámetro 'false' indica que no queremos filtrar solo por disponibles
        val listaCompleta = db.obtenerVehiculos(false)

        // Filtramos para mostrar solo aquellos que ya han sido rentados (disponible == 0)
        val listaRentados = listaCompleta.filter { it.disponible == 0 }

        adaptador.actualizarLista(listaRentados)
    }
}