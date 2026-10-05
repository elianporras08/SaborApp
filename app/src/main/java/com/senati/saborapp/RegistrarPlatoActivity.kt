package com.senati.saborapp

import android.os.Bundle
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

        binding.btnGuardarPlato.setOnClickListener {
            val nombre = binding.etNombrePlato.text.toString().trim()
            val categoria = binding.etCategoriaPlato.text.toString().trim()
            val precioStr = binding.etPrecioPlato.text.toString().trim()

            // Validar campos vacíos
            if (nombre.isEmpty() || categoria.isEmpty() || precioStr.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar que el precio sea mayor a 0 (Criterio de validación de la BD)
            val precio = precioStr.toDoubleOrNull()
            if (precio == null || precio <= 0) {
                binding.etPrecioPlato.error = "Ingrese un precio válido mayor a 0"
                return@setOnClickListener
            }

            // Crear objeto Plato (id se autoincrementa en SQLite)
            val nuevoPlato = Plato(0, nombre, categoria, precio, 1)
            val resultado = platoDao.insertar(nuevoPlato)

            if (resultado) {
                Toast.makeText(this, "Plato registrado exitosamente", Toast.LENGTH_SHORT).show()
                finish() // Cierra la pantalla y regresa al listado
            } else {
                Toast.makeText(this, "Error al registrar el plato", Toast.LENGTH_SHORT).show()
            }
        }
    }
}