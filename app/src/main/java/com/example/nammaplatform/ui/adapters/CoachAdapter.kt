package com.example.nammaplatform.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.nammaplatform.R
import com.example.nammaplatform.databinding.ItemCoachBinding
import com.example.nammaplatform.models.CoachModel
import com.example.nammaplatform.models.CrowdLevel

class CoachAdapter(
    private val coaches: List<CoachModel>,
    private val onCoachSelected: (CoachModel) -> Unit
) : RecyclerView.Adapter<CoachAdapter.CoachViewHolder>() {

    class CoachViewHolder(val binding: ItemCoachBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoachViewHolder {
        val binding = ItemCoachBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CoachViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoachViewHolder, position: Int) {
        val coach = coaches[position]
        val context = holder.itemView.context
        holder.binding.tvCoachName.text = coach.name
        holder.binding.clCoachBg.setBackgroundColor(ContextCompat.getColor(context, coach.colorRes))
        
        holder.binding.ivLadiesIcon.visibility = if (coach.isLadies) View.VISIBLE else View.GONE
        
        val dotColor = when(coach.crowdIntensity) {
            CrowdLevel.LOW -> R.color.green
            CrowdLevel.MEDIUM -> R.color.railway_yellow
            CrowdLevel.HEAVY -> R.color.red
        }
        holder.binding.vCrowdDot.backgroundTintList = ContextCompat.getColorStateList(context, dotColor)

        holder.itemView.setOnClickListener {
            onCoachSelected(coach)
        }
    }

    override fun getItemCount() = coaches.size
}
