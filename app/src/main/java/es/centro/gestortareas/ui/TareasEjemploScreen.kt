package es.centro.gestortareas.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.centro.gestortareas.R
import es.centro.gestortareas.data.remote.EstadoCarga
import es.centro.gestortareas.data.remote.TareaRemota

/** Pantalla «Tareas de ejemplo»: datos de JSONPlaceholder, separados de las tareas personales. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareasEjemploScreen(
    vm: EjemploViewModel,
    alVolver: () -> Unit
) {
    val estado = vm.estado

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ejemplo_titulo)) },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.volver)
                        )
                    }
                }
            )
        }
    ) { padding ->
        ContenidoAdaptable(padding) {
            Text(
                text = stringResource(R.string.ejemplo_aviso),
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = vm::cargar,
                enabled = estado !is EstadoCarga.Cargando,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Text(stringResource(R.string.cargar_tareas))
            }

            when (estado) {
                EstadoCarga.Inicial -> Text(stringResource(R.string.ejemplo_inicial))

                EstadoCarga.Cargando -> {
                    CircularProgressIndicator()
                    Text(stringResource(R.string.cargando), modifier = Modifier.padding(top = 8.dp))
                }

                is EstadoCarga.Datos ->
                    if (estado.tareas.isEmpty()) {
                        Text(stringResource(R.string.ejemplo_vacio))
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(estado.tareas, key = { it.id }) { tarea -> TarjetaRemota(tarea) }
                        }
                    }

                is EstadoCarga.Error -> {
                    val mensaje = if (estado.codigo != null) {
                        stringResource(estado.mensaje, estado.codigo)
                    } else {
                        stringResource(estado.mensaje)
                    }
                    Text(text = mensaje, color = MaterialTheme.colorScheme.error)
                    Button(onClick = vm::cargar, modifier = Modifier.padding(top = 8.dp)) {
                        Text(stringResource(R.string.reintentar))
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaRemota(tarea: TareaRemota) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = stringResource(R.string.ejemplo_item_id, tarea.id),
                style = MaterialTheme.typography.labelMedium
            )
            Text(text = tarea.title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(
                    if (tarea.completed) R.string.estado_completada else R.string.estado_pendiente
                ),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
