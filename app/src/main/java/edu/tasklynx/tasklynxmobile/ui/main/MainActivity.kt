package edu.tasklynx.tasklynxmobile.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import edu.tasklynx.tasklynxmobile.R
import edu.tasklynx.tasklynxmobile.TaskLynxApplication
import edu.tasklynx.tasklynxmobile.ui.adapters.TrabajoAdapter
import edu.tasklynx.tasklynxmobile.data.Repository
import edu.tasklynx.tasklynxmobile.data.TaskLynxDataSource
import edu.tasklynx.tasklynxmobile.databinding.ActivityMainBinding
import edu.tasklynx.tasklynxmobile.models.Trabajo
import edu.tasklynx.tasklynxmobile.models.TrabajoRoom
import edu.tasklynx.tasklynxmobile.ui.login.LoginActivity
import edu.tasklynx.tasklynxmobile.ui.trabajo.TrabajoDetailActivity
import edu.tasklynx.tasklynxmobile.utils.EMPLOYEE_ID_TAG
import edu.tasklynx.tasklynxmobile.utils.EMPLOYEE_PASS_TAG
import edu.tasklynx.tasklynxmobile.utils.TASK_FINISHED
import edu.tasklynx.tasklynxmobile.utils.checkConnection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    var employeeId: String = ""
    var employeePassword: String = ""

    private var ascendingOrder = true
    private var viewingPending = true

    private lateinit var adapter: TrabajoAdapter
    private var priority: Int = 0
    private var taskPosition: Int = 0

    private var tasksList: MutableList<Trabajo> = mutableListOf()

    private val vm: MainViewModel by viewModels {
        val db = (application as TaskLynxApplication).tasksDB
        val ds = TaskLynxDataSource(db.trabajoDao())
        MainViewModelFactory(Repository(ds), employeeId, employeePassword)
    }

    private val adapterTask by lazy {
        TrabajoAdapter(
            onClickTrabajo = { task, position ->
                taskPosition = position
                taskDetailResult.launch(
                    TrabajoDetailActivity.navigate(this@MainActivity, task)
                )
            }
        )
    }

    /**
     * Function to receive the user data from LoginActivity
     */
    private val loginResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data: Intent? = result.data
        if (result.resultCode == Activity.RESULT_OK) {
            employeeId = data!!.getStringExtra(EMPLOYEE_ID_TAG)!!
            employeePassword = data.getStringExtra(EMPLOYEE_PASS_TAG)!!
            TaskLynxApplication.preferences.employeeId = employeeId
            TaskLynxApplication.preferences.employeePassword = employeePassword
            getAndSortPendingTasks()
        }
    }

    /**
     * Function to receive info from TrabajoDetailActivity
     */
    private val taskDetailResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data: Intent? = result.data
        if (result.resultCode == Activity.RESULT_OK) {
            val taskFinished = data!!.getBooleanExtra(TASK_FINISHED, false)

            if (taskFinished) {
                tasksList.removeAt(taskPosition)
                adapter.notifyItemRemoved(taskPosition)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = adapterTask

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
        menuInflater.inflate(R.menu.top_menu, binding.mToolbar.menu)

        // If there are no saved preferences, redirect to login
        if (!TaskLynxApplication.preferences.checkPreferences()) {
            loginResult.launch(
                LoginActivity.navigate(this)
            )
        } else {
            employeeId = TaskLynxApplication.preferences.employeeId
            employeePassword = TaskLynxApplication.preferences.employeePassword
            getAndSortPendingTasks()
        }
    }

    override fun onStart() {
        super.onStart()

        viewingPending = true

        binding.mToolbar.setOnMenuItemClickListener() { item ->
            when (item.itemId) {
                R.id.opSort -> {
                    adapter.submitList(emptyList())
                    ascendingOrder = !ascendingOrder
                    if (viewingPending) {
                        getAndSortPendingTasks()
                    } else {
                        getAndSortCompletedTasks()
                    }
                    true
                }

                R.id.opSpeciality -> {
                    filterListByPriority(vm.currentPendingTasks)
                    //TODO ¿Manejar también el botón para eliminar el filtro?
                    // Igualmente, al clickar en "Pending tasks" en la bottom navigation, se elimina el filtro de todas maneras.
                    true
                }

                else -> false
            }
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.opPending -> {
                    adapter.submitList(emptyList())
                    viewingPending = true
                    getAndSortPendingTasks()
                    true
                }

                R.id.opCompleted -> {
                    adapter.submitList(emptyList())
                    viewingPending = false
                    getAndSortCompletedTasks()
                    true
                }

                else -> false
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            when {
                checkConnection(this) -> {
                    adapter.submitList(emptyList())
                    if (viewingPending) {
                        getAndSortPendingTasks()
                    } else {
                        getAndSortCompletedTasks()
                    }
                    binding.swipeRefresh.isRefreshing = false
                }

                else -> {
                    Toast.makeText(this, getString(R.string.txt_noConnection), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun getAndSortPendingTasks() {
        //TODO Aplicar un color a cada tarea según la prioridad
        if (checkConnection(this)) {
            lifecycleScope.launch {
                vm.currentPendingTasks.collect { tasks ->
                    tasksList = if (ascendingOrder)
                        tasks.sortedBy { it.prioridad }.toMutableList()
                    else tasks.sortedByDescending { it.prioridad }.toMutableList()

                    adapter.submitList(tasksList)
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.txt_noConnection), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAndSortCompletedTasks() {
        if (checkConnection(this)) {
            lifecycleScope.launch {
                vm.currentCompletedTasks.collect { tasks ->
                    tasksList = if (ascendingOrder)
                        tasks.sortedBy { it.prioridad }.toMutableList()
                    else tasks.sortedByDescending { it.prioridad }.toMutableList()

                    adapter.submitList(tasksList.filter {
                        it.idTrabajador!!.idTrabajador == employeeId
                    })
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.txt_noConnection), Toast.LENGTH_SHORT).show()
        }
    }

    private fun filterListByPriority(flow: Flow<List<Trabajo>>) {
        if (checkConnection(this)) {
            val dialogView = EditText(this@MainActivity)
            val dialog = MaterialAlertDialogBuilder(this@MainActivity)
                .setTitle("Insert the priority to filter the tasks")
                .setView(dialogView)
                .setPositiveButton("OK") { dialog, _ ->
                    val priorityToFilter = dialogView.text.toString().toInt()
                    dialog.dismiss()
                    priority = priorityToFilter

                    adapter.submitList(emptyList())

                    lifecycleScope.launch {
                        flow.catch {
                            Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
                        }.collect { trabajos ->
                            tasksList =
                                trabajos.filter { t -> t.prioridad == priority }.toMutableList()
                            adapter.submitList(tasksList)
                        }
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            dialog.show()
        } else {
            Toast.makeText(this, getString(R.string.txt_noConnection), Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout() {
        // TODO Implementar lógica para cerrar sesión
    }
}
