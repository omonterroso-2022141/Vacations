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
    private var employeeId: Int = -1
    private var availableDays: Int = 0
    private lateinit var startDate: String
    private lateinit var endDate: String


    private val holidays = listOf(
        "2024-01-01",
        "2024-12-25",
        "2024-09-15"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_request, container, false)

        dbHelper = VacationDbHelper(requireContext())


        employeeId = arguments?.getInt("employeeId") ?: -1
        if (employeeId == -1) {
            Toast.makeText(requireContext(), "Error: Employee ID not found", Toast.LENGTH_SHORT).show()
            return view
        }


        availableDays = getAvailableDays(employeeId)
        val textViewAvailableDays = view.findViewById<TextView>(R.id.textViewAvailableDays)
        textViewAvailableDays.text = "$availableDays"

        val buttonStartDate = view.findViewById<Button>(R.id.buttonStartDate)
        val buttonEndDate = view.findViewById<Button>(R.id.buttonEndDate)
        val btnRequest = view.findViewById<Button>(R.id.btnRequest)
        val editTextComment = view.findViewById<EditText>(R.id.editTextComment)


        buttonStartDate.setOnClickListener { showDatePickerDialog(true) }
        buttonEndDate.setOnClickListener { showDatePickerDialog(false) }


        btnRequest.setOnClickListener {
            if (::startDate.isInitialized && ::endDate.isInitialized) {
                val daysRequested = calculateDaysRequested(startDate, endDate)
                if (daysRequested > 0) {

                    if (isValidDateRange(startDate, endDate)) {

                        if (daysRequested <= availableDays) {
                            val comment = editTextComment.text.toString()
                            addRequest(startDate, endDate, daysRequested, comment)
                            Toast.makeText(requireContext(), "Request submitted", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "You cannot request more days than available", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Selected dates include weekends or holidays", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Invalid date range", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please select both start and end dates", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }


    private fun showDatePickerDialog(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = "$year-${month + 1}-$dayOfMonth"
                if (isStartDate) {
                    startDate = selectedDate
                    view?.findViewById<Button>(R.id.buttonStartDate)?.text = "Fecha de inicio: $selectedDate"
                } else {
                    endDate = selectedDate
                    view?.findViewById<Button>(R.id.buttonEndDate)?.text = "Fecha de finalización: $selectedDate"
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    // Validar si las fechas seleccionadas caen en fines de semana o días festivos
    private fun isValidDateRange(startDate: String, endDate: String): Boolean {
        return !isWeekend(startDate) && !isHoliday(startDate, holidays) &&
                !isWeekend(endDate) && !isHoliday(endDate, holidays)
    }


    private fun isWeekend(date: String): Boolean {
        val dateParts = date.split("-")
        val year = dateParts[0].toInt()
        val month = dateParts[1].toInt() - 1
        val day = dateParts[2].toInt()

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
    }


    private fun isHoliday(date: String, holidays: List<String>): Boolean {
        return holidays.contains(date)
    }


    private fun calculateDaysRequested(startDate: String, endDate: String): Int {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val start = dateFormat.parse(startDate)
        val end = dateFormat.parse(endDate)
        val diff = end.time - start.time
        return (diff / (1000 * 60 * 60 * 24)).toInt() + 1
    }


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
