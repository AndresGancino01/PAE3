package com.example.pae3

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ReportesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes)

        val db = AyudanteBaseDatos(this)
        val listaVehiculos = db.obtenerVehiculos()

        // Cálculo de métricas
        val total = listaVehiculos.size
        val alquilados = listaVehiculos.count { it.disponible == 0 }
        val disponibles = listaVehiculos.count { it.disponible == 1 }

        // Sumamos el precio de solo los vehículos que están alquilados (estado 0)
        val ingresosActuales = listaVehiculos.filter { it.disponible == 0 }.sumOf { it.precio }

        // Asignar los valores a la interfaz
        findViewById<TextView>(R.id.txt_total_vehiculos).text = "Total en Flota: $total"
        findViewById<TextView>(R.id.txt_alquilados).text = "Vehículos Alquilados: $alquilados"
        findViewById<TextView>(R.id.txt_disponibles).text = "Vehículos Disponibles: $disponibles"
        findViewById<TextView>(R.id.txt_ganancias_potenciales).text = "$$ingresosActuales"
    }
}