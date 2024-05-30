package edu.tasklynx.tasklynxmobile.ui.trabajo

import android.app.Activity
import android.app.ActivityOptions
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import edu.tasklynx.tasklynxmobile.R
import edu.tasklynx.tasklynxmobile.TaskLynxApplication
import edu.tasklynx.tasklynxmobile.data.Repository
import edu.tasklynx.tasklynxmobile.data.TaskLynxDataSource
import edu.tasklynx.tasklynxmobile.databinding.FinishTaskLayoutBinding
import edu.tasklynx.tasklynxmobile.databinding.TrabajoDetailBinding
import edu.tasklynx.tasklynxmobile.models.Trabajo
import edu.tasklynx.tasklynxmobile.ui.login.LoginActivity
import edu.tasklynx.tasklynxmobile.utils.EMPLOYEE_ID_TAG
import edu.tasklynx.tasklynxmobile.utils.EMPLOYEE_PASS_TAG
import edu.tasklynx.tasklynxmobile.utils.TASK_FINISHED
import edu.tasklynx.tasklynxmobile.utils.checkConnection
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TrabajoDetailActivity : AppCompatActivity() {
    private lateinit var binding: TrabajoDetailBinding
    private lateinit var task: Trabajo
    private val TAG = TrabajoDetailActivity::class.java.simpleName
    private var deleteTask: Boolean = false

    companion object {
        const val TASK = "TASK"

        fun navigate(activity: Activity, task: Trabajo): Intent {
            val intent = Intent(activity, TrabajoDetailActivity::class.java).apply {
                putExtra(TASK, task)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

            return intent
        }
    }

    private val vm: TrabajoDetailViewModel by viewModels {
        val db = (application as TaskLynxApplication).tasksDB
        val dataSource = TaskLynxDataSource(db.trabajoDao())
        val repository = Repository(dataSource)
        TrabajoDetailViewModel.TrabajoDetailViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TrabajoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        task = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra(TASK, Trabajo::class.java)!!
        else intent.getParcelableExtra(TASK)!!

        binding.mToolbar.navigationIcon =
            AppCompatResources.getDrawable(this, R.drawable.ic_arrow_back)

        binding.mToolbar.setNavigationOnClickListener {
            backToMainActivity()
        }

        binding.tvTaskID.text = task.codTrabajo
        binding.tvSpeciality.text = task.categoria
        binding.tvDescription.text = task.descripcion
        binding.tvStartingDate.text = task.fecIni
        binding.tvPriority.text = task.prioridad.toString()

        if (task.fecFin.isNullOrBlank()) {
            binding.tvEndingDate.visibility = View.INVISIBLE
        } else {
            binding.tvEndingDate.text = task.fecFin
        }

        if (task.tiempo == null) {
            binding.tvTime.visibility = View.INVISIBLE
        } else {
            binding.tvTime.text = task.tiempo.toString()
        }

        if (task.tiempo != null && !task.fecFin.isNullOrBlank()) {
            binding.finishBtn.visibility = View.INVISIBLE
        }

        binding.mToolbar.setTitle(task.codTrabajo)

        binding.finishBtn.setOnClickListener {
            showModal()
        }
    }

    private fun showModal() {
        val dialogView = FinishTaskLayoutBinding.inflate(layoutInflater)

        dialogView.btnDatePicker.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { dp, y, m, d ->
                cal.set(Calendar.YEAR, y)
                cal.set(Calendar.MONTH, m + 1) // 0/>Enero, 1/>Febrero, etc.
                cal.set(Calendar.DAY_OF_MONTH, d)

                dialogView.tvDate.visibility = View.VISIBLE
                dialogView.tvDate.text = getString(R.string.txt_date_selected,
                    "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}-${cal.get(Calendar.DAY_OF_MONTH)}")
            }

            DatePickerDialog(
                this@TrabajoDetailActivity,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val dialog = MaterialAlertDialogBuilder(this@TrabajoDetailActivity).apply {
            setView(dialogView.root)

            setPositiveButton(android.R.string.ok, null)

            setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        }.create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (dialogView.tvDate.visibility == View.INVISIBLE || dialogView.tieHours.text.isNullOrEmpty()) {
                    dialogView.txtError.visibility = View.VISIBLE
                } else {
                    dialogView.txtError.visibility = View.INVISIBLE
                    if (checkConnection(this@TrabajoDetailActivity)) {
                        val timeSpent = dialogView.tieHours.text.toString().toDouble()
                        val finishDate = dialogView.tvDate.text.split(" ")[2]

                        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-M-d")
                        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

                        val date = LocalDate.parse(finishDate, inputFormatter)
                        val formattedDate = date.format(outputFormatter)

                        Log.i(TAG, "time: ${timeSpent} - date: ${formattedDate}")

                        task.fecFin = formattedDate
                        task.tiempo = timeSpent

                        val taskFinished = vm.finishTask(task.codTrabajo, formattedDate, timeSpent)
                        if (taskFinished != null) {
                            Log.i(TAG, "Trabajo finalizado ${taskFinished.codTrabajo}")
                            deleteTask = true

                            runBlocking {
                                vm.insertEmployee(taskFinished.idTrabajador!!)
                                delay(100)
                                vm.insertTask(taskFinished)
                            }

                            binding.tvTime.text = taskFinished.tiempo.toString()
                            binding.tvEndingDate.text = taskFinished.fecFin
                            binding.tvTime.visibility = View.VISIBLE
                            binding.tvEndingDate.visibility = View.VISIBLE
                            binding.finishBtn.visibility = View.INVISIBLE
                        } else {
                            // TODO: Cambiar string
                            Toast.makeText(
                                this@TrabajoDetailActivity,
                                "Error finalizando trabajo",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        dialog.dismiss()
                    } else
                        Toast.makeText(
                            this@TrabajoDetailActivity,
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
    private fun backToMainActivity() {
        val intentResult: Intent = Intent().apply {
            putExtra(TASK_FINISHED, deleteTask)
        }
        setResult(Activity.RESULT_OK, intentResult)
        finish()
    }
}