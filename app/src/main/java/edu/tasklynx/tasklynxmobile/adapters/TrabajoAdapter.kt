package edu.tasklynx.tasklynxmobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.tasklynx.tasklynxmobile.databinding.TrabajoDetailBinding
import edu.tasklynx.tasklynxmobile.models.Trabajo

class TrabajoAdapter (
    val onClickTrabajo: (idTask: Int) -> Unit,
): ListAdapter<Trabajo, TrabajoAdapter.TrabajoViewHolder>(ShowsDiffCallback()) {

    inner class TrabajoViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val bind = TrabajoDetailBinding.bind(view)

        fun bind (trabajo: Trabajo) {
            // Bindeo de datos al ViewHolder de trabajo
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrabajoViewHolder {
        return TrabajoViewHolder(
            TrabajoDetailBinding.inflate(
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

class ShowsDiffCallback: DiffUtil.ItemCallback<Trabajo>() {
    override fun areItemsTheSame(oldItem: Trabajo, newItem: Trabajo): Boolean {
        return oldItem.codTrabajo == newItem.codTrabajo
    }

    override fun areContentsTheSame(oldItem: Trabajo, newItem: Trabajo): Boolean {
        return oldItem == newItem
    }
}
