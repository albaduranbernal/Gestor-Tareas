package es.centro.gestortareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import es.centro.gestortareas.ui.GestorTareasApp
import es.centro.gestortareas.ui.theme.GestorTareasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GestorTareasTheme {
                GestorTareasApp()
            }
        }
    }
}
