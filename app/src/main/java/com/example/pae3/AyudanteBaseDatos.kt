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

    override fun onUpgrade(db: SQLiteDatabase?, old: Int, new: Int) {
        db?.execSQL("DROP TABLE IF EXISTS usuarios")
        db?.execSQL("DROP TABLE IF EXISTS vehiculos")
        onCreate(db)
    }

    fun registrarUsuario(nombre: String, correo: String, pass: String, rol: String): Long {
        val valores = ContentValues().apply {
            put("nombre", nombre); put("correo", correo); put("password", pass); put("rol", rol)
        }
        return this.writableDatabase.insert("usuarios", null, valores)
    }

    fun validarLogin(correo: String, pass: String): Map<String, String>? {
        val cursor = this.readableDatabase.rawQuery("SELECT rol, nombre FROM usuarios WHERE correo=? AND password=?", arrayOf(correo, pass))
        return if (cursor.moveToFirst()) {
            val res = mapOf("rol" to cursor.getString(0), "nombre" to cursor.getString(1))
            cursor.close(); res
        } else { cursor.close(); null }
    }

    // Corregido: Ahora devuelve Long para evitar error en RegistroVehiculoActivity
    fun insertarVehiculo(v: Vehiculo): Long {
        val cv = ContentValues().apply {
            put("marca", v.marca); put("modelo", v.modelo); put("placa", v.placa)
            put("precio", v.precio); put("disponible", 1)
        }
        return this.writableDatabase.insert("vehiculos", null, cv)
    }

    // Corregido: Agregado parámetro 'soloDisponibles' para CatalogoActivity y ReportesActivity
    fun obtenerVehiculos(soloDisponibles: Boolean): List<Vehiculo> {
        val lista = mutableListOf<Vehiculo>()
        val query = if (soloDisponibles) "SELECT * FROM vehiculos WHERE disponible = 1" else "SELECT * FROM vehiculos"
        val c = this.readableDatabase.rawQuery(query, null)
        if (c.moveToFirst()) {
            do {
                lista.add(Vehiculo(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getDouble(4), c.getInt(5)))
            } while (c.moveToNext())
        }
        c.close(); return lista
    }

    // Corregido: Implementación de realizarReserva
    fun realizarReserva(id: Int, cliente: String, f1: String, f2: String, total: Double) {
        val v = ContentValues().apply {
            put("disponible", 0); put("cliente", cliente); put("fechaIni", f1); put("fechaFin", f2); put("total", total)
        }
        this.writableDatabase.update("vehiculos", v, "id=?", arrayOf(id.toString()))
    }

    fun eliminarVehiculo(id: Int) = this.writableDatabase.delete("vehiculos", "id=?", arrayOf(id.toString()))
}