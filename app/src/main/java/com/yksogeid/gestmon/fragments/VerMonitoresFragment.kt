package com.yksogeid.gestmon.fragments

import com.yksogeid.gestmon.models.Monitor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yksogeid.gestmon.R
import com.yksogeid.gestmon.adapters.MonitorAdapter
import com.yksogeid.gestmon.models.*
import com.yksogeid.gestmon.services.MateriaResponse
import com.yksogeid.gestmon.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerMonitoresFragment : Fragment() {
    private lateinit var spinnerMateria: Spinner
    private lateinit var materiasList: List<MateriaResponse>
    private lateinit var rvMonitores: RecyclerView
    private val monitorAdapter = MonitorAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ver_monitores, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews(view)
        fetchMaterias()
        fetchMonitores()
    }

    private fun setupViews(view: View) {
        spinnerMateria = view.findViewById(R.id.spinnerMateria)
        rvMonitores = view.findViewById(R.id.rvMonitores)
        
        rvMonitores.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = monitorAdapter
        }

        view.findViewById<ImageButton>(R.id.btnSearch).setOnClickListener {
            performSearch()
        }
    }

    private fun fetchMonitores() {
        RetrofitClient.apiService.getMonitores().enqueue(object : Callback<List<Monitor>> {
            override fun onFailure(call: Call<List<Monitor>>, t: Throwable) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Error al cargar monitores", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call<List<Monitor>>, response: Response<List<Monitor>>) {
                if (response.isSuccessful) {
                    val monitores = response.body() ?: emptyList()
                    activity?.runOnUiThread {
                        monitorAdapter.setData(monitores)
                    }
                }
            }
        })
    }

    private fun performSearch() {
        val nombreBusqueda = view?.findViewById<EditText>(R.id.etNombre)?.text.toString()
        val materiaSeleccionada = if (spinnerMateria.selectedItemPosition > 0) {
            materiasList[spinnerMateria.selectedItemPosition - 1]
        } else null

        // Implement search logic here
        // You can filter the monitors based on nombre and materia
    }

    private fun fetchMaterias() {
        RetrofitClient.apiService.getMaterias().enqueue(object : Callback<List<MateriaResponse>> {
            override fun onFailure(call: Call<List<MateriaResponse>>, t: Throwable) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Error al cargar materias", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(
                call: Call<List<MateriaResponse>>,
                response: Response<List<MateriaResponse>>
            ) {
                if (response.isSuccessful) {
                    materiasList = response.body() ?: emptyList()
                    activity?.runOnUiThread {
                        setupSpinner()
                    }
                }
            }
        })
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            materiasList.map { it.nombre }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        
        spinnerMateria.adapter = adapter
        
        spinnerMateria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMateria = materiasList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    companion object {
        fun newInstance() = VerMonitoresFragment()
    }
}