package com.example.pae3

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class HistorialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val listView = ListView(this)
        setContentView(listView)
        setTitle("Historial de Alquileres")

        val db = AyudanteBaseDatos(this)
        val lista = db.obtenerHistorial()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lista)
        listView.adapter = adapter
    }
}