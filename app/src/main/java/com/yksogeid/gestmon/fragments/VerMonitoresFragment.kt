package com.yksogeid.gestmon.fragments

import com.yksogeid.gestmon.models.Monitor
import android.os.Bundle
import android.util.Log
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

    private var allMonitores: List<Monitor> = emptyList()

    private fun fetchMonitores() {
        RetrofitClient.apiService.getMonitores().enqueue(object : Callback<List<Monitor>> {
            override fun onResponse(call: Call<List<Monitor>>, response: Response<List<Monitor>>) {
                if (response.isSuccessful) {
                    allMonitores = response.body() ?: emptyList()
                    activity?.runOnUiThread {
                        monitorAdapter.setData(allMonitores)
                    }
                }
            }
            override fun onFailure(call: Call<List<Monitor>>, t: Throwable) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Error al cargar monitores", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun performSearch() {
        val nombreBusqueda = view?.findViewById<EditText>(R.id.etNombre)?.text.toString().trim().lowercase()
        val materiaSeleccionada = materiasList[spinnerMateria.selectedItemPosition]

        // Log de búsqueda
        Log.d("FiltroBusqueda", "Nombre buscado: '$nombreBusqueda'")
        Log.d("FiltroBusqueda", "Materia seleccionada: '${materiaSeleccionada.nombre}'")

        val filteredMonitores = allMonitores.filter { monitor ->
            Log.d("FiltroMonitor", "Revisando monitor: ${monitor.nombre} - Materias: ${monitor.materias.map { it.nombre }}")

            val matchesNombre = if (nombreBusqueda.isNotEmpty()) {
                monitor.nombre.trim().lowercase().contains(nombreBusqueda)
            } else true

            val matchesMateria = if (materiaSeleccionada.nombre != "Todas las materias") {
                monitor.materias.any {
                    it.nombre.trim().lowercase() == materiaSeleccionada.nombre.trim().lowercase()
                }
            } else true

            matchesNombre && matchesMateria
        }

        monitorAdapter.setData(filteredMonitores)
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
        // Crear un objeto MateriaResponse para "Todas las materias"
        val todasLasMaterias = MateriaResponse(idmateria = 0, nombre = "Todas las materias", created_at = "", updated_at = "")

        // Crear una nueva lista con la opción "Todas las materias" al inicio
        val listaConTodas = listOf(todasLasMaterias) + materiasList

        // Asignar la lista con "Todas las materias"
        materiasList = listaConTodas

        // Adaptar los nombres de las materias para el spinner
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            materiasList.map { it.nombre }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerMateria.adapter = adapter
    }

    companion object {
        fun newInstance() = VerMonitoresFragment()
    }
}