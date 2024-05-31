package edu.tasklynx.tasklynxmobile.ui.main

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
import edu.tasklynx.tasklynxmobile.ui.login.LoginActivity
import edu.tasklynx.tasklynxmobile.ui.trabajo.TrabajoDetailActivity
import edu.tasklynx.tasklynxmobile.utils.EMPLOYEE_ID_TAG
import edu.tasklynx.tasklynxmobile.utils.EMPLOYEE_PASS_TAG
import edu.tasklynx.tasklynxmobile.utils.Preferences
import edu.tasklynx.tasklynxmobile.utils.TASK_FINISHED
import edu.tasklynx.tasklynxmobile.utils.checkConnection
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val TAG = MainActivity::class.java.simpleName

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
        MainViewModelFactory(Repository(ds))
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
            Log.i(TAG, "ID: ${employeeId} - PASS: ${employeePassword}")
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

        binding.mToolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.opSort -> {
                    ascendingOrder = !ascendingOrder
                    if (viewingPending) {
                        getAndSortPendingTasks()
                    } else {
                        getAndSortCompletedTasks()
                    }
                    true
                }

                R.id.opSpeciality -> {
                    filterListByPriority()
                    true
                }

                R.id.opLogout -> {
                    logout()
                    loginResult.launch(
                        LoginActivity.navigate(this)
                    )
                    true
                }

                else -> false
            }
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.opPending -> {
                    viewingPending = true
                    getAndSortPendingTasks()
                    true
                }

                R.id.opCompleted -> {
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
        if (checkConnection(this)) {
            lifecycleScope.launch {
                val list = vm.fetchPendingTasks(employeeId, employeePassword)
                tasksList = if (ascendingOrder)
                    list.sortedBy { it.prioridad }.toMutableList()
                else list.sortedByDescending { it.prioridad }.toMutableList()

                binding.tvNoElements.visibility = if (tasksList.isEmpty()) View.VISIBLE
                    else View.INVISIBLE
                adapter.submitList(emptyList())
                adapter.submitList(tasksList)
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

                    tasksList = tasksList.filter {
                        it.idTrabajador!!.idTrabajador == employeeId
                    }.toMutableList()

                    binding.tvNoElements.visibility = if (tasksList.isEmpty()) View.VISIBLE
                        else View.INVISIBLE

                    adapter.submitList(emptyList())
                    adapter.submitList(tasksList)
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.txt_noConnection), Toast.LENGTH_SHORT).show()
        }
    }

    private fun filterListByPriority() {
        MaterialAlertDialogBuilder(this@MainActivity).apply {
            setTitle(getString(R.string.txt_modal_select_priority))

            var selectedPos = -1
            val prioritiesArray = resources.getIntArray(R.array.array_priorities).toTypedArray()

            setSingleChoiceItems(prioritiesArray.map { p -> p.toString() }.toTypedArray(), -1) { _, which ->
                selectedPos = which
            }

            setPositiveButton(android.R.string.ok) { dialog, _ ->
                if (selectedPos != -1) {
                    val priority = prioritiesArray[selectedPos]

                    if (checkConnection(this@MainActivity)) {
                        lifecycleScope.launch {
                            runBlocking {
                                if(viewingPending)
                                   tasksList = vm.fetchPendingTasks(employeeId, employeePassword).toMutableList()
                                else
                                    vm.currentCompletedTasks.collect{
                                        tasksList = it.filter { t ->
                                            t.idTrabajador!!.idTrabajador == employeeId
                                        }.toMutableList()
                                    }
                            }
                            tasksList = tasksList.filter { t -> t.prioridad == priority }.toMutableList()

                            binding.tvNoElements.visibility = if (tasksList.isEmpty()) View.VISIBLE
                            else View.INVISIBLE

                            adapter.submitList(emptyList())
                            adapter.submitList(tasksList)
                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.txt_noConnection),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                dialog.dismiss()
            }

            setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        }.show()
    }

    private fun logout() {
        Preferences(this).deletePreferences()
    }
}
