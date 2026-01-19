/**
 * Universidad Indoamérica
 * Desarrollo de Aplicaciones Móviles
 * Autores: Andrés Gancino - Diego García
 * PAE 3
 * Fecha de entrega: 19/01/2026
 */

package com.example.pae3

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

// Actividad de Control de Acceso: Gestiona el ingreso y registro de nuevos usuarios
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicialización del ayudante de base de datos SQLite
        val db = AyudanteBaseDatos(this)

        // Referencias a los componentes de la interfaz de usuario
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)
        val btnIrRegistro = findViewById<Button>(R.id.btnIrRegistro)

        /**
         * Lógica de Inicio de Sesión:
         * Valida las credenciales ingresadas contra la tabla de usuarios en SQLite.
         */
        btnEntrar.setOnClickListener {
            val correo = findViewById<EditText>(R.id.txtCorreoLogin).text.toString()
            val pass = findViewById<EditText>(R.id.txtPasswordLogin).text.toString()

            // Consulta a la base de datos para verificar existencia del usuario
            val usuario = db.validarLogin(correo, pass)

            if (usuario != null) {
                // Si el acceso es correcto, se navega al MainActivity enviando el ROL y NOMBRE
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("ROL", usuario["rol"])
                    putExtra("NOMBRE", usuario["nombre"])
                }
                startActivity(intent)
                finish() // Cierra la pantalla de login para que no se pueda regresar con el botón 'atrás'
            } else {
                // Notificación en caso de error en credenciales
                Toast.makeText(this, "Acceso denegado: Datos incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

        /**
         * Lógica de Registro de Usuarios:
         * Utiliza un Inflater para mostrar un cuadro de diálogo (AlertDialog) con el formulario de registro.
         */
        btnIrRegistro.setOnClickListener {
            val vistaRegistro = layoutInflater.inflate(R.layout.dialog_registro, null)

            AlertDialog.Builder(this)
                .setTitle("Crear Nueva Cuenta")
                .setView(vistaRegistro)
                .setPositiveButton("REGISTRAR") { _, _ ->
                    // Captura de datos desde la vista inflada del diálogo
                    val nom = vistaRegistro.findViewById<EditText>(R.id.regNombre).text.toString()
                    val cor = vistaRegistro.findViewById<EditText>(R.id.regCorreo).text.toString()
                    val pas = vistaRegistro.findViewById<EditText>(R.id.regPass).text.toString()

                    // Validación de campos no vacíos antes de la inserción
                    if (nom.isNotEmpty() && cor.isNotEmpty() && pas.isNotEmpty()) {
                        // Por defecto, todo usuario registrado desde el app tiene el rol "CLIENTE"
                        db.registrarUsuario(nom, cor, pas, "CLIENTE")
                        Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("CANCELAR", null).show()
        }
    }
}