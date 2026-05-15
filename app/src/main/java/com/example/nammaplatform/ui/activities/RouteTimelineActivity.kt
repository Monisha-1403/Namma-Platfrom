package com.example.nammaplatform.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.nammaplatform.R
import com.example.nammaplatform.databinding.ActivityRouteTimelineBinding
import com.example.nammaplatform.ui.viewmodels.RouteViewModel
import com.google.android.material.card.MaterialCardView

class RouteTimelineActivity : BaseActivity() {

    private lateinit var binding: ActivityRouteTimelineBinding
    private val viewModel: RouteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRouteTimelineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val trainId = intent.getStringExtra("TRAIN_ID")
        val trainName = intent.getStringExtra("TRAIN_NAME") ?: "Route Timeline"
        
        binding.tvRouteTitle.text = trainName
        viewModel.setTrainId(trainId)

        setupTimeline()
        setupButtons()
    }

    private fun setupTimeline() {
        val isKannada = com.example.nammaplatform.utils.LocaleHelper.getLanguage(this) == "kn"
        viewModel.filteredRoute.observe(this) { entities ->
            android.util.Log.d("ROUTE_UI", "Received ${entities?.size ?: 0} route stops")
            binding.llTimeline.removeAllViews()
            
            if (entities.isNullOrEmpty()) {
                android.util.Log.w("ROUTE_UI", "Route list is empty for this train")
                return@observe
            }

            // Randomly set a current station index for demo purposes
            val currentStationIndex = entities.size / 2

            for (i in entities.indices) {
                val itemView = LayoutInflater.from(this).inflate(R.layout.timeline_item, binding.llTimeline, false)
                val tvName = itemView.findViewById<TextView>(R.id.tv_station_name)
                val tvTime = itemView.findViewById<TextView>(R.id.tv_time)
                val tvPlatform = itemView.findViewById<TextView>(R.id.tv_platform)
                val cvDot = itemView.findViewById<MaterialCardView>(R.id.cv_dot)
                val vTopLine = itemView.findViewById<View>(R.id.v_top_line)
                val vBottomLine = itemView.findViewById<View>(R.id.v_bottom_line)
                val cvInfo = itemView.findViewById<MaterialCardView>(R.id.cv_station_info)

                val routeStop = entities[i]
                val stationName = if (isKannada) {
                    if (!routeStop.stationNameKannada.isNullOrBlank()) routeStop.stationNameKannada else routeStop.stationNameEnglish
                } else {
                    routeStop.stationNameEnglish
                }
                tvName.text = stationName
                
                val timeStr = if (routeStop.arrival == "-") getString(R.string.departure_short, routeStop.departure)
                              else if (routeStop.departure == "-") getString(R.string.arrival_short, routeStop.arrival)
                              else getString(R.string.time_range_format, routeStop.arrival, routeStop.departure)
                
                tvTime.text = timeStr
                tvPlatform.text = getString(R.string.platform_format, routeStop.platform)

                // Color Logic: Current: Yellow, Visited: Green, Upcoming: Grey
                when {
                    i < currentStationIndex -> {
                        // Visited
                        cvDot.setCardBackgroundColor(ContextCompat.getColor(this, R.color.green))
                        vTopLine.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                        vBottomLine.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                    }
                    i == currentStationIndex -> {
                        // Current
                        tvName.setTextColor(ContextCompat.getColor(this, R.color.railway_yellow))
                        cvDot.setCardBackgroundColor(ContextCompat.getColor(this, R.color.railway_yellow))
                        vTopLine.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                        vBottomLine.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                        cvInfo.setStrokeColor(ContextCompat.getColor(this, R.color.railway_yellow))
                        cvInfo.setStrokeWidth(4)
                    }
                    else -> {
                        // Upcoming
                        cvDot.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                        vTopLine.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                        vBottomLine.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                    }
                }

                if (i == 0) vTopLine.visibility = View.INVISIBLE
                if (i == entities.size - 1) vBottomLine.visibility = View.INVISIBLE

                binding.llTimeline.addView(itemView)
            }
        }
    }

    private fun setupButtons() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnNext.setOnClickListener {
            // We could navigate back to facilities or a summary
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
