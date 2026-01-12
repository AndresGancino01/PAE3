package com.example.pae3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class VehiculoAdapter(private var listaVehiculos: List<Vehiculo>, private val db: AyudanteBaseDatos) :
    RecyclerView.Adapter<VehiculoAdapter.VehiculoViewHolder>() {

    class VehiculoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtMarcaModelo: TextView = view.findViewById(R.id.txt_item_titulo)
        val txtDetalles: TextView = view.findViewById(R.id.txt_item_subtitulo)
        val btnAlquilar: Button = view.findViewById(R.id.btn_item_alquilar)
        val btnEliminar: Button = view.findViewById(R.id.btn_item_eliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehiculoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vehiculo, parent, false)
        return VehiculoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehiculoViewHolder, position: Int) {
        val v = listaVehiculos[position]
        val estaDisponible = v.disponible == 1

        holder.txtMarcaModelo.text = "${v.marca} ${v.modelo}"
        holder.txtDetalles.text = "Placa: ${v.placa} | Precio: $${v.precio}"

        // Cambiar texto del botón según estado
        holder.btnAlquilar.text = if (estaDisponible) "Alquilar" else "Devolver"
        holder.btnAlquilar.setBackgroundColor(if (estaDisponible) 0xFF2E7D32.toInt() else 0xFF1565C0.toInt())

        // ACCIÓN: ALQUILAR / CANCELAR
        holder.btnAlquilar.setOnClickListener {
            val accion = if (estaDisponible) "ALQUILAR" else "DEVOLVER"
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Confirmar Acción")
                .setMessage("¿Está seguro que desea $accion este vehículo (${v.marca})?")
                .setPositiveButton("SÍ, OK") { _, _ ->
                    val nuevoEstado = if (estaDisponible) 0 else 1
                    db.cambiarDisponibilidad(v.id!!, nuevoEstado)
                    Toast.makeText(holder.itemView.context, "Operación exitosa", Toast.LENGTH_SHORT).show()
                    actualizarLista(db.obtenerVehiculos())
                }
                .setNegativeButton("CANCELAR", null)
                .show()
        }

        // ACCIÓN: ELIMINAR
        holder.btnEliminar.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("¡ALERTA!")
                .setMessage("¿Realmente desea ELIMINAR este carro del sistema? Esta acción no se puede deshacer.")
                .setPositiveButton("SÍ, ELIMINAR") { _, _ ->
                    db.eliminarVehiculo(v.id!!)
                    Toast.makeText(holder.itemView.context, "Vehículo borrado", Toast.LENGTH_SHORT).show()
                    actualizarLista(db.obtenerVehiculos())
                }
                .setNegativeButton("CANCELAR", null)
                .setIcon(android.R.drawable.ic_delete)
                .show()
        }
    }

    override fun getItemCount() = listaVehiculos.size

    fun actualizarLista(nuevaLista: List<Vehiculo>) {
        this.listaVehiculos = nuevaLista
        notifyDataSetChanged()
    }
}