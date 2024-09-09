package com.example.vacations

import android.app.DatePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class RequestFragment : Fragment() {
    private lateinit var dbHelper: VacationDbHelper
    private var employeeId: Int = -1  // ID del empleado logueado
    private var availableDays: Int = 0  // Días de vacaciones disponibles
    private lateinit var startDate: String
    private lateinit var endDate: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_request, container, false)

        dbHelper = VacationDbHelper(requireContext())

        // Obtener el ID del empleado pasado desde MainActivity
        employeeId = arguments?.getInt("employeeId") ?: -1
        if (employeeId == -1) {
            Toast.makeText(requireContext(), "Error: Employee ID not found", Toast.LENGTH_SHORT).show()
            return view
        }

        // Mostrar los días de vacaciones disponibles
        availableDays = getAvailableDays(employeeId)
        val textViewAvailableDays = view.findViewById<TextView>(R.id.textViewAvailableDays)
        textViewAvailableDays.text = "Available days: $availableDays"

        // Configurar los botones de fecha
        val buttonStartDate = view.findViewById<Button>(R.id.buttonStartDate)
        val buttonEndDate = view.findViewById<Button>(R.id.buttonEndDate)
        buttonStartDate.setOnClickListener { showDatePickerDialog(true) }
        buttonEndDate.setOnClickListener { showDatePickerDialog(false) }

        // Configurar el botón de solicitud
        val btnRequest = view.findViewById<Button>(R.id.btnRequest)
        val editTextComment = view.findViewById<EditText>(R.id.editTextComment)

        btnRequest.setOnClickListener {
            if (::startDate.isInitialized && ::endDate.isInitialized) {
                val daysRequested = calculateDaysRequested(startDate, endDate)
                if (daysRequested > 0 && availableDays >= daysRequested) {
                    val comment = editTextComment.text.toString()
                    addRequest(startDate, endDate, daysRequested, comment)
                    Toast.makeText(requireContext(), "Request submitted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Not enough available days or invalid dates", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please select both start and end dates", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    // Mostrar el selector de fecha
    private fun showDatePickerDialog(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = "$year-${month + 1}-$dayOfMonth"
                if (isStartDate) {
                    startDate = selectedDate
                    view?.findViewById<Button>(R.id.buttonStartDate)?.text = "Start Date: $selectedDate"
                } else {
                    endDate = selectedDate
                    view?.findViewById<Button>(R.id.buttonEndDate)?.text = "End Date: $selectedDate"
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    // Calcular los días solicitados entre las dos fechas seleccionadas
    private fun calculateDaysRequested(startDate: String, endDate: String): Int {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val start = dateFormat.parse(startDate)
        val end = dateFormat.parse(endDate)
        val diff = end.time - start.time
        return (diff / (1000 * 60 * 60 * 24)).toInt() + 1
    }

    // Obtener los días disponibles del empleado
    private fun getAvailableDays(employeeId: Int): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT available_days FROM employee WHERE id = ?", arrayOf(employeeId.toString()))
        var availableDays = 0
        if (cursor.moveToFirst()) {
            availableDays = cursor.getInt(cursor.getColumnIndexOrThrow("available_days"))
        }
        cursor.close()
        return availableDays
    }

    // Añadir la solicitud a la base de datos con comentarios
    private fun addRequest(startDate: String, endDate: String, daysRequested: Int, comment: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("employee_id", employeeId)
            put("start_date", startDate)
            put("end_date", endDate)
            put("available_days", daysRequested)
            put("status", "pending")
            put("comment", comment)
        }
        val newRowId = db.insert("vacation", null, values)

        if (newRowId != -1L) {
            db.execSQL("UPDATE employee SET available_days = available_days - ? WHERE id = ?", arrayOf(daysRequested, employeeId))
            Log.d("RequestFragment", "Request inserted with ID: $newRowId. Days updated for employee ID: $employeeId")
        } else {
            Log.e("RequestFragment", "Error inserting the request")
        }
    }
}
