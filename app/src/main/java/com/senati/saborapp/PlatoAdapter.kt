package com.senati.saborapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.senati.saborapp.databinding.ItemPlatoBinding

class PlatoAdapter(private val listaPlatos: List<Plato>) : RecyclerView.Adapter<PlatoAdapter.PlatoViewHolder>() {

    inner class PlatoViewHolder(val binding: ItemPlatoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatoViewHolder {
        val binding = ItemPlatoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlatoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlatoViewHolder, position: Int) {
        val plato = listaPlatos[position]
        holder.binding.tvNombrePlato.text = plato.nombre
        holder.binding.tvCategoriaPlato.text = plato.categoria
        holder.binding.tvPrecioPlato.text = "S/ %.2f".format(plato.precio)
    }

    override fun getItemCount(): Int = listaPlatos.size
}