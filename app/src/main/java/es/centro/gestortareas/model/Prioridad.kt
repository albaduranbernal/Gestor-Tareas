package es.centro.gestortareas.model

import androidx.annotation.StringRes
import es.centro.gestortareas.R

/** Las tres prioridades permitidas para una tarea. */
enum class Prioridad(@StringRes val etiqueta: Int) {
    BAJA(R.string.prioridad_baja),
    MEDIA(R.string.prioridad_media),
    ALTA(R.string.prioridad_alta);

    companion object {
        /** Devuelve la prioridad con ese nombre o null si no es una de las tres permitidas. */
        fun desdeNombre(nombre: String?): Prioridad? = entries.firstOrNull { it.name == nombre }
    }
}
