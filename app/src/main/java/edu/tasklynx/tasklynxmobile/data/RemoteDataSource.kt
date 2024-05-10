package edu.tasklynx.tasklynxmobile.data

import edu.tasklynx.tasklynxmobile.models.Trabajador
import edu.tasklynx.tasklynxmobile.models.Trabajo

class RemoteDataSource {
    private val api = TasklynxAPI.getRetrofit2Api()

    suspend fun getEmployeeByEmailAndPass(email: String, pass: String): Trabajador {
        return api.getEmployeeByEmailAndPass(email, pass)
    }

    suspend fun getPendingTasksByEmployeeId(id: String): List<Trabajo> {
        return api.getPendingJobsByEmployeeId(id)
    }

    suspend fun getCompletedTasksByEmployeeId(id: String): List<Trabajo> {
        return api.getCompletedJobsByEmployeeId(id)
    }

    suspend fun getPendingTasksByEmployeeIdOrderedByPriority(id: String): List<Trabajo> {
        return api.getPendingJobsOrderedByPriority(id)
    }

    suspend fun getPendingTasksByEmployeeIdAndPriority(id: String, prioridad: Int): List<Trabajo> {
        return api.getPendingJobsByPriority(id, prioridad)
    }

    fun finishTask(id: String) {
        return api.finishTask(id)
    }
}