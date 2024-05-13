package edu.tasklynx.tasklynxmobile.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.tasklynx.tasklynxmobile.data.Repository
import edu.tasklynx.tasklynxmobile.models.Trabajador
import edu.tasklynx.tasklynxmobile.models.Trabajo
import kotlinx.coroutines.launch

class MainViewModel (val repository: Repository, trabajador: Trabajador): ViewModel() {
    private var _currentPendingTasks = repository.fetchPendingTasksByEmployeeId(trabajador.idTrabajador)
        val currentPendingTasks
            get() = _currentPendingTasks

    private var _currentCompletedTasks = repository.fetchCompletedTasksByEmployeeId(trabajador.idTrabajador)
        val currentCompletedTasks
            get() = _currentCompletedTasks

    private var _currentPendingTasksOrderedByPriority = repository.fetchPendingTasksByEmployeeIdOrderedByPriority(trabajador.idTrabajador)
        val currentPendingTasksOrderedByPriority
            get() = _currentPendingTasksOrderedByPriority

    fun finishTask(trabajo: Trabajo) = viewModelScope.launch {
        repository.finishTask(trabajo.codTrabajo)
    }
}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val repository: Repository,
    private val trabajador: Trabajador
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository, trabajador) as T
    }
}