package es.centro.gestortareas.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

private enum class Destino { LISTA, FORMULARIO, EJEMPLO }

/** Navegación sencilla basada en estado dentro de una única actividad. */
@Composable
fun GestorTareasApp() {
    val tareasVm: TareasViewModel = viewModel()
    val ejemploVm: EjemploViewModel = viewModel()

    var destino by rememberSaveable { mutableStateOf(Destino.LISTA) }
    var editandoId by rememberSaveable { mutableStateOf<Long?>(null) }

    BackHandler(enabled = destino != Destino.LISTA) { destino = Destino.LISTA }

    when (destino) {
        Destino.LISTA -> ListaTareasScreen(
            vm = tareasVm,
            alCrear = {
                editandoId = null
                destino = Destino.FORMULARIO
            },
            alEditar = { id ->
                editandoId = id
                destino = Destino.FORMULARIO
            },
            alAbrirEjemplo = { destino = Destino.EJEMPLO }
        )

        Destino.FORMULARIO -> FormularioTareaScreen(
            vm = tareasVm,
            idEdicion = editandoId,
            alVolver = { destino = Destino.LISTA }
        )

        Destino.EJEMPLO -> TareasEjemploScreen(
            vm = ejemploVm,
            alVolver = { destino = Destino.LISTA }
        )
    }
}

/**
 * Centra el contenido y limita su ancho para que la interfaz se adapte tanto a una pantalla
 * compacta (ocupa todo el ancho) como a una grande (columna centrada y legible).
 */
@Composable
fun ContenidoAdaptable(
    padding: androidx.compose.foundation.layout.PaddingValues,
    anchoMaximo: Dp = 700.dp,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = anchoMaximo)
                .fillMaxSize()
                .padding(16.dp),
            content = contenido
        )
    }
}
