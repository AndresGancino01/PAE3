package com.example.pae3

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AyudanteBaseDatos(context: Context) : SQLiteOpenHelper(context, "FlotaVehicular.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, correo TEXT UNIQUE, password TEXT, rol TEXT)")
        db?.execSQL("CREATE TABLE vehiculos (id INTEGER PRIMARY KEY AUTOINCREMENT, marca TEXT, modelo TEXT, placa TEXT, precio REAL, disponible INTEGER, cliente TEXT, fechaIni TEXT, fechaFin TEXT, total REAL)")
        db?.execSQL("INSERT OR IGNORE INTO usuarios (nombre, correo, password, rol) VALUES ('Administrador', 'admin@mail.com', 'admin123', 'ADMIN')")
    }

    override fun onUpgrade(db: SQLiteDatabase?, old: Int, new: Int) = onCreate(db)

    fun registrarUsuario(n: String, c: String, p: String, r: String) = this.writableDatabase.insert("usuarios", null, ContentValues().apply { put("nombre", n); put("correo", c); put("password", p); put("rol", r) })

    fun validarLogin(c: String, p: String): Map<String, String>? {
        val cursor = this.readableDatabase.rawQuery("SELECT rol, nombre FROM usuarios WHERE correo=? AND password=?", arrayOf(c, p))
        return if (cursor.moveToFirst()) mapOf("rol" to cursor.getString(0), "nombre" to cursor.getString(1)) else null
    }

    fun insertarVehiculo(v: Vehiculo) = this.writableDatabase.insert("vehiculos", null, ContentValues().apply {
        put("marca", v.marca); put("modelo", v.modelo); put("placa", v.placa); put("precio", v.precio); put("disponible", 1)
    })

    // Función mejorada para traer todos los datos de renta
    fun obtenerTodosLosVehiculos(): List<Vehiculo> {
        val lista = mutableListOf<Vehiculo>()
        val c = this.readableDatabase.rawQuery("SELECT * FROM vehiculos", null)
        if (c.moveToFirst()) {
            do {
                lista.add(Vehiculo(c.getInt(0), c.getString(1), c.getString(2), c.getString(3),
                    c.getDouble(4), c.getInt(5), c.getString(6) ?: "",
                    c.getString(7) ?: "", c.getString(8) ?: "", c.getDouble(9)))
            } while (c.moveToNext())
        }
        c.close(); return lista
    }

    fun realizarReserva(id: Int, cliente: String, f1: String, f2: String, total: Double) {
        val v = ContentValues().apply {
            put("disponible", 0); put("cliente", cliente); put("fechaIni", f1); put("fechaFin", f2); put("total", total)
        }
        this.writableDatabase.update("vehiculos", v, "id=?", arrayOf(id.toString()))
    }

    // Nueva función para liberar el vehículo
    fun entregarVehiculo(id: Int) {
        val v = ContentValues().apply {
            put("disponible", 1); put("cliente", ""); put("fechaIni", ""); put("fechaFin", ""); put("total", 0.0)
        }
        this.writableDatabase.update("vehiculos", v, "id=?", arrayOf(id.toString()))
    }

    fun eliminarVehiculo(id: Int) = this.writableDatabase.delete("vehiculos", "id=?", arrayOf(id.toString()))
}