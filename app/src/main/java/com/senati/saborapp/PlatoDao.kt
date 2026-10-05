package com.senati.saborapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException

class PlatoDao(context: Context) {
    private val dbHelper = DBHelper(context)

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

    fun actualizar(plato: Plato): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", plato.nombre)
            put("categoria", plato.categoria)
            put("precio", plato.precio)
            put("disponible", plato.disponible)
        }
        val filas = db.update("plato", values, "id = ?", arrayOf(plato.id.toString()))
        db.close()
        return filas > 0
    }

    fun eliminar(id: Int): String {
        val db = dbHelper.writableDatabase
        // Verificar antes si el plato está referenciado en un detalle de pedido
        val cursor = db.rawQuery("SELECT COUNT(*) FROM detalle_pedido WHERE id_plato = ?", arrayOf(id.toString()))
        if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
            cursor.close()
            db.close()
            return "No se puede eliminar: tiene pedidos"
        }
        cursor.close()

        return try {
            val filas = db.delete("plato", "id = ?", arrayOf(id.toString()))
            db.close()
            if (filas > 0) "OK" else "Error al eliminar"
        } catch (e: SQLiteConstraintException) {
            db.close()
            "No se puede eliminar: tiene pedidos"
        } catch (e: Exception) {
            db.close()
            "No se puede eliminar: tiene pedidos"
        }
    }

    fun obtener(id: Int): Plato? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM plato WHERE id = ?", arrayOf(id.toString()))
        var plato: Plato? = null
        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"))
            val precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
            val disponible = cursor.getInt(cursor.getColumnIndexOrThrow("disponible"))
            plato = Plato(id, nombre, categoria, precio, disponible)
        }
        cursor.close()
        db.close()
        return plato
    }

    // Listar platos con filtro por nombre y categoría (HU-07, CA3)
    fun listar(filtroNombre: String = "", categoriaFiltro: String = "Todos"): List<Plato> {
        val lista = mutableListOf<Plato>()
        val db = dbHelper.readableDatabase

        var query = "SELECT * FROM plato WHERE 1=1"
        val params = mutableListOf<String>()

        if (filtroNombre.isNotBlank()) {
            query += " AND nombre LIKE ?"
            params.add("%$filtroNombre%")
        }

        if (categoriaFiltro != "Todos" && categoriaFiltro.isNotBlank()) {
            query += " AND categoria = ?"
            params.add(categoriaFiltro)
        }

        query += " ORDER BY categoria, nombre"

        val cursor: Cursor = db.rawQuery(query, params.toTypedArray())

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

    // Listar únicamente los platos disponibles (HU-07, CA4)
    fun listarDisponibles(): List<Plato> {
        val lista = mutableListOf<Plato>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM plato WHERE disponible = 1 ORDER BY categoria, nombre", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"))
                val precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))

                lista.add(Plato(id, nombre, categoria, precio, 1))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }
}
