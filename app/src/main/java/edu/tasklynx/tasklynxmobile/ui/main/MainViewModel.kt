package edu.tasklynx.tasklynxmobile.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.tasklynx.tasklynxmobile.data.Repository
import edu.tasklynx.tasklynxmobile.models.Trabajo
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainViewModel(
    private val repository: Repository
) : ViewModel() {
    private val TAG = MainViewModel::class.java.simpleName

    private var _currentCompletedTasks = repository.fetchTasksFromDB()
    val currentCompletedTasks
        get() = _currentCompletedTasks

    suspend fun fetchPendingTasks(employeeId: String, employeePassword: String) = runBlocking {
        var pendingTasks: List<Trabajo> = mutableListOf()
        repository.fetchPendingTasksByLoggedEmployee(employeeId, employeePassword).catch {
            Log.e(TAG, "Error fetching pending tasks")
        }.collect{
            pendingTasks = it
        }
        return@runBlocking pendingTasks
    }
}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val repository: Repository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            Log.i(MainViewModelFactory::class.java.simpleName, "Llamando al VM")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}