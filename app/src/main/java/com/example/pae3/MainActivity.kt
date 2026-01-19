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
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// Actividad Principal: Actúa como el panel de control (Dashboard) del usuario
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Recepción de parámetros desde el Login para personalizar la interfaz
        val rol = intent.getStringExtra("ROL") ?: "CLIENTE"
        val nombre = intent.getStringExtra("NOMBRE") ?: "Usuario"

        // Vinculación de los elementos de la interfaz (XML) con el código (Kotlin)
        val txtBienvenida = findViewById<TextView>(R.id.txtInfoUser)
        val btnGestion = findViewById<Button>(R.id.btnGestionarFlota)
        val btnCatalogo = findViewById<Button>(R.id.btnVerCatalogo)
        val btnReportes = findViewById<Button>(R.id.btnVerReportes)
        val btnSalir = findViewById<Button>(R.id.btnCerrarSesion)

        // Actualización del mensaje de bienvenida con el nombre y rol real
        txtBienvenida.text = "Sesión activa: $nombre\nRol asignado: $rol"

        /**
         * LÓGICA DE SEGURIDAD Y VISIBILIDAD (RBAC - Role Based Access Control):
         * Si el usuario es ADMIN, se habilitan las funciones de gestión y auditoría.
         * Si es CLIENTE, las opciones administrativas se ocultan para mantener la integridad.
         */
        if (rol == "ADMIN") {
            btnGestion.visibility = View.VISIBLE
            btnReportes.visibility = View.VISIBLE
        } else {
            btnGestion.visibility = View.GONE
            btnReportes.visibility = View.GONE
        }

        /**
         * Navegación al Catálogo:
         * Disponible para ambos roles, permite ver e interactuar con la flota vehicular.
         */
        btnCatalogo.setOnClickListener {
            val i = Intent(this, CatalogoActivity::class.java)
            i.putExtra("ROL", rol)
            i.putExtra("NOMBRE", nombre)
            startActivity(i)
        }

        /**
         * Gestión de Flota:
         * Permite al administrador añadir nuevas unidades vehiculares a la base de datos.
         */
        btnGestion.setOnClickListener {
            startActivity(Intent(this, RegistroVehiculoActivity::class.java))
        }

        /**
         * Reportes de Renta:
         * Permite visualizar el historial de transacciones y rentas finalizadas.
         */
        btnReportes.setOnClickListener {
            startActivity(Intent(this, ReportesActivity::class.java))
        }

        /**
         * Cierre de Sesión:
         * Limpia la pila de actividades y regresa al usuario a la pantalla de autenticación.
         */
        btnSalir.setOnClickListener {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}