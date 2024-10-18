package com.example.vacations

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class PendingRequestsFragment : Fragment() {
    private lateinit var dbHelper: VacationDbHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pending_requests, container, false)

        dbHelper = VacationDbHelper(requireContext())
        loadPendingRequests(view)

        return view
    }

    private fun loadPendingRequests(view: View) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM vacation WHERE status = 'pending'", null)
        val container = view.findViewById<LinearLayout>(R.id.requestsContainer)

        if (cursor.moveToFirst()) {
            do {
                val requestId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val employeeId = cursor.getInt(cursor.getColumnIndexOrThrow("employee_id"))
                val startDate = cursor.getString(cursor.getColumnIndexOrThrow("start_date"))
                val endDate = cursor.getString(cursor.getColumnIndexOrThrow("end_date"))
                val daysRequested = cursor.getInt(cursor.getColumnIndexOrThrow("available_days"))
                val comment = cursor.getString(cursor.getColumnIndexOrThrow("comment"))

                // Crear la vista para cada solicitud
                val requestView = layoutInflater.inflate(R.layout.item_request, container, false)
                val textView = requestView.findViewById<TextView>(R.id.requestInfo)
                textView.text = "Empleado: $employeeId\nInicio: $startDate\nFin: $endDate\nDías: $daysRequested\nComentario: $comment"

                // Botón de aprobar solicitud
                val approveButton = requestView.findViewById<Button>(R.id.approveButton)
                approveButton.setOnClickListener {
                    approveRequest(requestId, view)
                }

                // Botón de rechazar solicitud
                val rejectButton = requestView.findViewById<Button>(R.id.rejectButton)
                rejectButton.setOnClickListener {
                    rejectRequest(requestId, view)
                }

                // Añadir la vista al contenedor
                container.addView(requestView)
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    private fun approveRequest(requestId: Int, view: View) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("status", "approved")  // Cambiar el estado a "aprobado"
        }
        db.update("vacation", values, "id=?", arrayOf(requestId.toString()))

        Toast.makeText(requireContext(), "Solicitud aprobada", Toast.LENGTH_SHORT).show()
        reloadRequests(view)
    }

    private fun rejectRequest(requestId: Int, view: View) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("status", "rejected")  // Cambiar el estado a "rechazado"
        }
        db.update("vacation", values, "id=?", arrayOf(requestId.toString()))

        Toast.makeText(requireContext(), "Solicitud rechazada", Toast.LENGTH_SHORT).show()
        reloadRequests(view)
    }

    private fun reloadRequests(view: View) {
        val container = view.findViewById<LinearLayout>(R.id.requestsContainer)
        container.removeAllViews()  // Limpiar las solicitudes antes de recargar
        loadPendingRequests(view)  // Recargar las solicitudes
    }
}
