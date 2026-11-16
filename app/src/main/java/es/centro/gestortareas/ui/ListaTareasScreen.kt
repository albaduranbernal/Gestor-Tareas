package es.centro.gestortareas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import es.centro.gestortareas.R
import es.centro.gestortareas.model.Tarea
import es.centro.gestortareas.util.compartirTarea
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaTareasScreen(
    vm: TareasViewModel,
    alCrear: () -> Unit,
    alEditar: (Long) -> Unit,
    alAbrirEjemplo: () -> Unit
) {
    val ui by vm.estado.collectAsStateWithLifecycle()
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    // Los errores de Room/DataStore se resuelven a texto en la composición y se muestran una vez.
    val mensajeError = vm.error?.let { stringResource(it) }
    LaunchedEffect(mensajeError) {
        if (mensajeError != null) {
            snackbar.showSnackbar(mensajeError)
            vm.consumirError()
        }
    }

    val textoSinApp = stringResource(R.string.share_sin_app)
    var idAEliminar by rememberSaveable { mutableStateOf<Long?>(null) }
    val tareaAEliminar = ui.todas.firstOrNull { it.id == idAEliminar }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_tarea),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = stringResource(R.string.titulo_lista),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                },
                actions = {
                    TextButton(onClick = alAbrirEjemplo) {
                        Text(stringResource(R.string.ejemplo_titulo))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = alCrear) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.nueva_tarea))
            }
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        ContenidoAdaptable(padding) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.solo_pendientes),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                )
                Switch(checked = ui.soloPendientes, onCheckedChange = vm::cambiarFiltro)
            }

            Text(
                text = stringResource(R.string.seccion_personales),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
            )

            when {
                ui.cargando -> {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 24.dp))
                    Text(stringResource(R.string.cargando_tareas), modifier = Modifier.padding(top = 8.dp))
                }

                ui.visibles.isEmpty() -> Text(
                    text = stringResource(
                        if (ui.todas.isEmpty()) R.string.sin_tareas else R.string.sin_pendientes
                    ),
                    modifier = Modifier.padding(top = 24.dp)
                )

                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ui.visibles, key = { it.id }) { tarea ->
                        TarjetaTarea(
                            tarea = tarea,
                            alCambiarCompletada = { vm.cambiarCompletada(tarea, it) },
                            alEditar = { alEditar(tarea.id) },
                            alEliminar = { idAEliminar = tarea.id },
                            alCompartir = {
                                if (!compartirTarea(contexto, tarea)) {
                                    scope.launch { snackbar.showSnackbar(textoSinApp) }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (tareaAEliminar != null) {
        AlertDialog(
            onDismissRequest = { idAEliminar = null },
            title = { Text(stringResource(R.string.eliminar_titulo)) },
            text = { Text(stringResource(R.string.eliminar_mensaje, tareaAEliminar.titulo)) },
            confirmButton = {
                TextButton(onClick = {
                    vm.eliminar(tareaAEliminar)
                    idAEliminar = null
                }) { Text(stringResource(R.string.eliminar_confirmar)) }
            },
            dismissButton = {
                TextButton(onClick = { idAEliminar = null }) {
                    Text(stringResource(R.string.cancelar))
                }
            }
        )
    }
}

@Composable
private fun TarjetaTarea(
    tarea: Tarea,
    alCambiarCompletada: (Boolean) -> Unit,
    alEditar: () -> Unit,
    alEliminar: () -> Unit,
    alCompartir: () -> Unit
) {
    val estado = stringResource(
        if (tarea.completada) R.string.estado_completada else R.string.estado_pendiente
    )
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = tarea.completada, onCheckedChange = alCambiarCompletada)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tarea.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (tarea.completada) TextDecoration.LineThrough else null
                    )
                    Text(
                        text = stringResource(
                            R.string.prioridad_y_estado,
                            stringResource(tarea.prioridad.etiqueta),
                            estado
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = alCompartir) {
                    Icon(Icons.Default.Share, contentDescription = stringResource(R.string.accion_compartir))
                }
                IconButton(onClick = alEditar) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.accion_editar))
                }
                IconButton(onClick = alEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.accion_eliminar))
                }
            }
        }
    }
}
