package edu.tasklynx.tasklynxmobile.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.tasklynx.tasklynxmobile.data.Repository
import edu.tasklynx.tasklynxmobile.models.Trabajador
import edu.tasklynx.tasklynxmobile.models.Trabajo
import kotlinx.coroutines.launch

class MainViewModel (val repository: Repository, employeeId: String): ViewModel() {
    private var _currentPendingTasks = repository.fetchPendingTasksByEmployeeId(employeeId)
        val currentPendingTasks
            get() = _currentPendingTasks

    private var _currentCompletedTasks = repository.fetchCompletedTasksByEmployeeId(employeeId)
        val currentCompletedTasks
            get() = _currentCompletedTasks

    fun finishTask(trabajo: Trabajo) = viewModelScope.launch {
        repository.finishTask(trabajo.codTrabajo)
    }
}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val repository: Repository,
    private val employeeId: String
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository, employeeId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}