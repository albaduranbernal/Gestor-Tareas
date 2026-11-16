package es.centro.gestortareas.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import es.centro.gestortareas.R
import es.centro.gestortareas.model.Tarea

/**
 * Abre el selector de aplicaciones con el texto de la tarea.
 * Devuelve true si se pudo abrir el selector; eso NO significa que se haya compartido nada.
 */
fun compartirTarea(context: Context, tarea: Tarea): Boolean {
    val descripcion = tarea.descripcion.ifBlank { context.getString(R.string.share_sin_descripcion) }
    val texto = context.getString(
        R.string.share_texto,
        tarea.titulo,
        descripcion,
        context.getString(tarea.prioridad.etiqueta)
    )
    val envio = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, tarea.titulo)
        putExtra(Intent.EXTRA_TEXT, texto)
    }
    return try {
        context.startActivity(Intent.createChooser(envio, context.getString(R.string.share_selector)))
        true
    } catch (e: ActivityNotFoundException) {
        false
    }
}
