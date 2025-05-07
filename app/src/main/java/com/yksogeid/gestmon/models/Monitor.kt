package com.yksogeid.gestmon.models
data class Monitor(
    val nombre: String,
    val carrera: String,
    val materias: List<MonitorMateria>
)

data class MonitorMateria(
    val nombre: String,
    val estado: String,
    val id: Int
)