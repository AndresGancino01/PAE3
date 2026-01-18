package com.example.pae3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Recuperamos los datos del usuario logueado
        val rol = intent.getStringExtra("ROL") ?: "CLIENTE"
        val nombre = intent.getStringExtra("NOMBRE") ?: "Usuario"

        // Referencias a los componentes del XML
        val txtBienvenida = findViewById<TextView>(R.id.txtInfoUser)
        val btnGestion = findViewById<Button>(R.id.btnGestionarFlota)
        val btnCatalogo = findViewById<Button>(R.id.btnVerCatalogo)
        val btnReportes = findViewById<Button>(R.id.btnVerReportes) // Botón para reportes
        val btnSalir = findViewById<Button>(R.id.btnCerrarSesion)

        txtBienvenida.text = "Sesión activa: $nombre\nRol asignado: $rol"

        // LÓGICA DE VISIBILIDAD: Solo el ADMIN ve opciones de gestión y reportes
        if (rol == "ADMIN") {
            btnGestion.visibility = View.VISIBLE
            btnReportes.visibility = View.VISIBLE
        } else {
            btnGestion.visibility = View.GONE
            btnReportes.visibility = View.GONE
        }

        // Navegación al Catálogo (Ambos roles pueden entrar)
        btnCatalogo.setOnClickListener {
            val i = Intent(this, CatalogoActivity::class.java)
            i.putExtra("ROL", rol)
            i.putExtra("NOMBRE", nombre)
            startActivity(i)
        }

        // Navegación a Registro de Vehículos (Solo Admin)
        btnGestion.setOnClickListener {
            startActivity(Intent(this, RegistroVehiculoActivity::class.java))
        }

        // Navegación a Reportes (Solo Admin)
        btnReportes.setOnClickListener {
            startActivity(Intent(this, ReportesActivity::class.java))
        }

        // Cerrar sesión y volver al Login
        btnSalir.setOnClickListener {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}