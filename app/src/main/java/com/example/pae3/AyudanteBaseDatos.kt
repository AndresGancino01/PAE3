package com.example.pae3

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

class AyudanteBaseDatos(contexto: Context) : SQLiteOpenHelper(contexto, "AlquilerPAE3.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE vehiculos (id INTEGER PRIMARY KEY AUTOINCREMENT, marca TEXT, modelo TEXT, placa TEXT, precio REAL, disponible INTEGER)")
    }
    override fun onUpgrade(db: SQLiteDatabase?, old: Int, new: Int) {
        db?.execSQL("DROP TABLE IF EXISTS vehiculos")
        onCreate(db)
    }
    fun insertarVehiculo(v: Vehiculo): Long {
        val db = this.writableDatabase
        return db.insert("vehiculos", null, ContentValues().apply {
            put("marca", v.marca); put("modelo", v.modelo)
            put("placa", v.placa); put("precio", v.precio); put("disponible", 1)
        })
    }
    fun obtenerVehiculos(): List<Vehiculo> {
        val lista = mutableListOf<Vehiculo>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM vehiculos", null)
        if (cursor.moveToFirst()) {
            do {
                lista.add(Vehiculo(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(4), cursor.getInt(5)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }
    fun eliminarVehiculo(id: Int) {
        this.writableDatabase.delete("vehiculos", "id=?", arrayOf(id.toString()))
    }
    fun cambiarDisponibilidad(id: Int, estado: Int) {
        val v = ContentValues().apply { put("disponible", estado) }
        this.writableDatabase.update("vehiculos", v, "id=?", arrayOf(id.toString()))
    }
}