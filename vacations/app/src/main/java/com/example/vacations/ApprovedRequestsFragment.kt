package com.example.vacations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class ApprovedRequestsFragment : Fragment() {
    private lateinit var dbHelper: VacationDbHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_approved_requests, container, false)

        dbHelper = VacationDbHelper(requireContext())
        loadApprovedRequests(view)

        return view
    }

    private fun loadApprovedRequests(view: View) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM vacation WHERE status = 'approved' OR status = 'rejected'", null)
        val container = view.findViewById<LinearLayout>(R.id.approvedRequestsContainer)

        if (cursor.moveToFirst()) {
            do {
                val employeeId = cursor.getInt(cursor.getColumnIndexOrThrow("employee_id"))
                val startDate = cursor.getString(cursor.getColumnIndexOrThrow("start_date"))
                val endDate = cursor.getString(cursor.getColumnIndexOrThrow("end_date"))
                val daysRequested = cursor.getInt(cursor.getColumnIndexOrThrow("available_days"))
                val comment = cursor.getString(cursor.getColumnIndexOrThrow("comment"))
                val status = cursor.getString(cursor.getColumnIndexOrThrow("status"))

                // Crear la vista para cada solicitud aprobada o rechazada
                val requestView = layoutInflater.inflate(R.layout.item_approved_request, container, false)
                val textView = requestView.findViewById<TextView>(R.id.approvedRequestInfo)
                textView.text = "Identificador: $employeeId\nInicio: $startDate\nFin: $endDate\nDías: $daysRequested\nComentario: $comment\nEstado: $status"

                // Añadir la vista al contenedor
                container.addView(requestView)
            } while (cursor.moveToNext())
        }
        cursor.close()
    }
}
