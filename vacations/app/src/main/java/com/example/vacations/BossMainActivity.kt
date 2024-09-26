package com.example.vacations

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class BossMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boss_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationBoss)

        // Cargar el fragmento por defecto (Lista de solicitudes pendientes)
        loadFragment(PendingRequestsFragment())

        // Manejar la navegación del menú
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.navigation_pending_requests -> selectedFragment = PendingRequestsFragment()
                R.id.navigation_approved_requests -> selectedFragment = ApprovedRequestsFragment()
                R.id.navigation_team -> selectedFragment = TeamFragment()  // Opción para la lista de empleados
            }
            if (selectedFragment != null) {
                loadFragment(selectedFragment)
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.contentFrameBoss, fragment)
            .commit()
    }
}
