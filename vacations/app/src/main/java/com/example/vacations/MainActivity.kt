package com.example.vacations

import android.os.Bundle
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

        // Configurar la navegación inferior
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // Cargar el fragmento por defecto (HomeFragment)
        loadFragment(HomeFragment())

        // Manejar la selección de los elementos del BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.navigation_home -> selectedFragment = HomeFragment()
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
}
