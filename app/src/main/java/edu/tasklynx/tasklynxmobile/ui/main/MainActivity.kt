package edu.tasklynx.tasklynxmobile.ui.main

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import edu.tasklynx.tasklynxmobile.R
import edu.tasklynx.tasklynxmobile.TaskLynxApplication
import edu.tasklynx.tasklynxmobile.adapters.TrabajoAdapter
import edu.tasklynx.tasklynxmobile.data.TaskLynxDataSource
import edu.tasklynx.tasklynxmobile.data.Repository
import edu.tasklynx.tasklynxmobile.databinding.ActivityMainBinding
import edu.tasklynx.tasklynxmobile.ui.trabajo.TrabajoDetailActivity
import edu.tasklynx.tasklynxmobile.utils.checkConnection
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var ascendingOrder = true
    private var viewingPending = true

    private lateinit var employeeId: String
    private lateinit var adapter: TrabajoAdapter
    private var priority: Int = 0

    private val vm: MainViewModel by viewModels {
        val db = (application as TaskLynxApplication).tasksDB
        val ds = TaskLynxDataSource(db.trabajoDao())
        MainViewModelFactory(Repository(ds), employeeId, priority)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        employeeId = intent.getStringExtra("employeeID").toString()

        if (employeeId.isEmpty()) {
            Log.e("MainActivity", "No employee data provided")
            return
        }

        adapter = TrabajoAdapter(
            onClickTrabajo = { idTask, adapterPosition ->
                TrabajoDetailActivity.navigate(this@MainActivity, idTask)
            }
        )

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
        menuInflater.inflate(R.menu.top_menu, binding.mToolbar.menu)

        getAndSortPendingTasks()
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
                    adapter.submitList(emptyList())
                    getTaskFilteredByPriority()
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
                } else -> {
                    Toast.makeText(this, getString(R.string.txt_noConnection), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getAndSortPendingTasks() {
        //TODO Aplicar un color a cada tarea según la prioridad
        if (checkConnection(this)) {
            lifecycleScope.launch {
                vm.currentPendingTasks.collect { tasks ->
                    adapter.submitList(
                        if (ascendingOrder) {
                            tasks.sortedBy { it.prioridad }
                        } else {
                            tasks.sortedByDescending { it.prioridad }
                        }
                    )
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
                    adapter.submitList(
                        if (ascendingOrder) {
                            tasks.sortedBy { it.prioridad }
                        } else {
                            tasks.sortedByDescending { it.prioridad }
                        }
                    )
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.txt_noConnection), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getTaskFilteredByPriority() {
        if (checkConnection(this)) {
            lifecycleScope.launch {

                val dialogView = EditText(this@MainActivity)

                val dialog = AlertDialog.Builder(this@MainActivity)
                    .setTitle("Insert the priority to filter the tasks")
                    .setView(dialogView)
                    .setPositiveButton("OK") { dialog, _ ->
                        val priorityToFilter = dialogView.text.toString().toInt()
                        dialog.dismiss()
                        priority = priorityToFilter

                        adapter.submitList(emptyList())

                        lifecycleScope.launch {
                            vm.filterTasksByPriority(employeeId, priorityToFilter)
                            vm.currentPendingTasksFilteredByPriority.value?.collect { tasks ->
                                adapter.submitList(tasks)
                            }
                        }
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()

                dialog.show()
            }
        } else {
            Toast.makeText(this, getString(R.string.txt_noConnection), Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout() {
        // TODO Implementar lógica para cerrar sesión
    }
}
