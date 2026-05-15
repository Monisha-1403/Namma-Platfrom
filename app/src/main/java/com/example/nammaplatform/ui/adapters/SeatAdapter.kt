package com.example.nammaplatform.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.nammaplatform.R
import com.example.nammaplatform.databinding.ItemSeatBinding
import com.example.nammaplatform.models.SeatModel

class SeatAdapter(
    private var seats: List<SeatModel>,
    private val onSeatClicked: (SeatModel) -> Unit
) : RecyclerView.Adapter<SeatAdapter.SeatViewHolder>() {

    class SeatViewHolder(val binding: ItemSeatBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        val binding = ItemSeatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SeatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
        val seat = seats[position]
        holder.binding.tvSeatNum.text = seat.number
        
        val context = holder.itemView.context
        val color = when {
            seat.isSelected -> ContextCompat.getColor(context, R.color.railway_yellow)
            seat.isAvailable -> ContextCompat.getColor(context, R.color.green)
            else -> ContextCompat.getColor(context, R.color.gray)
        }
        holder.binding.cvSeat.setCardBackgroundColor(color)

        holder.itemView.setOnClickListener {
            if (seat.isAvailable) {
                onSeatClicked(seat)
            }
        }
    }

    override fun getItemCount() = seats.size

    fun updateSeats(newSeats: List<SeatModel>) {
        this.seats = newSeats
        notifyDataSetChanged()
    }
}
