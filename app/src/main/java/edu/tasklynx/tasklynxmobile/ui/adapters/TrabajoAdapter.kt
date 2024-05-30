package edu.tasklynx.tasklynxmobile.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.tasklynx.tasklynxmobile.databinding.TrabajoItemBinding
import edu.tasklynx.tasklynxmobile.models.Trabajo

class TrabajoAdapter (
    val onClickTrabajo: (task: Trabajo, position: Int) -> Unit,
): ListAdapter<Trabajo, TrabajoAdapter.TrabajoViewHolder>(TrabajoDiffCallback()) {
    inner class TrabajoViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = TrabajoItemBinding.bind(view)

        fun bind (task: Trabajo) {
            binding.tvTaskID.text = task.codTrabajo
            binding.tvDescription.text = task.descripcion
            binding.tvStartingDate.text = task.fecIni
            binding.tvPriority.text = task.prioridad.toString()

            if(task.fecFin.isNullOrBlank()) {
                binding.tvEndingDate.visibility = View.INVISIBLE
            } else {
                binding.tvEndingDate.text = task.fecFin
            }

            binding.root.setOnClickListener {
                onClickTrabajo(task, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrabajoViewHolder {
        return TrabajoViewHolder(
            TrabajoItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).root
        )
    }

    override fun onBindViewHolder(holder: TrabajoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class TrabajoDiffCallback: DiffUtil.ItemCallback<Trabajo>() {
    override fun areItemsTheSame(oldItem: Trabajo, newItem: Trabajo): Boolean {
        return oldItem.codTrabajo == newItem.codTrabajo
    }

    override fun areContentsTheSame(oldItem: Trabajo, newItem: Trabajo): Boolean {
        return oldItem == newItem
    }
}
