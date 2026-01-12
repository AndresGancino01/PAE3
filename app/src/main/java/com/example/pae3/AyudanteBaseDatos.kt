package com.example.pae3

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

class AyudanteBaseDatos(contexto: Context) : SQLiteOpenHelper(contexto, "AlquilerPAE3.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        // Creamos la tabla con los campos necesarios
        val crearTabla = "CREATE TABLE vehiculos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "marca TEXT," +
                "modelo TEXT," +
                "placa TEXT," +
                "precio REAL)"
        db?.execSQL(crearTabla)
    }

    override fun onUpgrade(db: SQLiteDatabase?, old: Int, new: Int) {
        db?.execSQL("DROP TABLE IF EXISTS vehiculos")
        onCreate(db)
    }

    // Función para insertar un vehículo en la base de datos
    fun insertarVehiculo(vehiculo: Vehiculo): Long {
        val db = this.writableDatabase
        val valores = ContentValues().apply {
            put("marca", vehiculo.marca)
            put("modelo", vehiculo.modelo)
            put("placa", vehiculo.placa)
            put("precio", vehiculo.precio)
        }
        return db.insert("vehiculos", null, valores)
    }
}