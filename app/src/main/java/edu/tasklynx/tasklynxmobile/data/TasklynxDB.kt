package edu.tasklynx.tasklynxmobile.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.tasklynx.tasklynxmobile.models.Trabajador
import edu.tasklynx.tasklynxmobile.models.Trabajo
import edu.tasklynx.tasklynxmobile.models.TrabajoRoom

//@TypeConverters(TrabajadorConverter::class)
@Database(entities = [TrabajoRoom::class, Trabajador::class], version = 2)
abstract class TasklynxDB: RoomDatabase() {
    abstract fun trabajoDao(): TrabajoDao
}

@Dao
interface TrabajoDao {
    @Query("SELECT * FROM trabajo")
    suspend fun getTasks(): List<TrabajoRoom>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TrabajoRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: Trabajador)
}
