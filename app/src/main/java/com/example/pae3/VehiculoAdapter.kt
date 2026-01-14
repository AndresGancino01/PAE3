package com.example.pae3

import android.app.DatePickerDialog
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import java.util.concurrent.TimeUnit

class VehiculoAdapter(private var lista: List<Vehiculo>, private val rol: String, private val usuarioLogueado: String) :
    RecyclerView.Adapter<VehiculoAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val txtInfo: TextView = v.findViewById(R.id.txt_item_info)
        val btnAccion: Button = v.findViewById(R.id.btn_item_alquilar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_vehiculo, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val v = lista[position]
        val db = AyudanteBaseDatos(holder.itemView.context)

        if (v.disponible == 1) {
            holder.txtInfo.text = "${v.marca} ${v.modelo}\nPlaca: ${v.placa}\n$${v.precio}/día - DISPONIBLE"
            holder.btnAccion.text = "RESERVAR"
            holder.btnAccion.setBackgroundColor(android.graphics.Color.parseColor("#2E7D32")) // Verde
            holder.btnAccion.setOnClickListener { mostrarPicker(holder, v, db) }
        } else {
            holder.txtInfo.text = "${v.marca} ${v.modelo}\nALQUILADO POR: ${v.cliente}\nTOTAL: $${v.costoTotal}"
            if (rol == "ADMIN") {
                holder.btnAccion.text = "LIBERAR"
                holder.btnAccion.setOnClickListener { db.liberarVehiculo(v.id!!); actualizar(db.obtenerVehiculos()) }
            } else {
                holder.btnAccion.text = "OCUPADO"
                holder.btnAccion.isEnabled = false
            }
        }
    }

    private fun mostrarPicker(holder: ViewHolder, v: Vehiculo, db: AyudanteBaseDatos) {
        val c = Calendar.getInstance()
        DatePickerDialog(holder.itemView.context, { _, y1, m1, d1 ->
            val f1 = Calendar.getInstance().apply { set(y1, m1, d1) }
            DatePickerDialog(holder.itemView.context, { _, y2, m2, d2 ->
                val f2 = Calendar.getInstance().apply { set(y2, m2, d2) }
                val dias = TimeUnit.MILLISECONDS.toDays(f2.timeInMillis - f1.timeInMillis)
                if (dias > 0) {
                    val total = dias * v.precio
                    db.realizarReserva(v.id!!, usuarioLogueado, "$d1/${m1+1}", "$d2/${m2+1}", total)
                    actualizar(db.obtenerVehiculos())
                    Toast.makeText(holder.itemView.context, "Costo: $$total por $dias días", Toast.LENGTH_LONG).show()
                }
            }, y1, m1, d1 + 1).show()
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
    }

    override fun getItemCount() = lista.size
    fun actualizar(nueva: List<Vehiculo>) { this.lista = nueva; notifyDataSetChanged() }
}