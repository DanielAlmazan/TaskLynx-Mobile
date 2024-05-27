package edu.tasklynx.tasklynxmobile.ui.main

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import edu.tasklynx.tasklynxmobile.R
import edu.tasklynx.tasklynxmobile.TaskLynxApplication
import edu.tasklynx.tasklynxmobile.adapters.TrabajoAdapter
import edu.tasklynx.tasklynxmobile.data.RemoteDataSource
import edu.tasklynx.tasklynxmobile.data.Repository
import edu.tasklynx.tasklynxmobile.databinding.ActivityMainBinding
import edu.tasklynx.tasklynxmobile.utils.checkConnection
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var ascendingOrder = true
    private var viewingPending = true

    private lateinit var employeeId: String
    private lateinit var adapter: TrabajoAdapter

    private val vm: MainViewModel by viewModels {
        val db = (application as TaskLynxApplication).tasksDB
        val ds = RemoteDataSource()
        MainViewModelFactory(Repository(db, ds), employeeId)
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

        Log.d("MainActivity", "Current employee: $employeeId")

        adapter = TrabajoAdapter(
            onClickTrabajo = { trabajo ->
                // Show the task details
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
                    getTasksBySpeciality()
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
                }

                else -> {
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }
    }

    private fun getAndSortPendingTasks() {
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

    private fun getTasksBySpeciality() {
        if (checkConnection(this)) {
            lifecycleScope.launch {
                // TODO Implement the logic to filter the tasks by speciality
            }
        } else {
            Toast.makeText(this, getString(R.string.txt_noConnection), Toast.LENGTH_SHORT).show()
        }
    }
}
