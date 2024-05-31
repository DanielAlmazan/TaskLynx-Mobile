package edu.tasklynx.tasklynxmobile.data

import android.util.Log
import edu.tasklynx.tasklynxmobile.models.Trabajador
import edu.tasklynx.tasklynxmobile.models.Trabajo
import edu.tasklynx.tasklynxmobile.models.TrabajoRoom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Repository(private val ds: TaskLynxDataSource) {
    private val TAG = Repository::class.java.simpleName

    // API
    fun fetchPendingTasksByLoggedEmployee(id: String, password: String): Flow<List<Trabajo>> {
        return flow {
            try {
                val trabajoListResponse = ds.getPendingTasksByLoggedEmployee(id, password)
                Log.i(TAG, "Fetch ID: ${id} - PASS: ${password}")
                emit(trabajoListResponse.result)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching tasks from the API: ${e.message}")
                throw e
            }
        }
    }

    fun finishTask(id: String, finishDate: String, timeSpent: Double) : Flow<Trabajo> {
        return flow {
            try {
                val trabajoSingleResponse = ds.finishTask(id, finishDate, timeSpent)
                emit(trabajoSingleResponse.result)
            } catch (e: Exception) {
                Log.e(TAG, "Error finishing task from the API: ${e.message}")
                throw e
            }
        }
    }

    fun changePassword(id: String, password: String) : Flow<Boolean> {
        return flow {
            try {
                val trabajoSingleResponse = ds.changePassword(id, password)
                emit(trabajoSingleResponse.error)
            } catch (e: Exception) {
                Log.e(TAG, "Error changing password from the API: ${e.message}")
                throw e
            }
        }
    }

    // Room
    suspend fun insertTask(task: TrabajoRoom) = ds.insertTask(task)
    suspend fun insertEmployee(employee: Trabajador) = ds.insertEmployee(employee)

    fun fetchTasksFromDB(): Flow<MutableList<Trabajo>> {
        return flow {
            emit(ds.getTasks())
        }
    }
}