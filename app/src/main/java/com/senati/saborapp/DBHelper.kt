package com.senati.saborapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "saborapp.db"
        const val DB_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Tablas del Sprint 2 (Catálogo y Usuarios)
        db.execSQL("CREATE TABLE usuario (id INTEGER PRIMARY KEY AUTOINCREMENT, usuario TEXT UNIQUE, clave TEXT, rol TEXT)")
        db.execSQL("CREATE TABLE plato (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, categoria TEXT, precio REAL CHECK(precio > 0), disponible INTEGER)")
        db.execSQL("CREATE TABLE mesa (id INTEGER PRIMARY KEY AUTOINCREMENT, numero INTEGER UNIQUE, capacidad INTEGER, estado TEXT)")

        // Insertar usuarios por defecto (HU-04)
        db.execSQL("INSERT INTO usuario (usuario, clave, rol) VALUES ('admin', '1234', 'ADMIN')")
        db.execSQL("INSERT INTO usuario (usuario, clave, rol) VALUES ('mozo', '1234', 'MOZO')")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // En el Sprint 3 se agregarán las tablas de pedido y detalle_pedido
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        // Activar el soporte para claves foráneas
        db.setForeignKeyConstraintsEnabled(true)
    }
}
