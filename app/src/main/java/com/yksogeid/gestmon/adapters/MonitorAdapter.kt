package com.yksogeid.gestmon.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yksogeid.gestmon.R
import com.yksogeid.gestmon.models.Monitor
import com.google.android.material.chip.ChipGroup

class MonitorAdapter : RecyclerView.Adapter<MonitorAdapter.MonitorViewHolder>() {
    private var monitores: List<Monitor> = emptyList()

    fun setData(newMonitores: List<Monitor>) {
        monitores = newMonitores
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonitorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_monitor, parent, false)
        return MonitorViewHolder(view)
    }

    override fun onBindViewHolder(holder: MonitorViewHolder, position: Int) {
        holder.bind(monitores[position])
    }

    override fun getItemCount() = monitores.size

    class MonitorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombreMonitor: TextView = itemView.findViewById(R.id.tvNombreMonitor)
        private val carreraMonitor: TextView = itemView.findViewById(R.id.carreraMonitor)
        private val chipGroupMaterias: ChipGroup = itemView.findViewById(R.id.chipGroupMaterias)

        fun bind(monitor: Monitor) {
            tvNombreMonitor.text = monitor.nombre
            carreraMonitor.text = monitor.carrera
            chipGroupMaterias.removeAllViews()
            
            monitor.materias.forEach { materia ->
                val chip = LayoutInflater.from(itemView.context)
                    .inflate(R.layout.item_materia, chipGroupMaterias, false) as com.google.android.material.chip.Chip
                chip.text = materia.nombre
                chipGroupMaterias.addView(chip)
            }
        }
    }
}