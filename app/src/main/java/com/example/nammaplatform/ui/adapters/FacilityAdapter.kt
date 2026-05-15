package com.example.nammaplatform.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nammaplatform.R
import com.example.nammaplatform.databinding.ItemFacilityBinding
import com.example.nammaplatform.models.FacilityModel

class FacilityAdapter(
    private val facilities: List<FacilityModel>,
    private val onFacilitySelected: (FacilityModel) -> Unit
) : RecyclerView.Adapter<FacilityAdapter.FacilityViewHolder>() {

    class FacilityViewHolder(val binding: ItemFacilityBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacilityViewHolder {
        val binding = ItemFacilityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FacilityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FacilityViewHolder, position: Int) {
        val facility = facilities[position]
        val context = holder.itemView.context
        holder.binding.tvFacilityName.text = facility.name
        holder.binding.ivFacilityIcon.setImageResource(facility.iconRes)
        
        holder.binding.llDetails.visibility = if (facility.isExpanded) android.view.View.VISIBLE else android.view.View.GONE
        holder.binding.ivExpand.rotation = if (facility.isExpanded) 180f else 0f

        val locationLabel = context.getString(R.string.location_label)
        val timingLabel = context.getString(R.string.availability_label)
        
        holder.binding.tvDetailLocation.text = "$locationLabel: ${facility.location} (${facility.distance})"
        holder.binding.tvDetailTiming.text = "$timingLabel: ${facility.availability}"
        holder.binding.tvDetailDesc.text = facility.description
        
        holder.itemView.setOnClickListener {
            facility.isExpanded = !facility.isExpanded
            notifyItemChanged(position)
            onFacilitySelected(facility)
        }
    }

    override fun getItemCount() = facilities.size
}
