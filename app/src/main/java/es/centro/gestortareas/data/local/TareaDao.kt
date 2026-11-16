package es.centro.gestortareas.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {
    @Query("SELECT * FROM tareas ORDER BY completada ASC, id DESC")
    fun observar(): Flow<List<TareaEntity>>

    @Insert
    suspend fun insertar(tarea: TareaEntity): Long

    @Update
    suspend fun actualizar(tarea: TareaEntity): Int

    @Delete
    suspend fun eliminar(tarea: TareaEntity): Int
}
