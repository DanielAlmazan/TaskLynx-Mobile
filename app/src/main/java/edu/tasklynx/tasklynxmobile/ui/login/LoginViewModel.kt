package edu.tasklynx.tasklynxmobile.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.tasklynx.tasklynxmobile.data.Repository
import edu.tasklynx.tasklynxmobile.ui.main.MainViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.runBlocking

class LoginViewModel(
    private val repository: Repository
) : ViewModel() {
    fun checkLogin(id: String, password: String) = runBlocking {
        var result = false
        repository.fetchPendingTasksByLoggedEmployee(id, password).catch {
            Log.e("ERROR", "Credenciales incorrectas")
        }.collect { result = true }

        return@runBlocking result
    }

    fun changePassword(id: String, password: String) = runBlocking {
        var result = false
        repository.changePassword(id, password).catch {
            Log.e("ERROR", "Error cambiando la contraseña")
        }.collect { result = true }

        return@runBlocking result
    }
}

@Suppress("UNCHECKED_CAST")
class LoginViewModelFactory(
    private val repository: Repository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}