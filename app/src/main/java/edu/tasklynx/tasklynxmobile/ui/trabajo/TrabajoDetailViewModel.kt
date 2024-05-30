package edu.tasklynx.tasklynxmobile.ui.trabajo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.tasklynx.tasklynxmobile.data.Repository
import edu.tasklynx.tasklynxmobile.models.Trabajo

class TrabajoDetailViewModel (
    val repository: Repository, idTrabajo: String
): ViewModel() {
//    private val _trabajo = repository.fetchTaskById(idTrabajo)
//    val trabajo
//        get() = _trabajo

    fun finishTask(id: String, finishDate: String, timeSpent: Int) {
        repository.finishTask(id, finishDate, timeSpent)
        Log.d("TrabajoDetailViewModel", "Finishing task with ID: $id")
    }

    suspend fun insertTask(trabajo: Trabajo) {
        repository.insertTask(trabajo)
    }

    @Suppress("UNCHECKED_CAST")
    class TrabajoDetailViewModelFactory(
        private val repository: Repository,
        private val idTrabajo: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TrabajoDetailViewModel(repository, idTrabajo) as T
        }
    }
}