package com.example.pae3

import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class VehiculoAdapter(
    private var lista: List<Vehiculo>,
    private val rol: String,
    private val db: AyudanteBaseDatos,
    private val clickReserva: (Vehiculo) -> Unit // Lambda para el clic
) : RecyclerView.Adapter<VehiculoAdapter.VehiculoViewHolder>() {

    class VehiculoViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val txtInfo = v.findViewById<TextView>(R.id.txtInfoVehiculo)
        val txtPrecio = v.findViewById<TextView>(R.id.txtPrecioVehiculo)
        val btnRes = v.findViewById<Button>(R.id.btnReservar)
        val btnEli = v.findViewById<Button>(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehiculoViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_vehiculo, parent, false)
        return VehiculoViewHolder(v)
    }

    override fun onBindViewHolder(holder: VehiculoViewHolder, position: Int) {
        val veh = lista[position]
        holder.txtInfo.text = "${veh.marca} ${veh.modelo} [${veh.placa}]"
        holder.txtPrecio.text = "Tarifa: $${veh.precio}/día"

        if (rol == "ADMIN") {
            holder.btnRes.visibility = View.GONE
            holder.btnEli.visibility = View.VISIBLE
            holder.btnEli.setOnClickListener {
                db.eliminarVehiculo(veh.id)
                actualizarLista(db.obtenerVehiculos(false))
            }
        } else {
            holder.btnRes.visibility = View.VISIBLE
            holder.btnEli.visibility = View.GONE
            holder.btnRes.setOnClickListener { clickReserva(veh) }
        }
    }

    override fun getItemCount() = lista.size

    // Nombre de función unificado para evitar errores de compilación
    fun actualizarLista(nuevaLista: List<Vehiculo>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}