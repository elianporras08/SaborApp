package com.senati.saborapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.senati.saborapp.databinding.ActivityListaPlatosBinding

class ListaPlatosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaPlatosBinding
    private lateinit var platoDao: PlatoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListaPlatosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        platoDao = PlatoDao(this)

        // Configurar el RecyclerView en forma vertical
        binding.recyclerPlatos.layoutManager = LinearLayoutManager(this)

        // Botón flotante para ir a la pantalla de registro de platos
        binding.fabAgregarPlato.setOnClickListener {
            val intent = Intent(this, RegistrarPlatoActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Cargar o actualizar la lista cada vez que volvamos a la pantalla
        cargarPlatos()
    }

    private fun cargarPlatos() {
        val lista = platoDao.listar()
        val adapter = PlatoAdapter(lista)
        binding.recyclerPlatos.adapter = adapter
    }
}