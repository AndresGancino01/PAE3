package com.example.pae3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val db = AyudanteBaseDatos(this)
        val etUser = findViewById<EditText>(R.id.et_usuario)
        val etPass = findViewById<EditText>(R.id.et_password)
        val btnEntrar = findViewById<Button>(R.id.btn_entrar)
        val tvRegistrar = findViewById<TextView>(R.id.tv_registrar)

        btnEntrar.setOnClickListener {
            val user = etUser.text.toString()
            val pass = etPass.text.toString()
            val rol = db.validarUsuario(user, pass)

            if (rol != null) {
                // Guardamos la sesión
                val prefs = getSharedPreferences("Sesion", MODE_PRIVATE).edit()
                prefs.putString("rol", rol)
                prefs.apply()

                Toast.makeText(this, "Bienvenido como $rol", Toast.LENGTH_SHORT).show()

                when (rol) {
                    "ADMIN", "OPERADOR" -> startActivity(Intent(this, MainActivity::class.java))
                    "CLIENTE" -> startActivity(Intent(this, CatalogoActivity::class.java))
                }
                finish()
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

        tvRegistrar.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Registro de Cliente")
            val layout = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(50, 30, 50, 30)
            }
            val regUser = EditText(this).apply { hint = "Usuario" }
            val regPass = EditText(this).apply { hint = "Contraseña"; inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD }
            layout.addView(regUser)
            layout.addView(regPass)
            builder.setView(layout)

            builder.setPositiveButton("Registrar") { _, _ ->
                if (regUser.text.isNotEmpty() && regPass.text.isNotEmpty()) {
                    db.registrarUsuario(regUser.text.toString(), regPass.text.toString(), "CLIENTE")
                    Toast.makeText(this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Cancelar", null)
            builder.show()
        }
    }
}