package com.senati.saborapp

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.senati.saborapp.databinding.ActivityRegistrarPlatoBinding

class RegistrarPlatoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarPlatoBinding
    private lateinit var platoDao: PlatoDao
    private var platoId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrarPlatoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        platoDao = PlatoDao(this)

        // Configurar Spinner de categorías
        val categorias = arrayOf("Entradas", "Fondos", "Bebidas", "Postres")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categorias)
        binding.spCategoriaPlato.adapter = spinnerAdapter

        // Verificar si viene en modo Edición (HU-07, CA1)
        platoId = intent.getIntExtra("EXTRA_PLATO_ID", -1)
        if (platoId != -1) {
            val platoExistente = platoDao.obtener(platoId)
            if (platoExistente != null) {
                binding.tvTituloPlato.text = "Editar plato"
                binding.etNombrePlato.setText(platoExistente.nombre)
                binding.etPrecioPlato.setText(platoExistente.precio.toString())
                binding.swDisponible.isChecked = platoExistente.disponible == 1
                binding.btnGuardarPlato.text = "Actualizar"
                binding.btnEliminarPlato.visibility = View.VISIBLE

                val posIndex = categorias.indexOf(platoExistente.categoria)
                if (posIndex >= 0) {
                    binding.spCategoriaPlato.setSelection(posIndex)
                }
            }
        }

        binding.btnGuardarPlato.setOnClickListener {
            val nombre = binding.etNombrePlato.text.toString().trim()
            val categoria = binding.spCategoriaPlato.selectedItem?.toString() ?: "Fondos"
            val precioStr = binding.etPrecioPlato.text.toString().trim()
            val disponible = if (binding.swDisponible.isChecked) 1 else 0

            var isValid = true

            if (nombre.isEmpty()) {
                binding.etNombrePlato.error = getString(R.string.error_campo_vacio)
                isValid = false
            } else {
                binding.etNombrePlato.error = null
            }

            if (precioStr.isEmpty()) {
                binding.etPrecioPlato.error = getString(R.string.error_campo_vacio)
                isValid = false
            } else {
                binding.etPrecioPlato.error = null
            }

            if (!isValid) return@setOnClickListener

            val precio = precioStr.toDoubleOrNull()
            if (precio == null || precio <= 0) {
                binding.etPrecioPlato.error = "Precio inválido"
                Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (platoId != -1) {
                // Actualizar plato existente
                val platoActualizar = Plato(platoId, nombre, categoria, precio, disponible)
                val exito = platoDao.actualizar(platoActualizar)
                if (exito) {
                    Toast.makeText(this, "Plato actualizado exitosamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error al actualizar el plato", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Registrar nuevo plato
                val nuevoPlato = Plato(0, nombre, categoria, precio, disponible)
                val exito = platoDao.insertar(nuevoPlato)
                if (exito) {
                    Toast.makeText(this, "Plato registrado exitosamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error al registrar el plato", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Eliminar plato con diálogo de confirmación (HU-07, CA2)
        binding.btnEliminarPlato.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Eliminar plato")
                .setMessage("¿Está seguro de que desea eliminar este plato?")
                .setPositiveButton("Eliminar") { _, _ ->
                    val resultado = platoDao.eliminar(platoId)
                    if (resultado == "OK") {
                        Toast.makeText(this, "Plato eliminado exitosamente", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        // Muestra "No se puede eliminar: tiene pedidos"
                        Toast.makeText(this, resultado, Toast.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }
}
