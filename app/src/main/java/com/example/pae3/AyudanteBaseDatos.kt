package com.example.pae3

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

class AyudanteBaseDatos(contexto: Context) : SQLiteOpenHelper(contexto, "AlquilerPAE3.db", null, 6) {

    override fun onCreate(db: SQLiteDatabase?) {
        // Tablas principales
        db?.execSQL("CREATE TABLE vehiculos (id INTEGER PRIMARY KEY AUTOINCREMENT, marca TEXT, modelo TEXT, placa TEXT, precio REAL, disponible INTEGER, nombreCliente TEXT, cedulaCliente TEXT)")
        db?.execSQL("CREATE TABLE historial (id INTEGER PRIMARY KEY AUTOINCREMENT, detalle TEXT, fecha TEXT)")

        // Tabla de usuarios con ROL
        db?.execSQL("CREATE TABLE usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, usuario TEXT, password TEXT, rol TEXT)")

        // Cuentas predeterminadas para pruebas
        insertarUsuarioInicial(db, "admin", "admin123", "ADMIN")
        insertarUsuarioInicial(db, "empleado", "123", "OPERADOR")
    }

    private fun insertarUsuarioInicial(db: SQLiteDatabase?, user: String, pass: String, rol: String) {
        val v = ContentValues().apply {
            put("usuario", user)
            put("password", pass)
            put("rol", rol)
        }
        db?.insert("usuarios", null, v)
    }

    override fun onUpgrade(db: SQLiteDatabase?, old: Int, new: Int) {
        db?.execSQL("DROP TABLE IF EXISTS vehiculos")
        db?.execSQL("DROP TABLE IF EXISTS historial")
        db?.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }

    // --- SEGURIDAD ---
    fun validarUsuario(user: String, pass: String): String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT rol FROM usuarios WHERE usuario=? AND password=?", arrayOf(user, pass))
        var rol: String? = null
        if (cursor.moveToFirst()) rol = cursor.getString(0)
        cursor.close()
        return rol
    }

    fun registrarUsuario(user: String, pass: String, rol: String): Long {
        val db = this.writableDatabase
        val v = ContentValues().apply {
            put("usuario", user); put("password", pass); put("rol", rol)
        }
        return db.insert("usuarios", null, v)
    }

    // --- GESTIÓN DE VEHÍCULOS ---
    fun insertarVehiculo(v: Vehiculo): Long {
        val db = this.writableDatabase
        return db.insert("vehiculos", null, ContentValues().apply {
            put("marca", v.marca); put("modelo", v.modelo); put("placa", v.placa); put("precio", v.precio); put("disponible", 1)
        })
    }

    fun obtenerVehiculos(): List<Vehiculo> {
        val lista = mutableListOf<Vehiculo>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM vehiculos", null)
        if (cursor.moveToFirst()) {
            do {
                lista.add(Vehiculo(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(4), cursor.getInt(5), cursor.getString(6), cursor.getString(7)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun alquilarVehiculo(id: Int, nombre: String, cedula: String) {
        val v = ContentValues().apply { put("disponible", 0); put("nombreCliente", nombre); put("cedulaCliente", cedula) }
        this.writableDatabase.update("vehiculos", v, "id=?", arrayOf(id.toString()))
    }

    fun devolverVehiculo(id: Int, detalle: String) {
        val h = ContentValues().apply { put("detalle", detalle); put("fecha", java.text.DateFormat.getDateTimeInstance().format(java.util.Date())) }
        this.writableDatabase.insert("historial", null, h)
        val v = ContentValues().apply { put("disponible", 1); put("nombreCliente", ""); put("cedulaCliente", "") }
        this.writableDatabase.update("vehiculos", v, "id=?", arrayOf(id.toString()))
    }

    fun obtenerHistorial(): List<String> {
        val lista = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM historial ORDER BY id DESC", null)
        if (cursor.moveToFirst()) { do { lista.add("📅 ${cursor.getString(2)}\n📝 ${cursor.getString(1)}") } while (cursor.moveToNext()) }
        cursor.close()
        return lista
    }

    fun eliminarVehiculo(id: Int) { this.writableDatabase.delete("vehiculos", "id=?", arrayOf(id.toString())) }
}