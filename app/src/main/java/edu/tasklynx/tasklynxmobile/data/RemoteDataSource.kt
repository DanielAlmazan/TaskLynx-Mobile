package edu.tasklynx.tasklynxmobile.data

import TrabajoResponse
import edu.tasklynx.tasklynxmobile.models.Trabajador
import edu.tasklynx.tasklynxmobile.models.Trabajo

class RemoteDataSource {
    private val api = TasklynxAPI.getRetrofit2Api()

    suspend fun getPendingTasksByEmployeeId(id: String): TrabajoResponse {
        return api.getPendingJobsByEmployeeId(id)
    }

    suspend fun getCompletedTasksByEmployeeId(id: String): TrabajoResponse {
        return api.getCompletedJobsByEmployeeId(id)
    }

    fun finishTask(id: String) {
        return api.finishTask(id)
    }
}