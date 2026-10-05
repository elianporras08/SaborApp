package com.senati.saborapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.senati.saborapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar ViewBinding para conectar el XML con el código
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnIngresar.setOnClickListener {
            val usuario = binding.etUsuario.text.toString().trim()
            val clave = binding.etClave.text.toString().trim()

            var isValid = true

            // Validar si el campo usuario está vacío (CA1)
            if (usuario.isEmpty()) {
                binding.etUsuario.error = getString(R.string.error_campo_vacio)
                isValid = false
            } else {
                binding.etUsuario.error = null
            }

            // Validar si el campo contraseña está vacío (CA1)
            if (clave.isEmpty()) {
                binding.etClave.error = getString(R.string.error_campo_vacio)
                isValid = false
            } else {
                binding.etClave.error = null
            }

            // Si los campos tienen datos, validamos las credenciales
            if (isValid) {
                if (usuario == "admin" && clave == "1234") {
                    // Credenciales correctas (CA2): pasamos temporalmente a MainActivity2
                    Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity2::class.java)
                    startActivity(intent)
                    finish() // Cierra el login para que al presionar atrás no regrese (CA2)
                } else {
                    // Credenciales incorrectas (CA3)
                    Toast.makeText(this, getString(R.string.error_credenciales), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}