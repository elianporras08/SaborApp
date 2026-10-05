package com.senati.saborapp

import android.content.Intent
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

        // Compartir la cuenta por WhatsApp / Apps de mensajería (HU-11, CA1, CA2, CA3)
        binding.btnCompartirWhatsApp.setOnClickListener {
            val pedido = pedidoActual
            val sb = StringBuilder()
            sb.append("Pollería El Buen Sabor\n")
            sb.append("Cuenta - Mesa $numeroMesa\n")

            if (pedido != null) {
                sb.append("Pedido #${pedido.id} · ${pedido.fecha}\n\n")
                val detalles = pedidoDao.listarDetallesPedido(pedido.id)
                for (d in detalles) {
                    sb.append("${d.cantidad} x ${d.nombrePlato} - S/ %.2f\n".format(d.subtotal))
                }
                sb.append("\nTotal: S/ %.2f\n".format(pedido.total))
            } else {
                sb.append("Total: S/ 0.00\n")
            }
            sb.append("¡Gracias por su preferencia!")

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, sb.toString())
            }
            startActivity(Intent.createChooser(shareIntent, "Compartir cuenta"))
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
