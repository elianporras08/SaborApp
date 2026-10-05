package com.senati.saborapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PedidoDao(context: Context) {
    private val dbHelper = DBHelper(context)

    // Obtener pedido ABIERTO para una mesa determinada
    fun obtenerPedidoAbiertoPorMesa(idMesa: Int): Pedido? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM pedido WHERE id_mesa = ? AND estado = 'ABIERTO'",
            arrayOf(idMesa.toString())
        )
        var pedido: Pedido? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"))
            val estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"))
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
            pedido = Pedido(id, idMesa, fecha, estado, total)
        }
        cursor.close()
        db.close()
        return pedido
    }

    // Agregar plato al pedido usando una transacción de SQLite (HU-08, CA2, CA3, CA4)
    fun agregarPlatoAPedido(idMesa: Int, idPlato: Int, cantidad: Int): Boolean {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            // 1. Obtener precio actual del plato
            val cursorPlato = db.rawQuery("SELECT precio FROM plato WHERE id = ?", arrayOf(idPlato.toString()))
            if (!cursorPlato.moveToFirst()) {
                cursorPlato.close()
                return false
            }
            val precioUnit = cursorPlato.getDouble(0)
            cursorPlato.close()

            // 2. Obtener o crear el pedido ABIERTO para la mesa
            var idPedido: Long = -1
            val cursorPedido = db.rawQuery(
                "SELECT id FROM pedido WHERE id_mesa = ? AND estado = 'ABIERTO'",
                arrayOf(idMesa.toString())
            )
            if (cursorPedido.moveToFirst()) {
                idPedido = cursorPedido.getLong(0)
            } else {
                val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                val valPedido = ContentValues().apply {
                    put("id_mesa", idMesa)
                    put("fecha", fechaActual)
                    put("estado", "ABIERTO")
                    put("total", 0.0)
                }
                idPedido = db.insert("pedido", null, valPedido)

                // Cambiar estado de la mesa a OCUPADA
                val valMesa = ContentValues().apply { put("estado", "OCUPADA") }
                db.update("mesa", valMesa, "id = ?", arrayOf(idMesa.toString()))
            }
            cursorPedido.close()

            if (idPedido == -1L) return false

            // 3. Insertar o actualizar item en detalle_pedido
            val subtotal = precioUnit * cantidad
            val cursorDetalle = db.rawQuery(
                "SELECT id, cantidad FROM detalle_pedido WHERE id_pedido = ? AND id_plato = ?",
                arrayOf(idPedido.toString(), idPlato.toString())
            )
            if (cursorDetalle.moveToFirst()) {
                val idDetalle = cursorDetalle.getLong(0)
                val cantExistente = cursorDetalle.getInt(1)
                val nuevaCant = cantExistente + cantidad
                val nuevoSubtotal = precioUnit * nuevaCant
                val valDetalle = ContentValues().apply {
                    put("cantidad", nuevaCant)
                    put("subtotal", nuevoSubtotal)
                }
                db.update("detalle_pedido", valDetalle, "id = ?", arrayOf(idDetalle.toString()))
            } else {
                val valDetalle = ContentValues().apply {
                    put("id_pedido", idPedido)
                    put("id_plato", idPlato)
                    put("cantidad", cantidad)
                    put("precio_unit", precioUnit)
                    put("subtotal", subtotal)
                }
                db.insert("detalle_pedido", null, valDetalle)
            }
            cursorDetalle.close()

            // 4. Recalcular total del pedido
            val cursorTotal = db.rawQuery(
                "SELECT SUM(subtotal) FROM detalle_pedido WHERE id_pedido = ?",
                arrayOf(idPedido.toString())
            )
            var nuevoTotal = 0.0
            if (cursorTotal.moveToFirst()) {
                nuevoTotal = cursorTotal.getDouble(0)
            }
            cursorTotal.close()

            val valTotal = ContentValues().apply { put("total", nuevoTotal) }
            db.update("pedido", valTotal, "id = ?", arrayOf(idPedido.toString()))

            db.setTransactionSuccessful()
            return true
        } catch (e: Exception) {
            return false
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    // Listar detalles del pedido con JOIN a la tabla plato
    fun listarDetallesPedido(idPedido: Int): List<DetallePedido> {
        val lista = mutableListOf<DetallePedido>()
        val db = dbHelper.readableDatabase
        val query = """
            SELECT d.id, d.id_pedido, d.id_plato, p.nombre, d.cantidad, d.precio_unit, d.subtotal 
            FROM detalle_pedido d 
            INNER JOIN plato p ON d.id_plato = p.id 
            WHERE d.id_pedido = ?
        """.trimIndent()

        val cursor: Cursor = db.rawQuery(query, arrayOf(idPedido.toString()))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val idPlato = cursor.getInt(cursor.getColumnIndexOrThrow("id_plato"))
                val nombrePlato = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"))
                val precioUnit = cursor.getDouble(cursor.getColumnIndexOrThrow("precio_unit"))
                val subtotal = cursor.getDouble(cursor.getColumnIndexOrThrow("subtotal"))

                lista.add(DetallePedido(id, idPedido, idPlato, nombrePlato, cantidad, precioUnit, subtotal))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    // Cerrar la cuenta de la mesa en una sola transacción (HU-09, CA2)
    fun cerrarCuenta(idPedido: Int, idMesa: Int): Boolean {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val valPedido = ContentValues().apply { put("estado", "CERRADO") }
            db.update("pedido", valPedido, "id = ?", arrayOf(idPedido.toString()))

            val valMesa = ContentValues().apply { put("estado", "LIBRE") }
            db.update("mesa", valMesa, "id = ?", arrayOf(idMesa.toString()))

            db.setTransactionSuccessful()
            return true
        } catch (e: Exception) {
            return false
        } finally {
            db.endTransaction()
            db.close()
        }
    }
}
