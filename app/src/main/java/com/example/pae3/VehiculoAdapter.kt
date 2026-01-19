package com.example.pae3

import android.graphics.Color
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class VehiculoAdapter(
    private var lista: List<Vehiculo>,
    private val rol: String,
    private val nombreUsuario: String,
    private val db: AyudanteBaseDatos,
    private val onAction: () -> Unit
) : RecyclerView.Adapter<VehiculoAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val txtInfo = v.findViewById<TextView>(R.id.txtInfoVehiculo)
        val txtPrecio = v.findViewById<TextView>(R.id.txtPrecioVehiculo)
        val btnRes = v.findViewById<Button>(R.id.btnReservar)
        val btnEli = v.findViewById<Button>(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(p: ViewGroup, t: Int) = VH(LayoutInflater.from(p.context).inflate(R.layout.item_vehiculo, p, false))

    override fun onBindViewHolder(h: VH, p: Int) {
        val v = lista[p]
        h.txtInfo.text = "${v.marca} ${v.modelo} [${v.placa}]"

        if (v.disponible == 1) {
            h.txtPrecio.text = "ESTADO: DISPONIBLE\nTarifa: $${v.precio}/día"
            h.txtPrecio.setTextColor(Color.parseColor("#2E7D32"))
        } else {
            // MOSTRAMOS EL TOTAL CALCULADO AQUÍ
            h.txtPrecio.text = "RENTADO POR: ${v.cliente}\nTOTAL A PAGAR: $${v.total}\nENTREGA: ${v.fechaFin}"
            h.txtPrecio.setTextColor(Color.RED)
        }

        if (rol == "ADMIN") {
            h.btnRes.visibility = View.GONE
            h.btnEli.visibility = View.VISIBLE
            h.btnEli.setOnClickListener { db.eliminarVehiculo(v.id); onAction() }
        } else {
            h.btnEli.visibility = View.GONE
            if (v.disponible == 1) {
                h.btnRes.visibility = View.VISIBLE
                h.btnRes.text = "ALQUILAR"
                h.btnRes.setBackgroundColor(Color.parseColor("#1A237E"))
                h.btnRes.setOnClickListener { mostrarDialogoReserva(h.itemView.context, v) }
            } else if (v.cliente == nombreUsuario) {
                h.btnRes.visibility = View.VISIBLE
                h.btnRes.text = "ENTREGAR Y PAGAR"
                h.btnRes.setBackgroundColor(Color.BLACK)
                h.btnRes.setOnClickListener {
                    db.entregarVehiculo(v)
                    onAction()
                }
            } else {
                h.btnRes.visibility = View.GONE
            }
        }
    }

    private fun mostrarDialogoReserva(ctx: android.content.Context, v: Vehiculo) {
        val view = LayoutInflater.from(ctx).inflate(R.layout.dialog_reserva, null)
        androidx.appcompat.app.AlertDialog.Builder(ctx)
            .setTitle("Confirmar Renta")
            .setView(view)
            .setPositiveButton("CONFIRMAR") { _, _ ->
                val f1 = view.findViewById<EditText>(R.id.resFechaIni).text.toString()
                val f2 = view.findViewById<EditText>(R.id.resFechaFin).text.toString()

                if (f1.isNotEmpty() && f2.isNotEmpty()) {
                    val dias = calcularDias(f1, f2)
                    val totalCalculado = dias * v.precio
                    db.realizarReserva(v.id, nombreUsuario, f1, f2, totalCalculado)
                    onAction()
                }
            }.show()
    }

    // Función para calcular la diferencia de días entre fechas
    private fun calcularDias(inicio: String, fin: String): Long {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val d1 = sdf.parse(inicio)
            val d2 = sdf.parse(fin)
            val diff = d2!!.time - d1!!.time
            val res = diff / (1000 * 60 * 60 * 24)
            if (res <= 0) 1 else res // Mínimo 1 día
        } catch (e: Exception) { 1 }
    }

    override fun getItemCount() = lista.size
    fun actualizar(n: List<Vehiculo>) { lista = n; notifyDataSetChanged() }
}