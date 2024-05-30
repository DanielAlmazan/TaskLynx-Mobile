package edu.tasklynx.tasklynxmobile.data

import TrabajoListResponse
import TrabajoSingleResponse
import edu.tasklynx.tasklynxmobile.models.Trabajo

class TaskLynxDataSource(val db: TrabajoDao) {
    private val api = TasklynxAPI.getRetrofit2Api()


    //API Actions
    suspend fun getPendingTasksByLoggedEmployee(id: String, password: String): TrabajoListResponse {
        return api.getPendingTasksByLoggedEmployee(id, password)
    }

    fun finishTask(id: String, finishDate: String, timeSpent: Int): TrabajoSingleResponse {
        return api.finishTask(id, finishDate, timeSpent)
    }

    //Room Actions

    suspend fun insertTask(trabajo: Trabajo) {
        db.insertTask(trabajo)
    }

    suspend fun getTasks(): List<Trabajo> {
        return db.getTasks()
    }

}