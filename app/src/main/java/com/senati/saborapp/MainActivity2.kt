package com.senati.saborapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.senati.saborapp.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar ViewBinding para el menú principal
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Programar el botón Salir para regresar al Login (HU-02, CA3)
        binding.btnSalir.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Cierra el menú para que no regrese con atrás
        }
    }
}