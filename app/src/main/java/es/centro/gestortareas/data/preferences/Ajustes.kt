package es.centro.gestortareas.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

// Se declara una sola vez, fuera de cualquier clase o función.
val Context.ajustes by preferencesDataStore(name = "ajustes")

private val SOLO_PENDIENTES = booleanPreferencesKey("solo_pendientes")

/** Emite la preferencia «Mostrar solo pendientes». Valor inicial: false (mostrar todas). */
fun observarFiltro(context: Context): Flow<Boolean> = context.ajustes.data
    .catch { error ->
        if (error is IOException) emit(emptyPreferences()) else throw error
    }
    .map { preferencias -> preferencias[SOLO_PENDIENTES] ?: false }

suspend fun guardarFiltro(context: Context, valor: Boolean) {
    context.ajustes.edit { preferencias ->
        preferencias[SOLO_PENDIENTES] = valor
    }
}
