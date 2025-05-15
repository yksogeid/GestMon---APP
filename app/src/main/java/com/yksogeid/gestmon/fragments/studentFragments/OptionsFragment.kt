package com.yksogeid.gestmon.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.yksogeid.gestmon.R

class OptionsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_options, container, false)

        // Manejar el botón de retroceso (flecha)
        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Puedes agregar más inicializaciones aquí si necesitas

        return view
    }

    companion object {
        fun newInstance() = OptionsFragment()
    }
}
