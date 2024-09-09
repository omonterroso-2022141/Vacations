package com.example.vacations

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var dbHelper: VacationDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = VacationDbHelper(this)

        val usernameInput = findViewById<EditText>(R.id.username)
        val passwordInput = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                val employee = login(username, password)
                if (employee != null) {
                    // Almacenar los datos del empleado en un Intent y enviar a MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("employeeId", employee.id)
                    intent.putExtra("username", employee.username)
                    startActivity(intent)
                    finish() // Finaliza LoginActivity para que no se pueda regresar con "back"
                } else {
                    Toast.makeText(this, "Invalid login credentials", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para verificar el login
    private fun login(username: String, password: String): Employee? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM employee WHERE username = ? AND password = ?", arrayOf(username, password))

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val availableDays = cursor.getInt(cursor.getColumnIndexOrThrow("available_days"))
            cursor.close()
            return Employee(id, username, availableDays)
        }

        cursor.close()
        return null
    }
}

data class Employee(val id: Int, val username: String, val availableDays: Int)
