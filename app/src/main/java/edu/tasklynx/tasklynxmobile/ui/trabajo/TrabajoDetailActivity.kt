package edu.tasklynx.tasklynxmobile.ui.trabajo

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import edu.tasklynx.tasklynxmobile.R
import edu.tasklynx.tasklynxmobile.TaskLynxApplication
import edu.tasklynx.tasklynxmobile.data.Repository
import edu.tasklynx.tasklynxmobile.data.TaskLynxDataSource
import edu.tasklynx.tasklynxmobile.databinding.TrabajoDetailBinding
import edu.tasklynx.tasklynxmobile.utils.checkConnection
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

class TrabajoDetailActivity: AppCompatActivity() {
    private lateinit var binding: TrabajoDetailBinding

    companion object{
        const val TASK_ID = "TASK_ID"

        fun navigate(activity: AppCompatActivity, taskId: String){
            val intent = Intent(activity, TrabajoDetailActivity::class.java).apply {
                putExtra(TASK_ID, taskId)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

            activity.startActivity(
                intent,
                ActivityOptions.makeSceneTransitionAnimation(activity).toBundle()
            )
        }
    }

    private val vm: TrabajoDetailViewModel by viewModels {
        val db = (application as TaskLynxApplication).tasksDB
        val dataSource = TaskLynxDataSource(db.trabajoDao())
        val repository = Repository(dataSource)
        val taskId = intent.getStringExtra(TASK_ID)
        Log.d("TrabajoDetailActivity", "Received Task ID: $taskId")
        TrabajoDetailViewModel.TrabajoDetailViewModelFactory(repository, taskId!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TrabajoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mToolbar.navigationIcon = AppCompatResources.getDrawable(this, R.drawable.ic_arrow_back)
        binding.mToolbar.setNavigationOnClickListener {
            finishAfterTransition()
        }

//        if(checkConnection(this)){
//            lifecycleScope.launch {
//                repeatOnLifecycle(Lifecycle.State.STARTED){
//                    vm.trabajo.collect {trabajo ->
//                        trabajo?.let {
//                            binding.tvTaskID.text = trabajo.codTrabajo
//                            binding.tvSpeciality.text = trabajo.categoria
//                            binding.tvDescription.text = trabajo.descripcion
//                            binding.tvStartingDate.text = trabajo.fecIni
//                            binding.tvEndingDate.text = trabajo.fecFin
//                            binding.tvPriority.text = trabajo.prioridad.toString()
//                            binding.tvTime.text = trabajo.tiempo.toString()
//
//                            binding.mToolbar.setTitle(trabajo.codTrabajo)
//
//                            if (trabajo.fecFin == null) {
//                                binding.finishBtn.isEnabled = true
//                                binding.finishBtn.visibility = View.VISIBLE
//                            } else {
//                                binding.finishBtn.isEnabled = false
//                                binding.finishBtn.visibility = View.INVISIBLE
//                            }
//
//                            binding.finishBtn.setOnClickListener {
//
//                                val currentDateTime = LocalDateTime.now()
//                                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//                                val finishDate  = currentDateTime.format(formatter).toString()
//
//                                val dialogView = EditText(this@TrabajoDetailActivity)
//
//                                val dialog = AlertDialog.Builder(this@TrabajoDetailActivity)
//                                    .setTitle("Insert the time spent on this task")
//                                    .setView(dialogView)
//                                    .setPositiveButton("OK") { dialog, _ ->
//                                        //TODO: Controlar que el usuario inserte un número
//                                        val timeSpent = dialogView.text.toString().toInt()
//                                        trabajo.fecFin = finishDate
//                                        trabajo.tiempo = timeSpent
//                                        lifecycleScope.launch {
//                                            Log.d("TrabajoDetailActivity", "Finishing task ${trabajo}")
//                                            // TODO : Manejar los errores al finalizar la tarea
//                                            vm.finishTask(trabajo.codTrabajo, finishDate, timeSpent)
//                                            // Error del finish task: Error en el repository.
//                                            //vm.insertTask(trabajo)
//                                            // Error del insert: FK en la bbdd.
//                                        }
//                                        dialog.dismiss()
//                                    }
//                                    .setNegativeButton("Cancel") { dialog, _ ->
//                                        dialog.dismiss()
//                                    }
//                                    .create()
//
//                                dialog.show()
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }
}