package es.centro.gestortareas.ui

import android.app.Application
import android.database.sqlite.SQLiteException
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import es.centro.gestortareas.R
import es.centro.gestortareas.data.local.AppDatabase
import es.centro.gestortareas.data.local.aEntidad
import es.centro.gestortareas.data.local.aModelo
import es.centro.gestortareas.data.preferences.guardarFiltro
import es.centro.gestortareas.data.preferences.observarFiltro
import es.centro.gestortareas.model.Prioridad
import es.centro.gestortareas.model.Tarea
import java.io.IOException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Estado de la pantalla principal: datos de Room + preferencia de DataStore. */
data class ListaUi(
    val cargando: Boolean = true,
    val todas: List<Tarea> = emptyList(),
    val soloPendientes: Boolean = false
) {
    val visibles: List<Tarea>
        get() = if (soloPendientes) todas.filter { !it.completada } else todas
}

class TareasViewModel(application: Application) : AndroidViewModel(application) {

    private val contexto = application.applicationContext
    private val dao = AppDatabase.obtener(contexto).tareaDao()

    val estado: StateFlow<ListaUi> = combine(
        dao.observar().map { filas -> filas.map { it.aModelo() } },
        observarFiltro(contexto)
    ) { tareas, soloPendientes ->
        ListaUi(cargando = false, todas = tareas, soloPendientes = soloPendientes)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ListaUi())

    /** true mientras se está escribiendo en la base: evita guardados duplicados. */
    var guardando by mutableStateOf(false)
        private set

    /** Recurso del último error de almacenamiento (null si no hay). */
    @get:StringRes
    var error: Int? by mutableStateOf(null)
        private set

    fun consumirError() {
        error = null
    }

    /**
     * Crea (original == null) o modifica una tarea. [alTerminar] solo se llama si la escritura
     * se completó correctamente.
     */
    fun guardar(
        original: Tarea?,
        titulo: String,
        descripcion: String,
        prioridad: Prioridad,
        alTerminar: () -> Unit
    ) {
        if (guardando) return
        guardando = true
        viewModelScope.launch {
            try {
                val tarea = original?.copy(
                    titulo = titulo.trim(),
                    descripcion = descripcion.trim(),
                    prioridad = prioridad
                ) ?: Tarea(0, titulo.trim(), descripcion.trim(), prioridad, false)
                if (original == null) dao.insertar(tarea.aEntidad()) else dao.actualizar(tarea.aEntidad())
                alTerminar()
            } catch (e: SQLiteException) {
                error = R.string.error_guardar
            } finally {
                guardando = false
            }
        }
    }

    fun cambiarCompletada(tarea: Tarea, completada: Boolean) {
        viewModelScope.launch {
            try {
                dao.actualizar(tarea.copy(completada = completada).aEntidad())
            } catch (e: SQLiteException) {
                error = R.string.error_actualizar
            }
        }
    }

    fun eliminar(tarea: Tarea) {
        viewModelScope.launch {
            try {
                dao.eliminar(tarea.aEntidad())
            } catch (e: SQLiteException) {
                error = R.string.error_eliminar
            }
        }
    }

    fun cambiarFiltro(soloPendientes: Boolean) {
        viewModelScope.launch {
            try {
                guardarFiltro(contexto, soloPendientes)
            } catch (e: IOException) {
                error = R.string.error_filtro
            }
        }
    }
}
