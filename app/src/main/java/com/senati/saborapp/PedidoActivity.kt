package com.senati.saborapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.senati.saborapp.databinding.ActivityPedidoBinding

class PedidoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPedidoBinding
    private lateinit var mesaDao: MesaDao
    private lateinit var platoDao: PlatoDao
    private lateinit var pedidoDao: PedidoDao

    private var mesaSeleccionada: Mesa? = null
    private var platosDisponibles: List<Plato> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mesaDao = MesaDao(this)
        platoDao = PlatoDao(this)
        pedidoDao = PedidoDao(this)

        binding.recyclerMesasPedido.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerDetallePedido.layoutManager = LinearLayoutManager(this)

        cargarSpinnerPlatos()

        // Botón Agregar Plato
        binding.btnAgregarPlato.setOnClickListener {
            val mesa = mesaSeleccionada
            if (mesa == null) {
                Toast.makeText(this, "Seleccione una mesa", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val posPlato = binding.spPlatosDisponibles.selectedItemPosition
            if (posPlato < 0 || posPlato >= platosDisponibles.size) {
                Toast.makeText(this, "Seleccione un plato disponible", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val plato = platosDisponibles[posPlato]

            val cantStr = binding.etCantidadPlato.text.toString().trim()
            val cantidad = cantStr.toIntOrNull()
            if (cantidad == null || cantidad <= 0) {
                binding.etCantidadPlato.error = "Cantidad inválida"
                return@setOnClickListener
            }

            // Transacción: Guardar plato en el pedido (HU-08, CA2, CA3, CA4)
            val exito = pedidoDao.agregarPlatoAPedido(mesa.id, plato.id, cantidad)
            if (exito) {
                Toast.makeText(this, "Plato agregado", Toast.LENGTH_SHORT).show()
                binding.etCantidadPlato.setText("1")
                cargarMesas()
                seleccionarMesa(mesaDao.listar().find { it.id == mesa.id } ?: mesa)
            } else {
                Toast.makeText(this, "Error al agregar el plato", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón Ver Cuenta (HU-09, CA1)
        binding.btnVerCuenta.setOnClickListener {
            val mesa = mesaSeleccionada
            if (mesa == null) {
                Toast.makeText(this, "Seleccione una mesa", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, CuentaActivity::class.java).apply {
                putExtra("EXTRA_MESA_ID", mesa.id)
                putExtra("EXTRA_MESA_NUMERO", mesa.numero)
            }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarMesas()
        cargarSpinnerPlatos()
        mesaSeleccionada?.let { m ->
            val mesaActualizada = mesaDao.listar().find { it.id == m.id }
            if (mesaActualizada != null) {
                seleccionarMesa(mesaActualizada)
            }
        }
    }

    private fun cargarMesas() {
        val listaMesas = mesaDao.listar()
        val adapter = MesaSelectAdapter(listaMesas, mesaSeleccionada?.id ?: -1) { mesa ->
            seleccionarMesa(mesa)
        }
        binding.recyclerMesasPedido.adapter = adapter
    }

    private fun cargarSpinnerPlatos() {
        // Solo platos con disponible = 1 (HU-07, CA4)
        platosDisponibles = platoDao.listarDisponibles()
        val nombresPlatos = platosDisponibles.map { "${it.nombre} (S/ %.2f)".format(it.precio) }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, nombresPlatos)
        binding.spPlatosDisponibles.adapter = adapter
    }

    private fun seleccionarMesa(mesa: Mesa) {
        mesaSeleccionada = mesa
        binding.tvMesaSeleccionada.text = "Mesa ${mesa.numero} · ${mesa.estado.lowercase()}"

        cargarMesas() // Para refrescar borde de selección

        val pedidoAbierto = pedidoDao.obtenerPedidoAbiertoPorMesa(mesa.id)
        if (pedidoAbierto != null) {
            val detalles = pedidoDao.listarDetallesPedido(pedidoAbierto.id)
            binding.recyclerDetallePedido.adapter = DetallePedidoAdapter(detalles)
            binding.tvTotalPedido.text = "S/ %.2f".format(pedidoAbierto.total)
        } else {
            binding.recyclerDetallePedido.adapter = DetallePedidoAdapter(emptyList())
            binding.tvTotalPedido.text = "S/ 0.00"
        }
    }
}
