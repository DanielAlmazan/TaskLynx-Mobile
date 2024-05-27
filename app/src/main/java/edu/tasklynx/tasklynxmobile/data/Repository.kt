package edu.tasklynx.tasklynxmobile.data

import TrabajoResponse
import android.util.Log
import edu.tasklynx.tasklynxmobile.models.Trabajo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Repository(db: tasklynxDB, val ds: RemoteDataSource) {
    val TAG = Repository::class.java.simpleName
    private val localDataSource = LocalDataSource(db)

    fun fetchPendingTasksByEmployeeId(id: String): Flow<List<Trabajo>> {
        return flow {
            val trabajoResponse: TrabajoResponse
            var tasks = emptyList<Trabajo>()

            try {
                trabajoResponse = ds.getPendingTasksByEmployeeId(id)
                tasks = trabajoResponse.result
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching tasks from the API: ${e.message}")
            } finally {
                emit(tasks)
            }
        }
    }

    fun fetchCompletedTasksByEmployeeId(id: String): Flow<List<Trabajo>> {
        return flow {
            val trabajoResponse: TrabajoResponse
            var tasks = emptyList<Trabajo>()

            try {
                trabajoResponse = ds.getCompletedTasksByEmployeeId(id)
                tasks = trabajoResponse.result
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching tasks from the API: ${e.message}")
            } finally {
                emit(tasks)
            }
        }
    }

    fun finishTask(id: String) {
        ds.finishTask(id)
    }

    suspend fun insertTask(trabajo: Trabajo) {
        localDataSource.insertTask(trabajo)
    }

    suspend fun fetchTasksfromDB(): List<Trabajo> {
        return localDataSource.getTasks()
    }
}