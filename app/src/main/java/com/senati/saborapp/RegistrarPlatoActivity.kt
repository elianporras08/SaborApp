package com.senati.saborapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.senati.saborapp.databinding.ActivityRegistrarPlatoBinding

class RegistrarPlatoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarPlatoBinding
    private lateinit var platoDao: PlatoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrarPlatoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        platoDao = PlatoDao(this)

        // Configurar Spinner de categorías (Entradas, Fondos, Bebidas, Postres) - HU-05, CA3
        val categorias = arrayOf("Entradas", "Fondos", "Bebidas", "Postres")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categorias)
        binding.spCategoriaPlato.adapter = adapter

        binding.btnGuardarPlato.setOnClickListener {
            val nombre = binding.etNombrePlato.text.toString().trim()
            val categoria = binding.spCategoriaPlato.selectedItem?.toString() ?: "Fondos"
            val precioStr = binding.etPrecioPlato.text.toString().trim()

            var isValid = true

            // Validar si nombre está vacío (CA1)
            if (nombre.isEmpty()) {
                binding.etNombrePlato.error = getString(R.string.error_campo_vacio)
                isValid = false
            } else {
                binding.etNombrePlato.error = null
            }

            // Validar si precio está vacío (CA1)
            if (precioStr.isEmpty()) {
                binding.etPrecioPlato.error = getString(R.string.error_campo_vacio)
                isValid = false
            } else {
                binding.etPrecioPlato.error = null
            }

            if (!isValid) return@setOnClickListener

            // Validar precio mayor a 0 (CA2)
            val precio = precioStr.toDoubleOrNull()
            if (precio == null || precio <= 0) {
                binding.etPrecioPlato.error = "Precio inválido"
                Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nuevoPlato = Plato(0, nombre, categoria, precio, 1)
            val resultado = platoDao.insertar(nuevoPlato)

            if (resultado) {
                Toast.makeText(this, "Plato registrado exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al registrar el plato", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
