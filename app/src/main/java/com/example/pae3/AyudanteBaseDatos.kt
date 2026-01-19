package com.example.pae3

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AyudanteBaseDatos(context: Context) : SQLiteOpenHelper(context, "FlotaVehicular.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase?) {
        // Tabla de usuarios
        db?.execSQL("CREATE TABLE usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, correo TEXT UNIQUE, password TEXT, rol TEXT)")

        // Tabla de vehículos (Estado actual)
        db?.execSQL("CREATE TABLE vehiculos (id INTEGER PRIMARY KEY AUTOINCREMENT, marca TEXT, modelo TEXT, placa TEXT, precio REAL, disponible INTEGER, cliente TEXT, fechaIni TEXT, fechaFin TEXT, total REAL)")

        // TABVA DE HISTORIAL (Para reportes permanentes)
        db?.execSQL("CREATE TABLE historial (id INTEGER PRIMARY KEY AUTOINCREMENT, marca TEXT, modelo TEXT, cliente TEXT, total REAL, fecha TEXT)")

        // Admin por defecto
        db?.execSQL("INSERT OR IGNORE INTO usuarios (nombre, correo, password, rol) VALUES ('Administrador', 'admin@mail.com', 'admin123', 'ADMIN')")
    }

    override fun onUpgrade(db: SQLiteDatabase?, old: Int, new: Int) {
        if (old < 2) {
            db?.execSQL("CREATE TABLE historial (id INTEGER PRIMARY KEY AUTOINCREMENT, marca TEXT, modelo TEXT, cliente TEXT, total REAL, fecha TEXT)")
        }
    }

    // --- Funciones de Usuario ---
    fun registrarUsuario(n: String, c: String, p: String, r: String) = this.writableDatabase.insert("usuarios", null, ContentValues().apply { put("nombre", n); put("correo", c); put("password", p); put("rol", r) })

    fun validarLogin(c: String, p: String): Map<String, String>? {
        val cursor = this.readableDatabase.rawQuery("SELECT rol, nombre FROM usuarios WHERE correo=? AND password=?", arrayOf(c, p))
        return if (cursor.moveToFirst()) {
            val res = mapOf("rol" to cursor.getString(0), "nombre" to cursor.getString(1))
            cursor.close(); res
        } else { cursor.close(); null }
    }

    // --- Funciones de Vehículos ---
    fun insertarVehiculo(v: Vehiculo) = this.writableDatabase.insert("vehiculos", null, ContentValues().apply {
        put("marca", v.marca); put("modelo", v.modelo); put("placa", v.placa); put("precio", v.precio); put("disponible", 1)
    })

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

    // Al entregar, guardamos en el historial antes de limpiar el vehículo
    fun entregarVehiculo(veh: Vehiculo) {
        val db = this.writableDatabase

        // 1. Guardar en historial
        val h = ContentValues().apply {
            put("marca", veh.marca); put("modelo", veh.modelo)
            put("cliente", veh.cliente); put("total", veh.total)
            put("fecha", veh.fechaFin)
        }
        db.insert("historial", null, h)

        // 2. Limpiar el vehículo para que esté disponible de nuevo
        val v = ContentValues().apply {
            put("disponible", 1); put("cliente", ""); put("fechaIni", ""); put("fechaFin", ""); put("total", 0.0)
        }
        db.update("vehiculos", v, "id=?", arrayOf(veh.id.toString()))
    }

    fun obtenerHistorial(): List<String> {
        val lista = mutableListOf<String>()
        val c = this.readableDatabase.rawQuery("SELECT * FROM historial ORDER BY id DESC", null)
        if (c.moveToFirst()) {
            do {
                lista.add("Cliente: ${c.getString(3)}\nVehículo: ${c.getString(1)} ${c.getString(2)}\nTotal Pagado: $${c.getDouble(4)}\nFecha: ${c.getString(5)}")
            } while (c.moveToNext())
        }
        c.close(); return lista
    }

    fun eliminarVehiculo(id: Int) = this.writableDatabase.delete("vehiculos", "id=?", arrayOf(id.toString()))
}