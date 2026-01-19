package com.example.pae3

import android.graphics.Color
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

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
            h.txtPrecio.text = "Disponible - $${v.precio}/día"
            h.txtPrecio.setTextColor(Color.parseColor("#2E7D32"))
        } else {
            h.txtPrecio.text = "RENTADO POR: ${v.cliente}\nEntrega: ${v.fechaFin}"
            h.txtPrecio.setTextColor(Color.RED)
        }

        if (rol == "ADMIN") {
            h.btnRes.visibility = View.GONE
            h.btnEli.visibility = View.VISIBLE
            h.btnEli.setOnClickListener {
                db.eliminarVehiculo(v.id)
                onAction()
            }
        } else {
            h.btnEli.visibility = View.GONE
            if (v.disponible == 1) {
                h.btnRes.visibility = View.VISIBLE
                h.btnRes.text = "ALQUILAR"
                h.btnRes.setBackgroundColor(Color.parseColor("#1A237E"))
                h.btnRes.setOnClickListener { mostrarDialogoReserva(h.itemView.context, v) }
            } else if (v.cliente == nombreUsuario) {
                h.btnRes.visibility = View.VISIBLE
                h.btnRes.text = "ENTREGAR VEHÍCULO"
                h.btnRes.setBackgroundColor(Color.BLACK)
                h.btnRes.setOnClickListener {
                    db.entregarVehiculo(v.id)
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
            .setTitle("Rentar ${v.marca}")
            .setView(view)
            .setPositiveButton("Confirmar") { _, _ ->
                val f1 = view.findViewById<EditText>(R.id.resFechaIni).text.toString()
                val f2 = view.findViewById<EditText>(R.id.resFechaFin).text.toString()
                if (f1.isNotEmpty() && f2.isNotEmpty()) {
                    db.realizarReserva(v.id, nombreUsuario, f1, f2, v.precio)
                    onAction()
                }
            }.setNegativeButton("Cancelar", null).show()
    }

    override fun getItemCount() = lista.size

    // Método UNIFICADO para actualizar la lista
    fun actualizar(n: List<Vehiculo>) {
        lista = n
        notifyDataSetChanged()
    }
}