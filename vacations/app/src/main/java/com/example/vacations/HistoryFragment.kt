package com.example.vacations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class HistoryFragment : Fragment() {

    private lateinit var dbHelper: VacationDbHelper
    private lateinit var listViewRequests: ListView
    private lateinit var requestAdapter: ArrayAdapter<String>
    private var employeeId: Int = -1 // Almacena el ID del empleado

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        listViewRequests = view.findViewById(R.id.listViewRequests)
        dbHelper = VacationDbHelper(requireContext())

        // Obtener el ID del empleado de los argumentos
        employeeId = arguments?.getInt("employeeId") ?: -1

        if (employeeId == -1) {
            // Maneja el error si no se encuentra el ID
            return view
        }

        // Obtener solicitudes
        val requests = dbHelper.getVacationRequests(employeeId)

        // Adaptador para mostrar solicitudes
        requestAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            requests.map { "${it.startDate} - ${it.endDate} (${it.status}): ${it.comment}" }
        )
        listViewRequests.adapter = requestAdapter

        return view
    }

    override fun onResume() {
        super.onResume()
        // Actualiza la lista al volver al fragmento
        val requests = dbHelper.getVacationRequests(employeeId)
        requestAdapter.clear()
        requestAdapter.addAll(requests.map { "${it.startDate} - ${it.endDate} (${it.status}): ${it.comment}" })
        requestAdapter.notifyDataSetChanged()
    }
}
