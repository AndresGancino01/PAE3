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

        // Referencias de la interfaz de Login
        val etCorreo = findViewById<EditText>(R.id.et_usuario) // Usamos el ID de tu XML para el correo
        val etPass = findViewById<EditText>(R.id.et_password)
        val btnEntrar = findViewById<Button>(R.id.btn_entrar)
        val tvRegistrar = findViewById<TextView>(R.id.tv_registrar)

        // Acción al presionar ENTRAR
        btnEntrar.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val pass = etPass.text.toString().trim()

            if (correo.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                val datosUsuario = db.validarLogin(correo, pass)
                if (datosUsuario != null) {
                    // Guardar sesión en SharedPreferences
                    val prefs = getSharedPreferences("Sesion", MODE_PRIVATE).edit()
                    prefs.putString("rol", datosUsuario["rol"])
                    prefs.putString("nombre", datosUsuario["nombre"])
                    prefs.putString("apellido", datosUsuario["apellido"])
                    prefs.apply()

                    Toast.makeText(this, "Bienvenido ${datosUsuario["nombre"]}", Toast.LENGTH_SHORT).show()

                    // Redirección según ROL
                    when (datosUsuario["rol"]) {
                        "ADMIN", "OPERADOR" -> {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                        "CLIENTE" -> {
                            val intent = Intent(this, CatalogoActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    finish() // Cierra el login para que no puedan volver atrás
                } else {
                    Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Acción al presionar "REGISTRATE AQUÍ"
        tvRegistrar.setOnClickListener {
            mostrarDialogoRegistroCompleto(db)
        }
    }

    private fun mostrarDialogoRegistroCompleto(db: AyudanteBaseDatos) {
        val builder = AlertDialog.Builder(this)
        val vista = layoutInflater.inflate(R.layout.dialog_registro, null)
        builder.setView(vista)
        val dialog = builder.create()

        val btnGuardar = vista.findViewById<Button>(R.id.btn_finalizar_registro)

        btnGuardar.setOnClickListener {
            val nom = vista.findViewById<EditText>(R.id.reg_nombre).text.toString().trim()
            val ape = vista.findViewById<EditText>(R.id.reg_apellido).text.toString().trim()
            val mail = vista.findViewById<EditText>(R.id.reg_correo).text.toString().trim()
            val p1 = vista.findViewById<EditText>(R.id.reg_pass1).text.toString().trim()
            val p2 = vista.findViewById<EditText>(R.id.reg_pass2).text.toString().trim()

            // Validaciones de Registro
            if (nom.isEmpty() || ape.isEmpty() || mail.isEmpty() || p1.isEmpty() || p2.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            } else if (!mail.contains("@") || !mail.contains(".")) {
                Toast.makeText(this, "Correo electrónico no válido", Toast.LENGTH_SHORT).show()
            } else if (p1 != p2) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            } else if (p1.length < 4) {
                Toast.makeText(this, "La contraseña debe tener al menos 4 caracteres", Toast.LENGTH_SHORT).show()
            } else {
                val resultado = db.registrarUsuario(nom, ape, mail, p1, "CLIENTE")
                if (resultado != -1L) {
                    Toast.makeText(this, "¡Cuenta creada! Inicia sesión ahora", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Este correo ya está registrado", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show()
    }
}