package edu.tasklynx.tasklynxmobile.data

import TrabajoListResponse
import TrabajoSingleResponse
import android.util.Log
import edu.tasklynx.tasklynxmobile.models.Trabajo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

class Repository(val ds: TaskLynxDataSource) {
    val TAG = Repository::class.java.simpleName

    fun fetchPendingTasksByLoggedEmployee(id: String, password: String): Flow<List<Trabajo>> {
        return flow {
            val trabajoListResponse: TrabajoListResponse
            val tasks: List<Trabajo>

            try {
                trabajoListResponse = ds.getPendingTasksByLoggedEmployee(id, password)
                tasks = trabajoListResponse.result
                emit(tasks)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching tasks from the API in REPOSITORY: ${e.message}")
                throw e
            }
        }
    }

    fun finishTask(id: String, finishDate: String, timeSpent: Int): Trabajo? {
        return runBlocking {
            val trabajoSingleResponse: TrabajoSingleResponse
            var trabajo: Trabajo? = null
            try {
                trabajoSingleResponse = ds.finishTask(id, finishDate, timeSpent)
                trabajo = trabajoSingleResponse.result
                Log.d(TAG, "Task finished: $trabajo")
            } catch (e: Exception) {
                Log.e(TAG, "Error finishing task from the API: ${e.message}")
                //TODO Da error aquí.
            }
            trabajo
        }
    }

    suspend fun insertTask(trabajo: Trabajo) {
        ds.insertTask(trabajo)
    }

    fun fetchTasksfromDB(): Flow<List<Trabajo>> {
        return flow {
            emit(ds.getTasks())
        }
    }
}