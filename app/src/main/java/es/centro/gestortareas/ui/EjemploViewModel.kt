package es.centro.gestortareas.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.centro.gestortareas.data.remote.EstadoCarga
import es.centro.gestortareas.data.remote.ServicioTareas
import es.centro.gestortareas.data.remote.cargarTareasRemotas
import kotlinx.coroutines.launch

/** Gestiona la consulta a JSONPlaceholder sin bloquear el hilo principal. */
class EjemploViewModel : ViewModel() {

    var estado: EstadoCarga by mutableStateOf(EstadoCarga.Inicial)
        private set

    fun cargar() {
        if (estado is EstadoCarga.Cargando) return // evita peticiones simultáneas
        estado = EstadoCarga.Cargando
        viewModelScope.launch {
            estado = cargarTareasRemotas(ServicioTareas.api)
        }
    }
}
