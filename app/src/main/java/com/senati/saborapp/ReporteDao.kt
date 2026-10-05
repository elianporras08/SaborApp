package com.senati.saborapp

import android.content.Context
import android.database.Cursor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReporteDao(context: Context) {
    private val dbHelper = DBHelper(context)

    private fun getFechaHoy(): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    fun ventaDelDia(): Double {
        val db = dbHelper.readableDatabase
        val fechaHoy = getFechaHoy()
        val cursor: Cursor = db.rawQuery(
            "SELECT SUM(total) FROM pedido WHERE estado = 'CERRADO' AND substr(fecha, 1, 10) = ?",
            arrayOf(fechaHoy)
        )
        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        db.close()
        return total
    }

    fun totalPedidosDelDia(): Int {
        val db = dbHelper.readableDatabase
        val fechaHoy = getFechaHoy()
        val cursor: Cursor = db.rawQuery(
            "SELECT COUNT(*) FROM pedido WHERE estado = 'CERRADO' AND substr(fecha, 1, 10) = ?",
            arrayOf(fechaHoy)
        )
        var cantidad = 0
        if (cursor.moveToFirst()) {
            cantidad = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return cantidad
    }

    fun ticketPromedioDelDia(): Double {
        val ventas = ventaDelDia()
        val pedidos = totalPedidosDelDia()
        return if (pedidos > 0) ventas / pedidos else 0.0
    }

    fun topPlatosDelDia(): List<TopPlato> {
        val lista = mutableListOf<TopPlato>()
        val db = dbHelper.readableDatabase
        val fechaHoy = getFechaHoy()
        val query = """
            SELECT p.nombre, SUM(d.cantidad) AS total_cant 
            FROM detalle_pedido d 
            INNER JOIN pedido pe ON d.id_pedido = pe.id 
            INNER JOIN plato p ON d.id_plato = p.id 
            WHERE pe.estado = 'CERRADO' AND substr(pe.fecha, 1, 10) = ? 
            GROUP BY p.id, p.nombre 
            ORDER BY total_cant DESC 
            LIMIT 5
        """.trimIndent()

        val cursor: Cursor = db.rawQuery(query, arrayOf(fechaHoy))
        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("total_cant"))
                lista.add(TopPlato(nombre, cantidad))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }
}
