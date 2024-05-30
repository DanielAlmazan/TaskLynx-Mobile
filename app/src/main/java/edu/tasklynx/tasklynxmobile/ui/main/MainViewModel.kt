package edu.tasklynx.tasklynxmobile.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.tasklynx.tasklynxmobile.data.Repository

class MainViewModel(
    repository: Repository,
    employeeId: String,
    employeePassword: String
) : ViewModel() {
    private var _currentPendingTasks =
        repository.fetchPendingTasksByLoggedEmployee(employeeId, employeePassword)
    val currentPendingTasks
        get() = _currentPendingTasks

    private var _currentCompletedTasks = repository.fetchTasksFromDB()
    val currentCompletedTasks
        get() = _currentCompletedTasks
}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val repository: Repository,
    private val employeeId: String,
    private val employeePassword: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            Log.i(MainViewModelFactory::class.java.simpleName, "Llamando al VM")
            return MainViewModel(repository, employeeId, employeePassword) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}