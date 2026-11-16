package es.centro.gestortareas.model

import androidx.annotation.StringRes
import es.centro.gestortareas.R

/** Reglas de validación del formulario. Funciones puras: devuelven el recurso del error o null. */
object ValidacionTarea {
    const val LONGITUD_MINIMA_TITULO = 3

    @StringRes
    fun errorTitulo(titulo: String): Int? {
        val limpio = titulo.trim()
        return when {
            limpio.isEmpty() -> R.string.error_titulo_vacio
            limpio.length < LONGITUD_MINIMA_TITULO -> R.string.error_titulo_corto
            else -> null
        }
    }

    @StringRes
    fun errorPrioridad(prioridad: Prioridad?): Int? =
        if (prioridad == null) R.string.error_prioridad else null
}
