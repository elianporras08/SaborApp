package com.senati.saborapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.senati.saborapp.databinding.ActivityListaMesasBinding

class ListaMesasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaMesasBinding
    private lateinit var mesaDao: MesaDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListaMesasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mesaDao = MesaDao(this)

        // Configurar RecyclerView en grilla de 3 columnas (HU-06)
        binding.recyclerMesas.layoutManager = GridLayoutManager(this, 3)

        binding.fabAgregarMesa.setOnClickListener {
            val intent = Intent(this, RegistrarMesaActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarMesas()
    }

    private fun cargarMesas() {
        val lista = mesaDao.listar()
        val adapter = MesaAdapter(lista)
        binding.recyclerMesas.adapter = adapter
    }
}
