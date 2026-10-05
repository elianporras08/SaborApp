package com.senati.saborapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.senati.saborapp.databinding.ItemMesaBinding

class MesaAdapter(private val listaMesas: List<Mesa>) : RecyclerView.Adapter<MesaAdapter.MesaViewHolder>() {

    inner class MesaViewHolder(val binding: ItemMesaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesaViewHolder {
        val binding = ItemMesaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MesaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MesaViewHolder, position: Int) {
        val mesa = listaMesas[position]
        holder.binding.tvNumeroMesa.text = "Mesa ${mesa.numero}"
        holder.binding.tvCapacidadMesa.text = "Cap: ${mesa.capacidad} pers."
        holder.binding.tvEstadoMesa.text = mesa.estado

        if (mesa.estado.equals("OCUPADA", ignoreCase = true)) {
            holder.binding.tvEstadoMesa.setTextColor(Color.parseColor("#D32F2F"))
        } else {
            holder.binding.tvEstadoMesa.setTextColor(Color.parseColor("#2E7D32"))
        }
    }

    override fun getItemCount(): Int = listaMesas.size
}
