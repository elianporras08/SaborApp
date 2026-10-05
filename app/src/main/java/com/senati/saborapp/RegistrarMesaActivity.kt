package com.senati.saborapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.senati.saborapp.databinding.ActivityRegistrarMesaBinding

class RegistrarMesaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarMesaBinding
    private lateinit var mesaDao: MesaDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrarMesaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mesaDao = MesaDao(this)

        binding.btnGuardarMesa.setOnClickListener {
            val numeroStr = binding.etNumeroMesa.text.toString().trim()
            val capacidadStr = binding.etCapacidadMesa.text.toString().trim()

            var isValid = true

            if (numeroStr.isEmpty()) {
                binding.etNumeroMesa.error = getString(R.string.error_campo_vacio)
                isValid = false
            } else {
                binding.etNumeroMesa.error = null
            }

            if (capacidadStr.isEmpty()) {
                binding.etCapacidadMesa.error = getString(R.string.error_campo_vacio)
                isValid = false
            } else {
                binding.etCapacidadMesa.error = null
            }

            if (!isValid) return@setOnClickListener

            val numero = numeroStr.toIntOrNull()
            val capacidad = capacidadStr.toIntOrNull()

            if (numero == null || numero <= 0) {
                binding.etNumeroMesa.error = "Número inválido"
                return@setOnClickListener
            }

            // CA2: Dado que la capacidad no está entre 1 y 12, cuando guardo, entonces se muestra «Capacidad inválida».
            if (capacidad == null || capacidad !in 1..12) {
                binding.etCapacidadMesa.error = "Capacidad inválida"
                Toast.makeText(this, "Capacidad inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // CA3: Registro de mesa nueva en estado LIBRE
            val nuevaMesa = Mesa(0, numero, capacidad, "LIBRE")
            val res = mesaDao.insertar(nuevaMesa)

            if (res == "OK") {
                Toast.makeText(this, "Mesa registrada exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                // CA1: Dado que registro un número de mesa que ya existe, cuando pulso «Guardar», entonces aparece «La mesa ya existe».
                Toast.makeText(this, res, Toast.LENGTH_SHORT).show()
                if (res == "La mesa ya existe") {
                    binding.etNumeroMesa.error = "La mesa ya existe"
                }
            }
        }
    }
}
