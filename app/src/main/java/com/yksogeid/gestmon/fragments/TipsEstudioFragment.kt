package com.yksogeid.gestmon.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yksogeid.gestmon.R

class TipsEstudioFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tips_estudio, container, false)
    }

    companion object {
        fun newInstance() = TipsEstudioFragment()
    }
}
