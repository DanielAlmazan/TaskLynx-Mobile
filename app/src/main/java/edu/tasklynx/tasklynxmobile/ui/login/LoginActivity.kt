package edu.tasklynx.tasklynxmobile.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import edu.tasklynx.tasklynxmobile.R
import edu.tasklynx.tasklynxmobile.TaskLynxApplication
import edu.tasklynx.tasklynxmobile.data.Repository
import edu.tasklynx.tasklynxmobile.data.TaskLynxDataSource
import edu.tasklynx.tasklynxmobile.databinding.ActivityLoginBinding
import edu.tasklynx.tasklynxmobile.databinding.ChangePasswordLayoutBinding
import edu.tasklynx.tasklynxmobile.utils.EMPLOYEE_ID_TAG
import edu.tasklynx.tasklynxmobile.utils.EMPLOYEE_PASS_TAG
import edu.tasklynx.tasklynxmobile.utils.checkConnection

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val DEFAUL_PASSWORD = "tasklynx2024"

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
            if(vm.checkLogin(id.toString(), password.toString())) {
                if(password.toString() == DEFAUL_PASSWORD) {
                    showModal(id.toString())
                } else
                    backToMainActivity(id.toString(), password.toString())
            } else {
                Toast.makeText(this, "Incorrect credentials", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showModal(id: String) {
        val dialogView = ChangePasswordLayoutBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(this@LoginActivity).apply {
            setView(dialogView.root)
            setTitle(getString(R.string.txt_modal_change_password))

            setPositiveButton(android.R.string.ok, null)

            setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        }.create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val pass = dialogView.tiePassword.text
                val passConfirm = dialogView.tiePassConfirm.text
                if (pass.isNullOrBlank() || passConfirm.isNullOrEmpty() || pass.toString() != passConfirm.toString()) {
                    dialogView.txtError.visibility = View.VISIBLE
                } else {
                    dialogView.txtError.visibility = View.INVISIBLE
                    if (checkConnection(this@LoginActivity)) {

                        val passChanged = vm.changePassword(id, pass.toString())
                        if (passChanged) {
                            backToMainActivity(id, pass.toString())
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                getString(R.string.txt_error_changing_pasword),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        dialog.dismiss()
                    } else
                        Toast.makeText(
                            this@LoginActivity,
                            getString(R.string.txt_noConnection),
                            Toast.LENGTH_SHORT
                        ).show()
                }
            }
        }

        dialog.show()
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