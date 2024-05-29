package edu.tasklynx.tasklynxmobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.tasklynx.tasklynxmobile.databinding.TrabajoDetailBinding
import edu.tasklynx.tasklynxmobile.databinding.TrabajoItemBinding
import edu.tasklynx.tasklynxmobile.models.Trabajo

class TrabajoAdapter (
    val onClickTrabajo: (idTask: String, position: Int) -> Unit,
): ListAdapter<Trabajo, TrabajoAdapter.TrabajoViewHolder>(TrabajoDiffCallback()) {

    inner class TrabajoViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = TrabajoItemBinding.bind(view)

        fun bind (trabajo: Trabajo) {
            binding.tvTaskID.text = trabajo.codTrabajo
            binding.tvDescription.text = trabajo.descripcion
            binding.tvStartingDate.text = trabajo.fecIni
            binding.tvEndingDate.text = trabajo.fecFin
            binding.tvPriority.text = trabajo.prioridad.toString()

            binding.root.setOnClickListener {
                onClickTrabajo(trabajo.codTrabajo, adapterPosition)
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
