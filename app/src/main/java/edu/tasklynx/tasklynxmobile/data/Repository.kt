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

    fun fetchPendingTasksByEmployeeId(id: String): Flow<List<Trabajo>> {
        return flow {
            val trabajoListResponse: TrabajoListResponse
            var tasks = emptyList<Trabajo>()

            try {
                trabajoListResponse = ds.getPendingTasksByEmployeeId(id)
                tasks = trabajoListResponse.result
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching tasks from the API: ${e.message}")
            } finally {
                emit(tasks)
            }
        }
    }

    fun fetchTaskById(id: String): Flow<Trabajo?> {
        return flow {
            val trabajoSingleResponse: TrabajoSingleResponse
            var trabajo: Trabajo? = null

            try {
                trabajoSingleResponse = ds.getTaskById(id)
                trabajo = trabajoSingleResponse.result
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching task from the API: ${e.message}")
            } finally {
                emit(trabajo)
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

    fun fetchPendingTasksByEmployeeIdAndPriority(id: String, prioridad: Int): Flow<List<Trabajo>> {
        return flow {
            val trabajoListResponse: TrabajoListResponse
            var tasks = emptyList<Trabajo>()

            try {
                trabajoListResponse = ds.getPendingTasksByEmployeeIdAndPriority(id, prioridad)
                tasks = trabajoListResponse.result
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching tasks from the API: ${e.message}")
            } finally {
                emit(tasks)
            }
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