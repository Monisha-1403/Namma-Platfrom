package com.example.nammaplatform.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nammaplatform.data.entities.JunctionEntity
import com.example.nammaplatform.databinding.ItemJunctionBinding

class JunctionAdapter(
    private var junctions: List<JunctionEntity>,
    private val onJunctionClick: (JunctionEntity) -> Unit
) : RecyclerView.Adapter<JunctionAdapter.ViewHolder>() {

    private var selectedPosition = -1

    class ViewHolder(val binding: ItemJunctionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemJunctionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val junction = junctions[position]
        val context = holder.itemView.context
        val isKannada = context.getSharedPreferences("Settings", android.content.Context.MODE_PRIVATE)
            .getString("Locale.Helper.Selected.Language", "en") == "kn"
            
        val name = if (isKannada) {
            if (!junction.nameKn.isNullOrBlank()) junction.nameKn else junction.name
        } else {
            junction.name
        }
        holder.binding.tvJunctionName.text = name

        if (position == selectedPosition) {
            holder.binding.cvJunction.strokeWidth = 6 
            holder.binding.cvJunction.cardElevation = 15f
        } else {
            holder.binding.cvJunction.strokeWidth = 0
            holder.binding.cvJunction.cardElevation = 4f
        }

        holder.itemView.setOnClickListener {
            val previousSelected = selectedPosition
            selectedPosition = holder.bindingAdapterPosition
            if (previousSelected != -1) {
                notifyItemChanged(previousSelected)
            }
            notifyItemChanged(selectedPosition)
            onJunctionClick(junction)
        }
    }

    override fun getItemCount() = junctions.size

    fun updateJunctions(newList: List<JunctionEntity>) {
        junctions = newList
        notifyDataSetChanged()
    }
}
