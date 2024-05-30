package edu.tasklynx.tasklynxmobile.data

import TrabajoListResponse
import TrabajoSingleResponse
import edu.tasklynx.tasklynxmobile.models.Trabajador
import edu.tasklynx.tasklynxmobile.models.Trabajo
import edu.tasklynx.tasklynxmobile.models.TrabajoRoom

class TaskLynxDataSource(private val db: TrabajoDao) {
    private val api = TasklynxAPI.getRetrofit2Api()

    //API Actions
    suspend fun getPendingTasksByLoggedEmployee(id: String, password: String): TrabajoListResponse {
        return api.getPendingTasksByLoggedEmployee(id, password)
    }

    suspend fun finishTask(id: String, finishDate: String, timeSpent: Double): TrabajoSingleResponse {
        return api.finishTask(id, finishDate, timeSpent)
    }

    //Room Actions
    suspend fun insertTask(task: TrabajoRoom) = db.insertTask(task)
    suspend fun insertEmployee(employee: Trabajador) = db.insertEmployee(employee)
    suspend fun getTasks(): MutableList<Trabajo>  {
        val tasks: MutableList<Trabajo> = mutableListOf()

        db.getTasks().forEach { task ->
            tasks.add(Trabajo(
                task.categoria,
                task.codTrabajo,
                task.descripcion,
                task.fecFin,
                task.fecIni,
                null,
                task.prioridad,
                task.tiempo
            ))
        }

        return tasks
    }
}