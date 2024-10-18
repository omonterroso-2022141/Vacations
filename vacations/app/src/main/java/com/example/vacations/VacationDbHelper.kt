package com.example.vacations

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class VacationDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "vacations.db"
        const val DATABASE_VERSION = 2
    }

    override fun onCreate(db: SQLiteDatabase) {

        val CREATE_EMPLOYEE_TABLE = """
            CREATE TABLE employee (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                role TEXT NOT NULL,
                available_days INTEGER DEFAULT 20
            )
        """.trimIndent()
        db.execSQL(CREATE_EMPLOYEE_TABLE)


        val CREATE_VACATION_TABLE = """
            CREATE TABLE vacation (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                employee_id INTEGER,
                start_date TEXT,
                end_date TEXT,
                available_days INTEGER,
                comment TEXT, 
                status TEXT,
                FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE
            )
        """.trimIndent()
        db.execSQL(CREATE_VACATION_TABLE)
        insertDefaultRequest(db)
        insertDefaultEmployee(db)
        insertDefaultBoss(db)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS employee (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL,
                    available_days INTEGER DEFAULT 20
                )
            """)
        }
    }


    private fun insertDefaultEmployee(db: SQLiteDatabase) {
        val values = ContentValues().apply {
            put("username", "defaultuser")    // Usuario por defecto
            put("password", "password123")    // Contraseña por defecto
            put("role", "employee")           // Rol del usuario
            put("available_days", 20)         // Días de vacaciones por defecto
        }
        val newRowId = db.insert("employee", null, values)
        if (newRowId != -1L) {
            println("Usuario por defecto creado con ID: $newRowId")
        } else {
            println("Error al crear el usuario por defecto")
        }
    }

    private fun insertDefaultBoss(db: SQLiteDatabase) {
        val values = ContentValues().apply {
            put("username", "defaultboss")    // Usuario por defecto
            put("password", "password123")    // Contraseña por defecto
            put("role", "boss")           // Rol del usuario
            put("available_days", 20)         // Días de vacaciones por defecto
        }
        val newRowId = db.insert("employee", null, values)
        if (newRowId != -1L) {
            println("Usuario por defecto creado con ID: $newRowId")
        } else {
            println("Error al crear el usuario por defecto")
        }
    }



    private fun insertDefaultRequest(db: SQLiteDatabase) {
        val values = ContentValues().apply {
            put("employee_id", "1")    // Usuario por defecto
            put("start_date", "2024-05-1")    // Contraseña por defecto
            put("end_date", "2024-05-1")           // Rol del usuario
            put("available_days", 20)
            put("comment", "playita")
            put("status", "pending")// Días de vacaciones por defecto
        }
        val newRowId = db.insert("vacation", null, values)
        if (newRowId != -1L) {
            println("Usuario por defecto creado con ID: $newRowId")
        } else {
            println("Error al crear el usuario por defecto")
        }
    }

    fun getVacationRequests(employeeId: Int): List<VacationRequest> {
        val requests = mutableListOf<VacationRequest>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM vacation WHERE employee_id = ?", arrayOf(employeeId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val startDate = cursor.getString(cursor.getColumnIndexOrThrow("start_date"))
                val endDate = cursor.getString(cursor.getColumnIndexOrThrow("end_date"))
                val availableDays = cursor.getInt(cursor.getColumnIndexOrThrow("available_days"))
                val comment = cursor.getString(cursor.getColumnIndexOrThrow("comment"))
                val status = cursor.getString(cursor.getColumnIndexOrThrow("status"))

                requests.add(VacationRequest(id, employeeId, startDate, endDate, availableDays, comment, status))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return requests
    }


    data class VacationRequest(
        val id: Int,
        val employeeId: Int,
        val startDate: String,
        val endDate: String,
        val availableDays: Int,
        val comment: String,
        val status: String
    )

    data class EmployeeInfo(
        val name: String,
        val availableDays: Int
    )
    data class Employee(
        val id: Int,
        val username: String,
        val role: String
    )

}
