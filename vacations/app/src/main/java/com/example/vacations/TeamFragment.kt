package com.example.vacations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TeamFragment : Fragment() {
    private lateinit var dbHelper: VacationDbHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var employeeAdapter: EmployeeAdapter
    private lateinit var employeeList: List<EmployeeInfo>  // Cambiamos a EmployeeInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_team, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewEmployees)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        dbHelper = VacationDbHelper(requireContext())
        employeeList = getEmployees()  // Obtener la lista de empleados desde la base de datos
        employeeAdapter = EmployeeAdapter(employeeList)
        recyclerView.adapter = employeeAdapter

        return view
    }

    private fun getEmployees(): List<EmployeeInfo> {  // Cambiamos a EmployeeInfo
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT username, available_days FROM employee", null)
        val employees = mutableListOf<EmployeeInfo>()

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndexOrThrow("username"))
                val availableDays = cursor.getInt(cursor.getColumnIndexOrThrow("available_days"))
                employees.add(EmployeeInfo(name, availableDays))  // Cambiamos a EmployeeInfo
            } while (cursor.moveToNext())
        }
        cursor.close()

        return employees
    }
}
