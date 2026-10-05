package com.senati.saborapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.senati.saborapp.databinding.ActivityReportesBinding

class ReportesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportesBinding
    private lateinit var reporteDao: ReporteDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReportesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reporteDao = ReporteDao(this)

        binding.recyclerTopPlatos.layoutManager = LinearLayoutManager(this)

        cargarReporte()

        // Compartir resumen por Intent (HU-10)
        binding.btnCompartirResumen.setOnClickListener {
            val totalVentas = reporteDao.ventaDelDia()
            val totalPedidos = reporteDao.totalPedidosDelDia()
            val ticketPromedio = reporteDao.ticketPromedioDelDia()
            val topPlatos = reporteDao.topPlatosDelDia()

            val sb = StringBuilder()
            sb.append("Pollería El Buen Sabor - Resumen de Ventas de Hoy\n")
            sb.append("Total Ventas: S/ %.2f\n".format(totalVentas))
            sb.append("Total Pedidos: $totalPedidos\n")
            sb.append("Ticket Promedio: S/ %.2f\n\n".format(ticketPromedio))
            sb.append("Platos más pedidos:\n")
            if (topPlatos.isEmpty()) {
                sb.append("Sin ventas hoy")
            } else {
                for (p in topPlatos) {
                    sb.append("- ${p.nombre}: ${p.cantidad} und.\n")
                }
            }

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, sb.toString())
            }
            startActivity(Intent.createChooser(shareIntent, "Compartir resumen de ventas"))
        }
    }

    private fun cargarReporte() {
        val ventas = reporteDao.ventaDelDia()
        val pedidos = reporteDao.totalPedidosDelDia()
        val ticketPromedio = reporteDao.ticketPromedioDelDia()
        val topPlatos = reporteDao.topPlatosDelDia()

        binding.tvVentaHoy.text = "S/ %.2f".format(ventas)
        binding.tvCantidadPedidosHoy.text = pedidos.toString()
        binding.tvTicketPromedio.text = "S/ %.2f".format(ticketPromedio)

        // HU-10, CA3: Si no hay ventas hoy, muestra "Sin ventas hoy"
        if (topPlatos.isEmpty()) {
            binding.tvSinVentasHoy.visibility = View.VISIBLE
            binding.recyclerTopPlatos.adapter = TopPlatoAdapter(emptyList(), 0)
        } else {
            binding.tvSinVentasHoy.visibility = View.GONE
            val maxCantidad = topPlatos.maxOf { it.cantidad }
            binding.recyclerTopPlatos.adapter = TopPlatoAdapter(topPlatos, maxCantidad)
        }
    }
}
