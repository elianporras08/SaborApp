package com.senati.saborapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.senati.saborapp.databinding.ItemTopPlatoBinding

class TopPlatoAdapter(
    private val lista: List<TopPlato>,
    private val maxCantidad: Int
) : RecyclerView.Adapter<TopPlatoAdapter.TopPlatoViewHolder>() {

    inner class TopPlatoViewHolder(val binding: ItemTopPlatoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopPlatoViewHolder {
        val binding = ItemTopPlatoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopPlatoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopPlatoViewHolder, position: Int) {
        val item = lista[position]
        holder.binding.tvNombreTopPlato.text = item.nombre
        holder.binding.tvCantidadTopPlato.text = item.cantidad.toString()
        holder.binding.progressTopPlato.max = if (maxCantidad > 0) maxCantidad else 1
        holder.binding.progressTopPlato.progress = item.cantidad
    }

    override fun getItemCount(): Int = lista.size
}
