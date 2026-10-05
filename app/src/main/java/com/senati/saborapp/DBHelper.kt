package com.senati.saborapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "saborapp.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // Tablas del Sprint 2 (Catálogo)
        db.execSQL("create table usuario (id integer primary key autoincrement, usuario text unique, clave text, rol text)")
        db.execSQL("create table plato (id integer primary key autoincrement, nombre text not null, categoria text, precio real check(precio > 0), disponible integer)")
        db.execSQL("create table mesa (id integer primary key autoincrement, numero integer unique, capacidad integer, estado text)")

        // Insertar el usuario administrador por defecto (HU-04)
        db.execSQL("insert into usuario (usuario, clave, rol) values ('admin', '1234', 'ADMIN')")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Aquí agregaremos las tablas de pedido y detalle_pedido en el Sprint 3
        // cuando subamos la versión a 2. Por ahora lo dejamos vacío.
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        // Activar el soporte para claves foráneas
        db.setForeignKeyConstraintsEnabled(true)
    }
}