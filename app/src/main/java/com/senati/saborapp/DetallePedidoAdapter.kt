package com.senati.saborapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.senati.saborapp.databinding.ItemDetallePedidoBinding

class DetallePedidoAdapter(
    private val listaDetalles: List<DetallePedido>
) : RecyclerView.Adapter<DetallePedidoAdapter.DetalleViewHolder>() {

    inner class DetalleViewHolder(val binding: ItemDetallePedidoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetalleViewHolder {
        val binding = ItemDetallePedidoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetalleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetalleViewHolder, position: Int) {
        val detalle = listaDetalles[position]
        holder.binding.tvCantidadNombre.text = "${detalle.cantidad} x ${detalle.nombrePlato}"
        holder.binding.tvSubtotal.text = "%.2f".format(detalle.subtotal)
    }

    override fun getItemCount(): Int = listaDetalles.size
}
