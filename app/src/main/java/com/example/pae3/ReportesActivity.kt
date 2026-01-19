package com.example.pae3

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ReportesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes)

        val db = AyudanteBaseDatos(this)
        val lv = findViewById<ListView>(R.id.lvReportesHistorial) // Cambiamos a ListView para simplicidad en reportes
        val btn = findViewById<Button>(R.id.btnVolverReportes)

        val datos = db.obtenerHistorial()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, datos)
        lv.adapter = adapter

        btn.setOnClickListener { finish() }
    }
}