package edu.tasklynx.tasklynxmobile.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import edu.tasklynx.tasklynxmobile.models.Trabajador
import edu.tasklynx.tasklynxmobile.models.Trabajo

@Database(entities = [Trabajo::class, Trabajador::class], version = 1)
abstract class tasklynxDB: RoomDatabase() {
    abstract fun trabajoDao(): TrabajoDao
}

@Dao
interface TrabajoDao {
    @Query("SELECT * FROM trabajo")
    suspend fun getTasks(): List<Trabajo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(trabajo: Trabajo)
}
