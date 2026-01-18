package com.example.pae3

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val db = AyudanteBaseDatos(this)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)
        val btnIrRegistro = findViewById<Button>(R.id.btnIrRegistro)

        // Acción de Login
        btnEntrar.setOnClickListener {
            val correo = findViewById<EditText>(R.id.txtCorreoLogin).text.toString()
            val pass = findViewById<EditText>(R.id.txtPasswordLogin).text.toString()

            val usuario = db.validarLogin(correo, pass)
            if (usuario != null) {
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("ROL", usuario["rol"])
                    putExtra("NOMBRE", usuario["nombre"])
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Acceso denegado: Datos incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

        // Acción de Registro mediante Diálogo
        btnIrRegistro.setOnClickListener {
            val vistaRegistro = layoutInflater.inflate(R.layout.dialog_registro, null)
            AlertDialog.Builder(this)
                .setView(vistaRegistro)
                .setPositiveButton("REGISTRAR") { _, _ ->
                    val nom = vistaRegistro.findViewById<EditText>(R.id.regNombre).text.toString()
                    val cor = vistaRegistro.findViewById<EditText>(R.id.regCorreo).text.toString()
                    val pas = vistaRegistro.findViewById<EditText>(R.id.regPass).text.toString()

                    if (nom.isNotEmpty() && cor.isNotEmpty() && pas.isNotEmpty()) {
                        db.registrarUsuario(nom, cor, pas, "CLIENTE")
                        Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("CANCELAR", null).show()
        }
    }
}