package edu.tasklynx.tasklynxmobile.data

import TrabajoListResponse
import TrabajoSingleResponse
import edu.tasklynx.tasklynxmobile.models.Trabajo

class TaskLynxDataSource(val db: TrabajoDao) {
    private val api = TasklynxAPI.getRetrofit2Api()


    //API Actions
    suspend fun getPendingTasksByEmployeeId(id: String): TrabajoListResponse {
        return api.getPendingTasksByEmployeeId(id)
    }

    suspend fun getTaskById(id: String): TrabajoSingleResponse {
        return api.getTaskById(id)
    }

    fun finishTask(id: String, finishDate: String, timeSpent: Int): TrabajoSingleResponse {
        return api.finishTask(id, finishDate, timeSpent)
    }

    suspend fun getPendingTasksByEmployeeIdAndPriority(id: String, prioridad: Int): TrabajoListResponse {
        return api.getPendingJobsByPriority(id, prioridad)
    }

    //Room Actions

    suspend fun insertTask(trabajo: Trabajo) {
        db.insertTask(trabajo)
    }

    suspend fun getTasks(): List<Trabajo> {
        return db.getTasks()
    }

}