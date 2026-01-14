package com.example.pae3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nombre = intent.getStringExtra("NOMBRE") ?: "Usuario"
        val rol = intent.getStringExtra("ROL") ?: "CLIENTE"

        findViewById<TextView>(R.id.txt_bienvenida).text = "Bienvenido, $nombre\nRol: $rol"

        findViewById<Button>(R.id.btn_ver_catalogo).setOnClickListener {
            val i = Intent(this, CatalogoActivity::class.java)
            i.putExtra("ROL", rol)
            i.putExtra("NOMBRE_U", nombre)
            startActivity(i)
        }

        findViewById<Button>(R.id.btn_cerrar_sesion).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}