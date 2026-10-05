package com.senati.saborapp

import android.content.Context
import android.database.Cursor

class UsuarioDao(context: Context) {
    private val dbHelper = DBHelper(context)

    fun validarUsuario(usuario: String, clave: String): Usuario? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM usuario WHERE usuario = ? AND clave = ?",
            arrayOf(usuario, clave)
        )
        var user: Usuario? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val userStr = cursor.getString(cursor.getColumnIndexOrThrow("usuario"))
            val passStr = cursor.getString(cursor.getColumnIndexOrThrow("clave"))
            val rolStr = cursor.getString(cursor.getColumnIndexOrThrow("rol"))
            user = Usuario(id, userStr, passStr, rolStr)
        }
        cursor.close()
        db.close()
        return user
    }
}
