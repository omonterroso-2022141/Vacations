package com.example.vacations

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vacations.VacationDbHelper.Employee

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
                    // Dependiendo del rol, redirigir a la actividad correcta
                    if (employee.role == "boss") {
                        val intent = Intent(this, BossMainActivity::class.java)  // Actividad de jefe
                        intent.putExtra("employeeId", employee.id)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, MainActivity::class.java)  // Actividad de empleado
                        intent.putExtra("employeeId", employee.id)
                        startActivity(intent)
                    }
                    finish()
                } else {
                    Toast.makeText(this, "Invalid login credentials", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Método para verificar el login
    private fun login(username: String, password: String): Employee? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM employee WHERE username = ? AND password = ?", arrayOf(username, password))

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val role = cursor.getString(cursor.getColumnIndexOrThrow("role"))
            cursor.close()
            return Employee(id, username, role)
        }

        cursor.close()
        return null
    }
}
