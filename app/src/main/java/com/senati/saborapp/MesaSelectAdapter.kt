package com.senati.saborapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.senati.saborapp.databinding.ItemMesaBinding

class MesaSelectAdapter(
    private val listaMesas: List<Mesa>,
    private val idMesaSeleccionada: Int,
    private val onMesaSelected: (Mesa) -> Unit
) : RecyclerView.Adapter<MesaSelectAdapter.MesaViewHolder>() {

    inner class MesaViewHolder(val binding: ItemMesaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesaViewHolder {
        val binding = ItemMesaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MesaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MesaViewHolder, position: Int) {
        val mesa = listaMesas[position]
        holder.binding.tvNumeroMesa.text = "Mesa ${mesa.numero}"
        holder.binding.tvCapacidadMesa.text = "${mesa.capacidad} pers."
        holder.binding.tvEstadoMesa.text = mesa.estado

        // Colores según estado (HU-08, CA1)
        if (mesa.estado.equals("OCUPADA", ignoreCase = true)) {
            holder.binding.tvEstadoMesa.setTextColor(Color.parseColor("#D32F2F"))
        } else {
            holder.binding.tvEstadoMesa.setTextColor(Color.parseColor("#2E7D32"))
        }

        // Resaltado de la mesa seleccionada
        if (mesa.id == idMesaSeleccionada) {
            holder.binding.root.setStrokeColor(Color.parseColor("#E64A19"))
            holder.binding.root.strokeWidth = 4
        } else {
            holder.binding.root.setStrokeColor(Color.parseColor("#E0E0E0"))
            holder.binding.root.strokeWidth = 1
        }

        holder.itemView.setOnClickListener {
            onMesaSelected(mesa)
        }
    }

    override fun getItemCount(): Int = listaMesas.size
}
