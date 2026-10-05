package com.senati.saborapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.senati.saborapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var usuarioDao: UsuarioDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuarioDao = UsuarioDao(this)

        binding.btnIngresar.setOnClickListener {
            val usuarioInput = binding.etUsuario.text.toString().trim()
            val claveInput = binding.etClave.text.toString().trim()

            var isValid = true

            // Validar si el campo usuario está vacío (CA1)
            if (usuarioInput.isEmpty()) {
                binding.etUsuario.error = getString(R.string.error_campo_vacio)
                isValid = false
            } else {
                binding.etUsuario.error = null
            }

            // Validar si el campo contraseña está vacío (CA1)
            if (claveInput.isEmpty()) {
                binding.etClave.error = getString(R.string.error_campo_vacio)
                isValid = false
            } else {
                binding.etClave.error = null
            }

            if (isValid) {
                // Consulta parametrizada a la BD SQLite (HU-04, CA2)
                val userLogged = usuarioDao.validarUsuario(usuarioInput, claveInput)
                if (userLogged != null) {
                    Toast.makeText(this, "Bienvenido ${userLogged.usuario}", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity2::class.java).apply {
                        putExtra("EXTRA_USUARIO", userLogged.usuario)
                        putExtra("EXTRA_ROL", userLogged.rol)
                    }
                    startActivity(intent)
                    finish() // Cierra el login para que atrás no regrese
                } else {
                    // Credenciales incorrectas (CA3)
                    Toast.makeText(this, getString(R.string.error_credenciales), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
