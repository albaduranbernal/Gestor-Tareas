package es.centro.gestortareas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import es.centro.gestortareas.R
import es.centro.gestortareas.model.Prioridad
import es.centro.gestortareas.model.Tarea
import es.centro.gestortareas.model.ValidacionTarea

/** Formulario de creación (idEdicion == null) o edición de una tarea. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioTareaScreen(
    vm: TareasViewModel,
    idEdicion: Long?,
    alVolver: () -> Unit
) {
    val ui by vm.estado.collectAsStateWithLifecycle()
    val original = idEdicion?.let { id -> ui.todas.firstOrNull { it.id == id } }
    val esperandoDatos = idEdicion != null && original == null

    // Si se edita una tarea que ya no existe, se vuelve a la lista cuando termina la carga.
    LaunchedEffect(esperandoDatos, ui.cargando) {
        if (esperandoDatos && !ui.cargando) alVolver()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (idEdicion == null) R.string.nueva_tarea else R.string.editar_tarea
                        )
                    )
                },
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
        ContenidoAdaptable(padding, anchoMaximo = 600.dp) {
            if (esperandoDatos) {
                CircularProgressIndicator()
            } else {
                CamposFormulario(vm = vm, original = original, alVolver = alVolver)
            }
        }
    }
}

@Composable
private fun CamposFormulario(
    vm: TareasViewModel,
    original: Tarea?,
    alVolver: () -> Unit
) {
    var titulo by rememberSaveable { mutableStateOf(original?.titulo ?: "") }
    var descripcion by rememberSaveable { mutableStateOf(original?.descripcion ?: "") }
    var prioridadNombre by rememberSaveable {
        mutableStateOf((original?.prioridad ?: Prioridad.MEDIA).name)
    }
    var intento by rememberSaveable { mutableStateOf(false) }
    var menuAbierto by remember { mutableStateOf(false) }

    val prioridad = Prioridad.desdeNombre(prioridadNombre)
    val errorTitulo = ValidacionTarea.errorTitulo(titulo)
    val errorPrioridad = ValidacionTarea.errorPrioridad(prioridad)

    androidx.compose.foundation.layout.Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text(stringResource(R.string.campo_titulo)) },
            singleLine = true,
            enabled = !vm.guardando,
            isError = intento && errorTitulo != null,
            supportingText = {
                if (intento && errorTitulo != null) Text(stringResource(errorTitulo))
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text(stringResource(R.string.campo_descripcion)) },
            minLines = 3,
            enabled = !vm.guardando,
            modifier = Modifier.fillMaxWidth()
        )

        Box {
            OutlinedButton(onClick = { menuAbierto = true }, enabled = !vm.guardando) {
                Text(
                    stringResource(
                        R.string.campo_prioridad,
                        stringResource((prioridad ?: Prioridad.MEDIA).etiqueta)
                    )
                )
            }
            DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                Prioridad.entries.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(stringResource(opcion.etiqueta)) },
                        onClick = {
                            prioridadNombre = opcion.name
                            menuAbierto = false
                        }
                    )
                }
            }
        }
        if (intento && errorPrioridad != null) {
            Text(
                text = stringResource(errorPrioridad),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = alVolver) {
                Text(stringResource(R.string.cancelar))
            }
            Button(
                enabled = !vm.guardando, // evita registros duplicados por pulsaciones repetidas
                onClick = {
                    intento = true
                    if (errorTitulo == null && errorPrioridad == null && prioridad != null) {
                        vm.guardar(original, titulo, descripcion, prioridad, alTerminar = alVolver)
                    }
                }
            ) {
                Text(stringResource(R.string.guardar))
            }
        }
    }
}
