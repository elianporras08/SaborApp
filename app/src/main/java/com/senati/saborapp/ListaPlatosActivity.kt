package com.senati.saborapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.senati.saborapp.databinding.ActivityListaPlatosBinding

class ListaPlatosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaPlatosBinding
    private lateinit var platoDao: PlatoDao
    private var categoriaSeleccionada: String = "Todos"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListaPlatosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        platoDao = PlatoDao(this)

        binding.recyclerPlatos.layoutManager = LinearLayoutManager(this)

        // Buscador de platos (HU-07, CA3)
        binding.etBuscarPlato.doAfterTextChanged {
            cargarPlatos()
        }

        // Selección de chips de categoría
        binding.chipGroupCategorias.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chip = group.findViewById<Chip>(checkedIds[0])
                categoriaSeleccionada = chip?.text?.toString() ?: "Todos"
            } else {
                categoriaSeleccionada = "Todos"
            }
            cargarPlatos()
        }

        // Botón flotante para registrar un plato nuevo
        binding.fabAgregarPlato.setOnClickListener {
            val intent = Intent(this, RegistrarPlatoActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarPlatos()
    }

    private fun cargarPlatos() {
        val filtroNombre = binding.etBuscarPlato.text.toString().trim()
        val lista = platoDao.listar(filtroNombre, categoriaSeleccionada)

        val adapter = PlatoAdapter(lista) { plato ->
            // Abrir el formulario en modo edición (HU-07, CA1)
            val intent = Intent(this, RegistrarPlatoActivity::class.java).apply {
                putExtra("EXTRA_PLATO_ID", plato.id)
            }
            startActivity(intent)
        }
        binding.recyclerPlatos.adapter = adapter
    }
}
