package com.example.vacations

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private var employeeId: Int = -1  // ID del empleado logueado
    private lateinit var username: String  // Nombre del empleado logueado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtener los datos del Intent (pasados desde LoginActivity)
        employeeId = intent.getIntExtra("employeeId", -1)
        username = intent.getStringExtra("username") ?: "Unknown"

        // Cargar el HistorialFragment como el fragmento inicial
        loadFragment(RequestFragment().apply {
            arguments = Bundle().apply {
                putInt("employeeId", employeeId)  // Pasar el ID del empleado al fragmento
            }
        })

        // Configurar la navegación inferior
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // Manejar la selección de los elementos del BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.navigation_profile -> {
                    // Pasar employeeId a HistorialFragment
                    selectedFragment = HistoryFragment().apply {
                        arguments = Bundle().apply {
                            putInt("employeeId", employeeId)  // Pasar el ID del empleado al fragmento
                        }
                    }
                }
                R.id.navigation_request -> {
                    // Pasar employeeId a RequestFragment
                    selectedFragment = RequestFragment().apply {
                        arguments = Bundle().apply {
                            putInt("employeeId", employeeId)  // Pasar el ID del empleado al fragmento
                        }
                    }
                }
                R.id.navigation_team -> {
                    // Navegar al fragmento TeamFragment
                    selectedFragment = TeamFragment()  // Cargar el fragmento de lista de empleados
                }
                R.id.navigation_logout -> {
                    // Cerrar sesión
                    logoutUser()
                    return@setOnNavigationItemSelectedListener true
                }
            }
            if (selectedFragment != null) {
                loadFragment(selectedFragment)
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.contentFrame, fragment)
            .commit()
    }

    private fun logoutUser() {
        // Crear un AlertDialog para confirmar la acción
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro de que deseas cerrar sesión?")
            .setPositiveButton("Sí") { dialog, which ->
                // Si el usuario elige "Sí", cerrar sesión
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish() // Cierra MainActivity
            }
            .setNegativeButton("No") { dialog, which ->
                // Si elige "No", simplemente cerrar el diálogo
                dialog.dismiss()
            }
        // Mostrar el diálogo
        builder.create().show()
    }


}
