package com.example.vacations

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
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
            .replace(R.id.contentFrameBoss, fragment)
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
