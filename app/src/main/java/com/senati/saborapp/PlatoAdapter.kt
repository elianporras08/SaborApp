package com.senati.saborapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.senati.saborapp.databinding.ItemPlatoBinding

class PlatoAdapter(
    private val listaPlatos: List<Plato>,
    private val onItemClick: ((Plato) -> Unit)? = null
) : RecyclerView.Adapter<PlatoAdapter.PlatoViewHolder>() {

    inner class PlatoViewHolder(val binding: ItemPlatoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatoViewHolder {
        val binding = ItemPlatoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlatoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlatoViewHolder, position: Int) {
        val plato = listaPlatos[position]
        holder.binding.tvNombrePlato.text = plato.nombre
        holder.binding.tvCategoriaPrecioPlato.text = "${plato.categoria} · S/ %.2f".format(plato.precio)

        if (plato.disponible == 1) {
            holder.binding.tvEstadoPlato.text = "Disponible"
            holder.binding.tvEstadoPlato.setTextColor(holder.itemView.context.getColor(android.R.color.holo_green_dark))
        } else {
            holder.binding.tvEstadoPlato.text = "Agotado"
            holder.binding.tvEstadoPlato.setTextColor(holder.itemView.context.getColor(android.R.color.holo_red_dark))
        }

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(plato)
        }
    }

    override fun getItemCount(): Int = listaPlatos.size
}
