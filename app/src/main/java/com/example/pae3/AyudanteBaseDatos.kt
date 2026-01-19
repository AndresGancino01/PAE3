/**
 * Universidad Indoamérica
 * Desarrollo de Aplicaciones Móviles
 * Autores: Andrés Gancino - Diego García
 * PAE 3
 * Fecha de entrega: 19/01/2026
 */

package com.example.pae3

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Clase encargada de la gestión de la base de datos SQLite
class AyudanteBaseDatos(context: Context) : SQLiteOpenHelper(context, "FlotaVehicular.db", null, 2) {

    // Se ejecuta al crear la base de datos por primera vez
    override fun onCreate(db: SQLiteDatabase?) {
        // Creación de la tabla de usuarios (Almacena credenciales y roles)
        db?.execSQL("CREATE TABLE usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, correo TEXT UNIQUE, password TEXT, rol TEXT)")

        // Creación de la tabla de vehículos (Almacena el estado actual y datos de renta activa)
        db?.execSQL("CREATE TABLE vehiculos (id INTEGER PRIMARY KEY AUTOINCREMENT, marca TEXT, modelo TEXT, placa TEXT, precio REAL, disponible INTEGER, cliente TEXT, fechaIni TEXT, fechaFin TEXT, total REAL)")

        // Creación de la tabla de historial (Almacena el registro permanente de rentas finalizadas)
        db?.execSQL("CREATE TABLE historial (id INTEGER PRIMARY KEY AUTOINCREMENT, marca TEXT, modelo TEXT, cliente TEXT, total REAL, fecha TEXT)")

        // Inserción del usuario Administrador por defecto para el primer acceso
        db?.execSQL("INSERT OR IGNORE INTO usuarios (nombre, correo, password, rol) VALUES ('Administrador', 'admin@mail.com', 'admin123', 'ADMIN')")
    }

    // Se ejecuta si se detecta un cambio en la versión de la base de datos
    override fun onUpgrade(db: SQLiteDatabase?, old: Int, new: Int) {
        if (old < 2) {
            // Si actualizamos de v1 a v2, creamos la tabla historial sin borrar los datos anteriores
            db?.execSQL("CREATE TABLE historial (id INTEGER PRIMARY KEY AUTOINCREMENT, marca TEXT, modelo TEXT, cliente TEXT, total REAL, fecha TEXT)")
        }
    }

    // --- FUNCIONES DE GESTIÓN DE USUARIOS ---

    // Inserta un nuevo usuario en la base de datos (Registro)
    fun registrarUsuario(n: String, c: String, p: String, r: String) = this.writableDatabase.insert("usuarios", null, ContentValues().apply {
        put("nombre", n); put("correo", c); put("password", p); put("rol", r)
    })

    // Valida las credenciales en el Login y devuelve los datos del usuario si coincide
    fun validarLogin(c: String, p: String): Map<String, String>? {
        val cursor = this.readableDatabase.rawQuery("SELECT rol, nombre FROM usuarios WHERE correo=? AND password=?", arrayOf(c, p))
        return if (cursor.moveToFirst()) {
            val res = mapOf("rol" to cursor.getString(0), "nombre" to cursor.getString(1))
            cursor.close(); res
        } else { cursor.close(); null }
    }

    // --- FUNCIONES DE GESTIÓN DE VEHÍCULOS ---

    // Permite al Administrador registrar una nueva unidad en la flota
    fun insertarVehiculo(v: Vehiculo) = this.writableDatabase.insert("vehiculos", null, ContentValues().apply {
        put("marca", v.marca); put("modelo", v.modelo); put("placa", v.placa); put("precio", v.precio); put("disponible", 1)
    })

    // Recupera la lista completa de vehículos para mostrarlos en el RecyclerView
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

    // Actualiza el estado de un vehículo a 'Rentado' (0) y guarda los datos del cliente y fechas
    fun realizarReserva(id: Int, cliente: String, f1: String, f2: String, total: Double) {
        val v = ContentValues().apply {
            put("disponible", 0); put("cliente", cliente); put("fechaIni", f1); put("fechaFin", f2); put("total", total)
        }
        this.writableDatabase.update("vehiculos", v, "id=?", arrayOf(id.toString()))
    }

    // Procesa la devolución del vehículo: guarda el registro en historial y libera la unidad
    fun entregarVehiculo(veh: Vehiculo) {
        val db = this.writableDatabase

        // Se guarda el registro de la transacción antes de limpiar los datos del auto
        val h = ContentValues().apply {
            put("marca", veh.marca); put("modelo", veh.modelo)
            put("cliente", veh.cliente); put("total", veh.total)
            put("fecha", veh.fechaFin)
        }
        db.insert("historial", null, h)

        // Se resetea el vehículo para que vuelva a aparecer como disponible
        val v = ContentValues().apply {
            put("disponible", 1); put("cliente", ""); put("fechaIni", ""); put("fechaFin", ""); put("total", 0.0)
        }
        db.update("vehiculos", v, "id=?", arrayOf(veh.id.toString()))
    }

    // Obtiene el historial de todas las rentas finalizadas para el reporte del administrador
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

    // Elimina físicamente un vehículo de la base de datos (Opción de Administrador)
    fun eliminarVehiculo(id: Int) = this.writableDatabase.delete("vehiculos", "id=?", arrayOf(id.toString()))
}