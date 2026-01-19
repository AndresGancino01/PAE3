/**
 * Universidad Indoamérica
 * Desarrollo de Aplicaciones Móviles
 * Autores: Andrés Gancino - Diego García
 * PAE 3
 * Fecha de entrega: 19/01/2026
 */

package com.example.pae3

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

// Actividad de Auditoría: Permite al administrador visualizar el historial de rentas y pagos
class ReportesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes)

        // Inicialización de la base de datos para recuperar el historial persistente
        val db = AyudanteBaseDatos(this)

        // Referencia al ListView que mostrará el listado de transacciones
        val lv = findViewById<ListView>(R.id.lvReportesHistorial)
        val btn = findViewById<Button>(R.id.btnVolverReportes)

        /**
         * Obtención de Datos:
         * Se recupera la lista de strings formateada desde la tabla 'historial' de SQLite.
         */
        val datos = db.obtenerHistorial()

        /**
         * Adaptador de Lista:
         * Se utiliza un ArrayAdapter sencillo para proyectar los datos del historial
         * en el componente visual ListView.
         */
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, datos)
        lv.adapter = adapter

        // Finaliza la actividad para regresar al panel de control principal
        btn.setOnClickListener { finish() }
    }
}