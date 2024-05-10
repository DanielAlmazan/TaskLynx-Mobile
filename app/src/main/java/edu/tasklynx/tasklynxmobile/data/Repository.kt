package edu.tasklynx.tasklynxmobile.data

import android.util.Log
import edu.tasklynx.tasklynxmobile.models.Trabajador
import edu.tasklynx.tasklynxmobile.models.Trabajo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Repository(db: tasklynxDB, val ds: RemoteDataSource) {
    val TAG = Repository::class.java.simpleName
    private val localDataSource = LocalDataSource(db)

    suspend fun getEmployeeByEmailAndPass(email: String, pass: String): Trabajador {
        return ds.getEmployeeByEmailAndPass(email, pass)
    }

    suspend fun fetchPendingTasksByEmployeeId(id: String): Flow<List<Trabajo>> {
        return flow {
            var resultApi = emptyList<Trabajo>()

            try {
                resultApi = ds.getPendingTasksByEmployeeId(id)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching tasks from the API: ${e.message}")
            } finally {
                emit(resultApi)
            }
        }
    }

    suspend fun fetchCompletedTasksByEmployeeId(id: String): Flow<List<Trabajo>> {
        return flow {
            var resultApi = emptyList<Trabajo>()

            try {
                resultApi = ds.getCompletedTasksByEmployeeId(id)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching tasks from the API: ${e.message}")
            } finally {
                emit(resultApi)
            }
        }
    }

    suspend fun fetchPendingTasksByEmployeeIdOrderedByPriority(id: String): Flow<List<Trabajo>> {
        return flow {
            var resultApi = emptyList<Trabajo>()

            try {
                resultApi = ds.getPendingTasksByEmployeeIdOrderedByPriority(id)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching tasks from the API: ${e.message}")
            } finally {
                emit(resultApi)
            }
        }
    }

    suspend fun fetchPendingTasksByEmployeeIdAndPriority(id: String, prioridad: Int): Flow<List<Trabajo>> {
        return flow {
            var resultApi = emptyList<Trabajo>()

            try {
                resultApi = ds.getPendingTasksByEmployeeIdAndPriority(id, prioridad)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching tasks from the API: ${e.message}")
            } finally {
                emit(resultApi)
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