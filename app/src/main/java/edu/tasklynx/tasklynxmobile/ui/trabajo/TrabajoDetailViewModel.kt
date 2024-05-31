package edu.tasklynx.tasklynxmobile.ui.trabajo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.tasklynx.tasklynxmobile.data.Repository
import edu.tasklynx.tasklynxmobile.models.Trabajador
import edu.tasklynx.tasklynxmobile.models.Trabajo
import edu.tasklynx.tasklynxmobile.models.TrabajoRoom
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TrabajoDetailViewModel(
    private val repository: Repository
) : ViewModel() {
    private val TAG = TrabajoDetailViewModel::class.java.simpleName
    fun finishTask(id: String, finishDate: String, timeSpent: Double) = runBlocking {
        var task: Trabajo? = null
        repository.finishTask(id, finishDate, timeSpent).catch {
            Log.e(TAG, it.message ?: "Error finishing task")
        }.collect {
            task = it
        }

        return@runBlocking task
    }

    fun insertTask(task: Trabajo) {
        val taskRoom = TrabajoRoom(
            categoria = task.categoria,
            codTrabajo = task.codTrabajo,
            descripcion = task.descripcion,
            fecFin = task.fecFin,
            fecIni = task.fecIni,
            idTrabajador = task.idTrabajador?.idTrabajador,
            prioridad = task.prioridad,
            tiempo = task.tiempo
        )

        viewModelScope.launch {
            repository.insertTask(taskRoom)
        }
    }

    fun insertEmployee(employee: Trabajador) {
        viewModelScope.launch {
            repository.insertEmployee(employee)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class TrabajoDetailViewModelFactory(
        private val repository: Repository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TrabajoDetailViewModel(repository) as T
        }
    }
}