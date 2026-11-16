package es.centro.gestortareas.data.remote

import androidx.annotation.StringRes
import com.google.gson.JsonParseException
import es.centro.gestortareas.R
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CancellationException
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Estados de la pantalla «Tareas de ejemplo». */
sealed interface EstadoCarga {
    data object Inicial : EstadoCarga
    data object Cargando : EstadoCarga
    data class Datos(val tareas: List<TareaRemota>) : EstadoCarga
    data class Error(@StringRes val mensaje: Int, val codigo: Int? = null) : EstadoCarga
}

object ServicioTareas {
    private val cliente = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: ApiTareas = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .client(cliente)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiTareas::class.java)
}

/** Realiza la consulta y traduce el resultado (o el fallo) a un estado de interfaz. */
suspend fun cargarTareasRemotas(api: ApiTareas): EstadoCarga = try {
    EstadoCarga.Datos(api.listar(usuario = 1))
} catch (e: CancellationException) {
    throw e
} catch (e: HttpException) {
    EstadoCarga.Error(R.string.error_http, e.code())
} catch (e: JsonParseException) {
    EstadoCarga.Error(R.string.error_datos)
} catch (e: IOException) {
    EstadoCarga.Error(R.string.error_conexion)
}
