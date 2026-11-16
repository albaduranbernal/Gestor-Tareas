package es.centro.gestortareas.model

/** Tarea personal tal y como la usa la interfaz. */
data class Tarea(
    val id: Long,
    val titulo: String,
    val descripcion: String,
    val prioridad: Prioridad,
    val completada: Boolean
)
