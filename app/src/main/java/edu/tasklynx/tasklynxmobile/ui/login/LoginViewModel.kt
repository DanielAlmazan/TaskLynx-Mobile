package edu.tasklynx.tasklynxmobile.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.tasklynx.tasklynxmobile.data.Repository

class LoginViewModel (val repository: Repository): ViewModel() {
}

@Suppress("UNCHECKED_CAST")
class LoginViewModelFactory(
    private val repository: Repository,
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}