package com.senati.saborapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.senati.saborapp.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val usuario = intent.getStringExtra("EXTRA_USUARIO") ?: "admin"
        val rol = intent.getStringExtra("EXTRA_ROL") ?: "ADMIN"

        binding.tvBienvenida.text = "Hola, $usuario"
        val rolTexto = if (rol.equals("ADMIN", ignoreCase = true)) "Administrador" else "Mozo"
        binding.tvRol.text = "Rol: $rolTexto"

        // Ocultar Reportes si el usuario tiene rol MOZO (HU-02, CA4)
        if (rol.equals("MOZO", ignoreCase = true)) {
            binding.btnReportes.visibility = View.GONE
        } else {
            binding.btnReportes.visibility = View.VISIBLE
        }

        // Abrir el catálogo de platos (HU-05, HU-07)
        binding.btnPlatos.setOnClickListener {
            val intent = Intent(this, ListaPlatosActivity::class.java)
            startActivity(intent)
        }

        // Abrir el listado de mesas (HU-06)
        binding.btnMesas.setOnClickListener {
            val intent = Intent(this, ListaMesasActivity::class.java)
            startActivity(intent)
        }

        // Abrir el módulo de pedidos (HU-08, HU-09)
        binding.btnPedidos.setOnClickListener {
            val intent = Intent(this, PedidoActivity::class.java)
            startActivity(intent)
        }

        binding.btnReportes.setOnClickListener {
            Toast.makeText(this, "Módulo de Reportes (Sprint 4)", Toast.LENGTH_SHORT).show()
        }

        // Botón Salir
        binding.btnSalir.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
