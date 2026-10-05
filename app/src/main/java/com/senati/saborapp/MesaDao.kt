package com.senati.saborapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException

class MesaDao(context: Context) {
    private val dbHelper = DBHelper(context)

    // Registrar una mesa validando si el número ya existe (HU-06, CA1)
    fun insertar(mesa: Mesa): String {
        val db = dbHelper.writableDatabase
        return try {
            val values = ContentValues().apply {
                put("numero", mesa.numero)
                put("capacidad", mesa.capacidad)
                put("estado", "LIBRE") // Toda mesa nueva nace en estado LIBRE (CA3)
            }
            val resultado = db.insert("mesa", null, values)
            db.close()
            if (resultado != -1L) "OK" else "Error al registrar la mesa"
        } catch (e: SQLiteConstraintException) {
            db.close()
            "La mesa ya existe" // Criterio de aceptación HU-06
        }
    }

    // Listar las mesas ordenadas por su número
    fun listar(): List<Mesa> {
        val lista = mutableListOf<Mesa>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("select * from mesa order by numero", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val numero = cursor.getInt(cursor.getColumnIndexOrThrow("numero"))
                val capacidad = cursor.getInt(cursor.getColumnIndexOrThrow("capacidad"))
                val estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"))

                lista.add(Mesa(id, numero, capacidad, estado))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }
}