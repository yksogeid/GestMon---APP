package com.yksogeid.gestmon.adapters

import com.yksogeid.gestmon.models.Materia
import com.yksogeid.gestmon.R  // Add this import
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip

class MateriaAdapter : RecyclerView.Adapter<MateriaAdapter.MateriaViewHolder>() {
    private var materias: List<Materia> = emptyList()

    fun setData(newMaterias: List<Materia>) {
        materias = newMaterias
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MateriaViewHolder {
        val chip = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_materia, parent, false) as Chip
        return MateriaViewHolder(chip)
    }

    override fun onBindViewHolder(holder: MateriaViewHolder, position: Int) {
        holder.bind(materias[position])
    }

    override fun getItemCount() = materias.size

    class MateriaViewHolder(private val chip: Chip) : RecyclerView.ViewHolder(chip) {
        fun bind(materia: Materia) {
            chip.text = materia.nombre
        }
    }
}