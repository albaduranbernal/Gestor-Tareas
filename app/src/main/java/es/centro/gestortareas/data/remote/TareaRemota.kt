package es.centro.gestortareas.data.remote

/** Tarea de ejemplo devuelta por JSONPlaceholder (GET /todos?userId=1). */
data class TareaRemota(
    val userId: Int,
    val id: Int,
    val title: String,
    val completed: Boolean
)
