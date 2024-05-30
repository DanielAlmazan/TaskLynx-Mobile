package edu.tasklynx.tasklynxmobile.ui.login

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
import kotlinx.coroutines.launch

class LoginActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

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

    private fun getDataFromFields(): Pair<String, String> {
        val employeeId = binding.tiedIdField.text.toString()
        val password = binding.tiedPasswordField.text.toString()
        return Pair(employeeId, password)
    }

    private fun validateFields(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            return false
        }
        return true
    }

    private fun login() {
        val (employeeId, password) = getDataFromFields()
        if (validateFields(employeeId, password)) {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("employeeID", employeeId)
            }
            Log.d("LoginActivity", "EmployeeID: $employeeId")
            startActivity(intent)
            finish()
            //TODO Controlar aquí que el trabajador existe y coincide con la pass. Si no, la app revienta.
        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

}