package es.centro.gestortareas.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import es.centro.gestortareas.model.Prioridad
import es.centro.gestortareas.model.Tarea

@Entity(tableName = "tareas")
data class TareaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val descripcion: String,
    val prioridad: String,
    val completada: Boolean = false
)

fun TareaEntity.aModelo(): Tarea = Tarea(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    prioridad = Prioridad.desdeNombre(prioridad) ?: Prioridad.MEDIA,
    completada = completada
)

fun Tarea.aEntidad(): TareaEntity = TareaEntity(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    prioridad = prioridad.name,
    completada = completada
)
