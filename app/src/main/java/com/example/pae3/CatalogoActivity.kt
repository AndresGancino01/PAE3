package com.example.pae3

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CatalogoActivity : AppCompatActivity() {
    private lateinit var db: AyudanteBaseDatos
    private lateinit var adaptador: VehiculoAdapter
    private var rolUsuario: String = "CLIENTE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogo)

        db = AyudanteBaseDatos(this)
        rolUsuario = intent.getStringExtra("ROL") ?: "CLIENTE"
        val nombreUsuario = intent.getStringExtra("NOMBRE") ?: ""

        val rv = findViewById<RecyclerView>(R.id.rvVehiculos)
        rv.layoutManager = LinearLayoutManager(this)

        // Corregido: Constructor del adaptador sin parámetros nombrados para evitar errores de tipos
        adaptador = VehiculoAdapter(mutableListOf(), rolUsuario, db) { vehiculo ->
            mostrarDialogoReserva(vehiculo, nombreUsuario)
        }
        rv.adapter = adaptador

        findViewById<Button>(R.id.btnVolverCatalogo).setOnClickListener { finish() }
        actualizarVista()
    }

    private fun actualizarVista() {
        // Corregido: Llamada a obtenerVehiculos con el parámetro requerido
        val lista = db.obtenerVehiculos(rolUsuario == "CLIENTE")
        adaptador.actualizarLista(lista) // Usa el nombre correcto: actualizarLista
    }

    private fun mostrarDialogoReserva(v: Vehiculo, nombreCliente: String) {
        // Asegúrate de que el archivo res/layout/dialog_reserva.xml existe
        val vista = layoutInflater.inflate(R.layout.dialog_reserva, null)

        AlertDialog.Builder(this)
            .setTitle("Confirmar Alquiler")
            .setView(vista)
            .setPositiveButton("CONFIRMAR") { _, _ ->
                val f1 = vista.findViewById<EditText>(R.id.resFechaIni).text.toString()
                val f2 = vista.findViewById<EditText>(R.id.resFechaFin).text.toString()

                if (f1.isNotEmpty() && f2.isNotEmpty()) {
                    db.realizarReserva(v.id, nombreCliente, f1, f2, v.precio)
                    Toast.makeText(this, "Reserva realizada", Toast.LENGTH_SHORT).show()
                    actualizarVista()
                }
            }
            .setNegativeButton("CANCELAR", null).show()
    }
}