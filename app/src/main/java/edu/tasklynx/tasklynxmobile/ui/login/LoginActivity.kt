package edu.tasklynx.tasklynxmobile.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import edu.tasklynx.tasklynxmobile.TaskLynxApplication
import edu.tasklynx.tasklynxmobile.data.TaskLynxDataSource
import edu.tasklynx.tasklynxmobile.data.Repository
import edu.tasklynx.tasklynxmobile.databinding.ActivityLoginBinding
import edu.tasklynx.tasklynxmobile.ui.main.MainActivity
import edu.tasklynx.tasklynxmobile.ui.main.MainViewModel
import edu.tasklynx.tasklynxmobile.ui.main.MainViewModelFactory
import edu.tasklynx.tasklynxmobile.utils.EMPLOYEE_ID_TAG
import edu.tasklynx.tasklynxmobile.utils.EMPLOYEE_PASS_TAG
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    companion object {
        /**
         * Function to start the activity
         */
        fun navigate(activity: Activity): Intent {
            val intent = Intent(activity, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            return intent
        }
    }

    private val vm: LoginViewModel by viewModels {
        val db = (application as TaskLynxApplication).tasksDB
        val ds = TaskLynxDataSource(db.trabajoDao())
        LoginViewModelFactory(Repository(ds))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        binding.button.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val id = binding.tiedIdField.text
        val password = binding.tiedPasswordField.text

        if (!id.isNullOrBlank() && !password.isNullOrBlank()) {
            Log.i(LoginActivity::class.java.simpleName, "ID: $id - PASS: $password")
            if(vm.checkLogin(id.toString(), password.toString())) {
                backToMainActivity(id.toString(), password.toString())
            } else {
                Toast.makeText(this, "Incorrect credentials", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Function to set the result of the activity and finishes it
     */
    private fun backToMainActivity(id: String, pass: String) {
        val intentResult: Intent = Intent().apply {
            putExtra(EMPLOYEE_ID_TAG, id)
            putExtra(EMPLOYEE_PASS_TAG, pass)
        }
        setResult(Activity.RESULT_OK, intentResult)
        finish()
    }
}