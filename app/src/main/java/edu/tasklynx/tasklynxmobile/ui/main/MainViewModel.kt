package edu.tasklynx.tasklynxmobile.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.tasklynx.tasklynxmobile.data.Repository
import edu.tasklynx.tasklynxmobile.models.Trabajo
import kotlinx.coroutines.flow.Flow

class MainViewModel (val repository: Repository, employeeId: String, priority: Int): ViewModel() {
    private var _currentPendingTasks = repository.fetchPendingTasksByEmployeeId(employeeId)
        val currentPendingTasks
            get() = _currentPendingTasks

    private var _currentCompletedTasks = repository.fetchTasksfromDB()
        val currentCompletedTasks
            get() = _currentCompletedTasks

    private val _currentPendingTasksFilteredByPriority = MutableLiveData<Flow<List<Trabajo>>>()
        val currentPendingTasksFilteredByPriority: LiveData<Flow<List<Trabajo>>>
            get() = _currentPendingTasksFilteredByPriority

    fun filterTasksByPriority(employeeId: String, priority: Int) {
        _currentPendingTasksFilteredByPriority.value = repository.fetchPendingTasksByEmployeeIdAndPriority(employeeId, priority)
    }

}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val repository: Repository,
    private val employeeId: String,
    private val priority: Int
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository, employeeId, priority) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}