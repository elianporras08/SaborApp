package com.senati.saborapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "saborapp.db"
        const val DB_VERSION = 2
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Tablas de Catálogo y Usuarios
        db.execSQL("CREATE TABLE usuario (id INTEGER PRIMARY KEY AUTOINCREMENT, usuario TEXT UNIQUE, clave TEXT, rol TEXT)")
        db.execSQL("CREATE TABLE plato (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, categoria TEXT, precio REAL CHECK(precio > 0), disponible INTEGER)")
        db.execSQL("CREATE TABLE mesa (id INTEGER PRIMARY KEY AUTOINCREMENT, numero INTEGER UNIQUE, capacidad INTEGER, estado TEXT)")

        // Tablas de Operación Principal (Sprint 3)
        db.execSQL("CREATE TABLE pedido (id INTEGER PRIMARY KEY AUTOINCREMENT, id_mesa INTEGER REFERENCES mesa(id), fecha TEXT, estado TEXT, total REAL)")
        db.execSQL("CREATE TABLE detalle_pedido (id INTEGER PRIMARY KEY AUTOINCREMENT, id_pedido INTEGER REFERENCES pedido(id) ON DELETE CASCADE, id_plato INTEGER REFERENCES plato(id), cantidad INTEGER CHECK(cantidad > 0), precio_unit REAL, subtotal REAL)")

        // Usuarios por defecto (HU-04)
        db.execSQL("INSERT INTO usuario (usuario, clave, rol) VALUES ('admin', '1234', 'ADMIN')")
        db.execSQL("INSERT INTO usuario (usuario, clave, rol) VALUES ('mozo', '1234', 'MOZO')")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE pedido (id INTEGER PRIMARY KEY AUTOINCREMENT, id_mesa INTEGER REFERENCES mesa(id), fecha TEXT, estado TEXT, total REAL)")
            db.execSQL("CREATE TABLE detalle_pedido (id INTEGER PRIMARY KEY AUTOINCREMENT, id_pedido INTEGER REFERENCES pedido(id) ON DELETE CASCADE, id_plato INTEGER REFERENCES plato(id), cantidad INTEGER CHECK(cantidad > 0), precio_unit REAL, subtotal REAL)")
        }
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }
}
