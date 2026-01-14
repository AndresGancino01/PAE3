package com.example.pae3

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

class AyudanteBaseDatos(context: Context) : SQLiteOpenHelper(context, "RentCar_V6_Verde.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("""CREATE TABLE vehiculos (id INTEGER PRIMARY KEY AUTOINCREMENT, marca TEXT, modelo TEXT, 
            placa TEXT, precio REAL, disponible INTEGER, cliente TEXT, cedula TEXT, fechaIni TEXT, fechaFin TEXT, total REAL)""")

        db?.execSQL("CREATE TABLE usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, apellido TEXT, correo TEXT UNIQUE, password TEXT, rol TEXT)")

        // Datos iniciales
        db?.execSQL("INSERT INTO usuarios (nombre, apellido, correo, password, rol) VALUES ('Admin', 'Sistema', 'admin@mail.com', 'admin123', 'ADMIN')")
        for (i in 1..20) {
            val v = ContentValues().apply {
                put("marca", listOf("Toyota", "Hyundai", "Suzuki").random()); put("modelo", "SUV-${i}")
                put("placa", "PBA-${1000+i}"); put("precio", (35..80).random().toDouble()); put("disponible", 1)
            }
            db?.insert("vehiculos", null, v)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, old: Int, new: Int) {
        db?.execSQL("DROP TABLE IF EXISTS vehiculos"); db?.execSQL("DROP TABLE IF EXISTS usuarios"); onCreate(db)
    }

    // Corregido: Acepta los 5 argumentos que envía tu LoginActivity
    fun registrarUsuario(nom: String, ape: String, mail: String, pass: String, rol: String): Long {
        val v = ContentValues().apply {
            put("nombre", nom); put("apellido", ape); put("correo", mail); put("password", pass); put("rol", rol)
        }
        return this.writableDatabase.insert("usuarios", null, v)
    }

    fun insertarVehiculo(v: Vehiculo): Long {
        val valo = ContentValues().apply {
            put("marca", v.marca); put("modelo", v.modelo); put("placa", v.placa); put("precio", v.precio); put("disponible", 1)
        }
        return this.writableDatabase.insert("vehiculos", null, valo)
    }

    fun realizarReserva(id: Int, cliente: String, f1: String, f2: String, total: Double) {
        val v = ContentValues().apply {
            put("disponible", 0); put("cliente", cliente); put("fechaIni", f1); put("fechaFin", f2); put("total", total)
        }
        this.writableDatabase.update("vehiculos", v, "id=?", arrayOf(id.toString()))
    }

    fun liberarVehiculo(id: Int) {
        val v = ContentValues().apply { put("disponible", 1); put("cliente", ""); put("total", 0.0) }
        this.writableDatabase.update("vehiculos", v, "id=?", arrayOf(id.toString()))
    }

    fun obtenerVehiculos(): List<Vehiculo> {
        val lista = mutableListOf<Vehiculo>()
        // Es mejor usar una variable para la DB
        val db = this.readableDatabase
        val c = db.rawQuery("SELECT * FROM vehiculos", null)

        if (c.moveToFirst()) {
            do {
                // Usamos el operador ?: "" para que si el valor es nulo, use un texto vacío
                // y evitamos que la app se cierre.
                val id = c.getInt(0)
                val marca = c.getString(1) ?: ""
                val modelo = c.getString(2) ?: ""
                val placa = c.getString(3) ?: ""
                val precio = c.getDouble(4)
                val anio = c.getInt(5)
                val color = c.getString(6) ?: ""
                val estado = "" // El índice 7 parece que lo saltas manualmente
                val imagenPath = c.getString(8) ?: ""
                val transmision = c.getString(9) ?: ""
                val motor = c.getDouble(10)

                lista.add(Vehiculo(id, marca, modelo, placa, precio, anio, color, estado, imagenPath, transmision, motor))
            } while (c.moveToNext())
        }
        c.close()
        return lista
    }

    fun validarLogin(u: String, p: String): Map<String, String>? {
        val c = this.readableDatabase.rawQuery("SELECT rol, nombre FROM usuarios WHERE correo=? AND password=?", arrayOf(u, p))
        var res: Map<String, String>? = null
        if (c.moveToFirst()) res = mapOf("rol" to c.getString(0), "nombre" to c.getString(1))
        c.close()
        return res
    }
}