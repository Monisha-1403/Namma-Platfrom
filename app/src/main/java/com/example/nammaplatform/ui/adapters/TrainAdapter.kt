package com.example.nammaplatform.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nammaplatform.R
import com.example.nammaplatform.databinding.ItemTrainBinding
import com.example.nammaplatform.models.TrainModel

class TrainAdapter(
    private val trains: List<TrainModel>,
    private val onTrainClick: (TrainModel) -> Unit,
    private val onAudioClick: (TrainModel) -> Unit = {}
) : RecyclerView.Adapter<TrainAdapter.TrainViewHolder>() {

    class TrainViewHolder(val binding: ItemTrainBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainViewHolder {
        val binding = ItemTrainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrainViewHolder, position: Int) {
        val train = trains[position]
        val context = holder.itemView.context
        val isKannada = context.getSharedPreferences("Settings", android.content.Context.MODE_PRIVATE)
            .getString("Locale.Helper.Selected.Language", "en") == "kn"

        val trainName = if (isKannada) {
            if (!train.trainNameKannada.isNullOrBlank()) train.trainNameKannada else train.trainNameEnglish
        } else {
            train.trainNameEnglish
        }
        holder.binding.tvTrainName.text = trainName
        holder.binding.tvTrainNumber.text = train.trainNumber
        holder.binding.tvPlatformNum.text = train.platformNumber
        
        holder.binding.chipStatus.text = if (train.status.equals("Delayed", ignoreCase = true)) {
            context.getString(R.string.delayed)
        } else {
            context.getString(R.string.on_time)
        }

        if (train.isDelayed) {
            holder.binding.chipStatus.setChipBackgroundColorResource(R.color.red)
        } else {
            holder.binding.chipStatus.setChipBackgroundColorResource(R.color.green)
        }

        holder.binding.btnSpeak.setOnClickListener {
            onAudioClick(train)
        }

        holder.itemView.setOnClickListener {
            onTrainClick(train)
        }
    }

    override fun getItemCount() = trains.size
}
