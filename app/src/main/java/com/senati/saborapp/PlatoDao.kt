package com.senati.saborapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class PlatoDao(context: Context) {
    private val dbHelper = DBHelper(context)

    // Metodo para registrar un plato en la base de datos (HU-05)
    fun insertar(plato: Plato): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", plato.nombre)
            put("categoria", plato.categoria)
            put("precio", plato.precio)
            put("disponible", plato.disponible)
        }
        val resultado = db.insert("plato", null, values)
        db.close()
        return resultado != -1L
    }

    // Metodo para listar los platos ordenados por categoría y nombre (HU-05, CA3)
    fun listar(): List<Plato> {
        val lista = mutableListOf<Plato>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("select * from plato order by categoria, nombre", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"))
                val precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
                val disponible = cursor.getInt(cursor.getColumnIndexOrThrow("disponible"))

                lista.add(Plato(id, nombre, categoria, precio, disponible))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }
}