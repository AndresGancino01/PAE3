package com.example.pae3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vehiculo, parent, false)
        return VehiculoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehiculoViewHolder, position: Int) {
        val v = listaVehiculos[position]
        val estaDisponible = v.disponible == 1

        holder.txtMarcaModelo.text = "${v.marca} ${v.modelo}"
        holder.txtDetalles.text = if (estaDisponible) "Placa: ${v.placa} | $${v.precio}/día"
        else "ALQUILADO A: ${v.nombreCliente}\nCédula: ${v.cedulaCliente}"

        holder.btnAlquilar.text = if (estaDisponible) "Alquilar" else "Devolver"
        holder.btnAlquilar.setBackgroundColor(if (estaDisponible) 0xFF2E7D32.toInt() else 0xFF1565C0.toInt())

        holder.btnAlquilar.setOnClickListener {
            if (estaDisponible) {
                val layout = LinearLayout(holder.itemView.context).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(50, 20, 50, 20)
                }
                val inputNombre = EditText(holder.itemView.context).apply { hint = "Nombre del Cliente" }
                val inputCedula = EditText(holder.itemView.context).apply { hint = "Número de Cédula" }
                layout.addView(inputNombre)
                layout.addView(inputCedula)

                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Registrar Alquiler")
                    .setView(layout)
                    .setPositiveButton("Confirmar") { _, _ ->
                        val nombre = inputNombre.text.toString()
                        val cedula = inputCedula.text.toString()
                        if (nombre.isNotEmpty() && cedula.isNotEmpty()) {
                            db.alquilarVehiculo(v.id!!, nombre, cedula)
                            actualizarLista(db.obtenerVehiculos())
                        }
                    }.setNegativeButton("Cancelar", null).show()
            } else {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Devolución")
                    .setMessage("¿Confirmar devolución de ${v.marca}?")
                    .setPositiveButton("Sí, Devolver") { _, _ ->
                        val resumen = "Devolución: ${v.marca} ${v.modelo} (Placa: ${v.placa}) - Cliente: ${v.nombreCliente}"
                        db.devolverVehiculo(v.id!!, resumen)
                        actualizarLista(db.obtenerVehiculos())
                        Toast.makeText(holder.itemView.context, "Historial actualizado", Toast.LENGTH_SHORT).show()
                    }.setNegativeButton("No", null).show()
            }
        }

        holder.btnEliminar.setOnClickListener {
            db.eliminarVehiculo(v.id!!)
            actualizarLista(db.obtenerVehiculos())
        }
    }

    override fun getItemCount() = listaVehiculos.size

    fun actualizarLista(nuevaLista: List<Vehiculo>) {
        this.listaVehiculos = nuevaLista
        notifyDataSetChanged()
    }
}