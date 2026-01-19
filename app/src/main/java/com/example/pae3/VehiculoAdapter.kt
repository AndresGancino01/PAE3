/**
 * Universidad Indoamérica
 * Desarrollo de Aplicaciones Móviles
 * Autores: Andrés Gancino - Diego García
 * PAE 3
 * Fecha de entrega: 19/01/2026
 */

package com.example.pae3

import android.graphics.Color
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Adaptador del RecyclerView: Gestiona cómo se dibujan los datos en la lista
 * y maneja las interacciones (clics) según el rol del usuario.
 */
class VehiculoAdapter(
    private var lista: List<Vehiculo>,     // Lista de objetos tipo Vehiculo
    private val rol: String,                // Rol del usuario logueado (ADMIN/CLIENTE)
    private val nombreUsuario: String,      // Nombre del usuario para validar posesión de renta
    private val db: AyudanteBaseDatos,      // Instancia de la base de datos
    private val onAction: () -> Unit        // Función callback para refrescar la interfaz tras un cambio
) : RecyclerView.Adapter<VehiculoAdapter.VH>() {

    // Clase interna ViewHolder que mapea los elementos visuales del archivo item_vehiculo.xml
    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val txtInfo = v.findViewById<TextView>(R.id.txtInfoVehiculo)
        val txtPrecio = v.findViewById<TextView>(R.id.txtPrecioVehiculo)
        val btnRes = v.findViewById<Button>(R.id.btnReservar)
        val btnEli = v.findViewById<Button>(R.id.btnEliminar)
    }

    // Infla el diseño XML para cada fila de la lista
    override fun onCreateViewHolder(p: ViewGroup, t: Int) =
        VH(LayoutInflater.from(p.context).inflate(R.layout.item_vehiculo, p, false))

    // Vincula los datos del objeto Vehiculo con los TextViews y Buttons
    override fun onBindViewHolder(h: VH, p: Int) {
        val v = lista[p]
        h.txtInfo.text = "${v.marca} ${v.modelo} [${v.placa}]"

        // CONFIGURACIÓN VISUAL SEGÚN ESTADO
        if (v.disponible == 1) {
            h.txtPrecio.text = "ESTADO: DISPONIBLE\nTarifa: $${v.precio}/día"
            h.txtPrecio.setTextColor(Color.parseColor("#2E7D32")) // Verde para disponible
        } else {
            // Muestra el total calculado y el nombre del cliente que lo posee
            h.txtPrecio.text = "RENTADO POR: ${v.cliente}\nTOTAL A PAGAR: $${v.total}\nENTREGA: ${v.fechaFin}"
            h.txtPrecio.setTextColor(Color.RED) // Rojo para rentado
        }

        // CONTROL DE BOTONES POR ROL Y ESTADO
        if (rol == "ADMIN") {
            // El administrador solo puede eliminar vehículos de la flota
            h.btnRes.visibility = View.GONE
            h.btnEli.visibility = View.VISIBLE
            h.btnEli.setOnClickListener { db.eliminarVehiculo(v.id); onAction() }
        } else {
            h.btnEli.visibility = View.GONE

            if (v.disponible == 1) {
                // Caso: Vehículo disponible para cualquier cliente
                h.btnRes.visibility = View.VISIBLE
                h.btnRes.text = "ALQUILAR"
                h.btnRes.setBackgroundColor(Color.parseColor("#1A237E"))
                h.btnRes.setOnClickListener { mostrarDialogoReserva(h.itemView.context, v) }
            } else if (v.cliente == nombreUsuario) {
                // Caso: El cliente actual tiene rentado este vehículo
                h.btnRes.visibility = View.VISIBLE
                h.btnRes.text = "ENTREGAR Y PAGAR"
                h.btnRes.setBackgroundColor(Color.BLACK)
                h.btnRes.setOnClickListener {
                    db.entregarVehiculo(v) // Procesa devolución y guarda en historial
                    onAction()
                }
            } else {
                // Caso: Rentado por otra persona (no se muestran botones)
                h.btnRes.visibility = View.GONE
            }
        }
    }

    /**
     * Muestra el formulario para ingresar fechas de inicio y fin.
     * Calcula automáticamente el costo total antes de guardar la reserva.
     */
    private fun mostrarDialogoReserva(ctx: android.content.Context, v: Vehiculo) {
        val view = LayoutInflater.from(ctx).inflate(R.layout.dialog_reserva, null)
        androidx.appcompat.app.AlertDialog.Builder(ctx)
            .setTitle("Confirmar Renta")
            .setView(view)
            .setPositiveButton("CONFIRMAR") { _, _ ->
                val f1 = view.findViewById<EditText>(R.id.resFechaIni).text.toString()
                val f2 = view.findViewById<EditText>(R.id.resFechaFin).text.toString()

                if (f1.isNotEmpty() && f2.isNotEmpty()) {
                    // LÓGICA MATEMÁTICA: Días x Precio diario
                    val dias = calcularDias(f1, f2)
                    val totalCalculado = dias * v.precio
                    db.realizarReserva(v.id, nombreUsuario, f1, f2, totalCalculado)
                    onAction()
                }
            }.show()
    }

    /**
     * Calcula la diferencia de tiempo entre dos cadenas de fecha.
     * Utiliza milisegundos para determinar la cantidad de días exactos.
     */
    private fun calcularDias(inicio: String, fin: String): Long {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val d1 = sdf.parse(inicio)
            val d2 = sdf.parse(fin)
            val diff = d2!!.time - d1!!.time
            val res = diff / (1000 * 60 * 60 * 24)
            if (res <= 0) 1 else res // Si es el mismo día, se cobra mínimo 1 día
        } catch (e: Exception) { 1 }
    }

    override fun getItemCount() = lista.size

    // Método para actualizar los datos del adaptador y refrescar la lista visualmente
    fun actualizar(n: List<Vehiculo>) {
        lista = n
        notifyDataSetChanged()
    }
}