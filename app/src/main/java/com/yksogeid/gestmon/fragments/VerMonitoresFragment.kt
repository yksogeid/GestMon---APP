package com.yksogeid.gestmon.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.yksogeid.gestmon.R
import com.yksogeid.gestmon.services.MateriaResponse
import com.yksogeid.gestmon.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class VerMonitoresFragment : Fragment() {
    private lateinit var spinnerMateria: Spinner
    private lateinit var materiasList: List<MateriaResponse>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ver_monitores, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        spinnerMateria = view.findViewById(R.id.spinnerMateria)
        fetchMaterias()
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