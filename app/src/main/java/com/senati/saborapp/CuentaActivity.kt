package com.senati.saborapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.senati.saborapp.databinding.ActivityCuentaBinding

class CuentaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCuentaBinding
    private lateinit var pedidoDao: PedidoDao
    private var idMesa: Int = -1
    private var numeroMesa: Int = -1
    private var pedidoActual: Pedido? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCuentaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pedidoDao = PedidoDao(this)

        idMesa = intent.getIntExtra("EXTRA_MESA_ID", -1)
        numeroMesa = intent.getIntExtra("EXTRA_MESA_NUMERO", -1)

        binding.tvTituloCuenta.text = "Cuenta · Mesa $numeroMesa"
        binding.recyclerDetalleCuenta.layoutManager = LinearLayoutManager(this)

        cargarCuenta()

        // Botón Cerrar Cuenta en una transacción (HU-09, CA2)
        binding.btnCerrarCuenta.setOnClickListener {
            val pedido = pedidoActual
            if (pedido == null) {
                Toast.makeText(this, "No hay pedido abierto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val exito = pedidoDao.cerrarCuenta(pedido.id, idMesa)
            if (exito) {
                Toast.makeText(this, "Mesa $numeroMesa liberada", Toast.LENGTH_LONG).show()
                binding.btnCerrarCuenta.isEnabled = false
                finish() // Cierra la pantalla y regresa
            } else {
                Toast.makeText(this, "Error al cerrar la cuenta", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCompartirWhatsApp.setOnClickListener {
            Toast.makeText(this, "Función de compartir (Sprint 4)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarCuenta() {
        val pedido = pedidoDao.obtenerPedidoAbiertoPorMesa(idMesa)
        pedidoActual = pedido

        if (pedido != null) {
            binding.tvSubtituloCuenta.text = "Pedido #${pedido.id} · ${pedido.fecha}"
            val detalles = pedidoDao.listarDetallesPedido(pedido.id)
            binding.recyclerDetalleCuenta.adapter = DetallePedidoAdapter(detalles)
            binding.tvTotalCuenta.text = "S/ %.2f".format(pedido.total)
            binding.btnCerrarCuenta.isEnabled = true
        } else {
            binding.tvSubtituloCuenta.text = "No hay pedido abierto para esta mesa"
            binding.recyclerDetalleCuenta.adapter = DetallePedidoAdapter(emptyList())
            binding.tvTotalCuenta.text = "S/ 0.00"
            // CA3: Si la mesa está LIBRE o sin pedido, el botón está deshabilitado
            binding.btnCerrarCuenta.isEnabled = false
        }
    }
}
