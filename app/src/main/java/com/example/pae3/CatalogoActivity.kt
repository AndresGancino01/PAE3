/**
 * Universidad Indoamérica
 * Desarrollo de Aplicaciones Móviles
 * Autores: Andrés Gancino - Diego García
 * PAE 3
 * Fecha de entrega: 19/01/2026
 */

package com.example.pae3

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Actividad encargada de mostrar el listado de vehículos disponibles y rentados
class CatalogoActivity : AppCompatActivity() {
    private lateinit var db: AyudanteBaseDatos
    private lateinit var adapt: VehiculoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogo)

        // Inicialización de la base de datos
        db = AyudanteBaseDatos(this)

        // Recepción de datos de sesión (Rol y Nombre) desde el Intent
        val rol = intent.getStringExtra("ROL") ?: ""
        val nom = intent.getStringExtra("NOMBRE") ?: ""

        // Configuración del RecyclerView para mostrar la lista de forma vertical
        val rv = findViewById<RecyclerView>(R.id.rvVehiculos)
        rv.layoutManager = LinearLayoutManager(this)

        /**
         * Inicialización del adaptador:
         * Se envía el nombre de usuario para que el sistema sepa qué vehículos
         * pertenecen a la sesión actual y mostrar el botón de "Entregar".
         */
        adapt = VehiculoAdapter(mutableListOf(), rol, nom, db) { cargar() }
        rv.adapter = adapt

        // Botón para regresar al menú principal (MainActivity)
        findViewById<Button>(R.id.btnVolverCatalogo).setOnClickListener { finish() }

        // Carga inicial de datos
        cargar()
    }

    /**
     * Función cargar():
     * Gestiona la lógica de visibilidad según el rol del usuario.
     */
    private fun cargar() {
        val rol = intent.getStringExtra("ROL") ?: ""
        val nom = intent.getStringExtra("NOMBRE") ?: ""

        // Obtención de la lista global de vehículos desde SQLite
        val listaCompleta = db.obtenerTodosLosVehiculos()

        if (rol == "ADMIN") {
            // El administrador visualiza absolutamente todas las unidades (Disponibles y Rentadas)
            adapt.actualizar(listaCompleta)
        } else {
            /**
             * Lógica de filtrado para clientes:
             * 1. Ven vehículos que tengan el estado 'disponible' (1).
             * 2. Ven vehículos que ellos mismos han rentado (cliente == nombre de usuario).
             * Esto evita que un cliente vea los autos rentados por otras personas.
             */
            val listaFiltrada = listaCompleta.filter { it.disponible == 1 || it.cliente == nom }
            adapt.actualizar(listaFiltrada)
        }
    }
}