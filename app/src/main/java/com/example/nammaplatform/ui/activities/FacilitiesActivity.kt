package com.example.nammaplatform.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.example.nammaplatform.databinding.ActivityFacilitiesBinding
import com.example.nammaplatform.models.FacilityModel
import com.example.nammaplatform.ui.adapters.FacilityAdapter
import com.example.nammaplatform.ui.viewmodels.FacilitiesViewModel

class FacilitiesActivity : BaseActivity() {

    private lateinit var binding: ActivityFacilitiesBinding
    private val viewModel: FacilitiesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFacilitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val junctionId = intent.getStringExtra("JUNCTION_ID")
        viewModel.setJunctionId(junctionId)

        setupFacilities()
        setupButtons()
    }

    private fun setupFacilities() {
        val isKannada = com.example.nammaplatform.utils.LocaleHelper.getLanguage(this) == "kn"

        viewModel.filteredFacilities.observe(this) { entities ->
            android.util.Log.d("FACILITIES_UI", "Received ${entities?.size ?: 0} facilities for junction")
            
            if (entities.isNullOrEmpty()) {
                android.util.Log.w("FACILITIES_UI", "No facilities found for this junctionId")
                binding.rvFacilities.visibility = android.view.View.GONE
                binding.tvEmptyState.visibility = android.view.View.VISIBLE
            } else {
                binding.rvFacilities.visibility = android.view.View.VISIBLE
                binding.tvEmptyState.visibility = android.view.View.GONE
            }

            val facilities = entities.map { entity ->
                val iconRes = when (entity.facilityName) {
                    "Water" -> android.R.drawable.ic_menu_gallery 
                    "Restroom" -> android.R.drawable.ic_menu_myplaces
                    "Food Court", "Food Centre" -> android.R.drawable.ic_menu_view
                    "Police" -> android.R.drawable.ic_lock_power_off
                    "Ticket Counter" -> android.R.drawable.ic_menu_agenda
                    "Wheelchair Assistance" -> android.R.drawable.ic_menu_directions
                    "Charging Point" -> android.R.drawable.ic_menu_send
                    "Medical Help" -> android.R.drawable.ic_menu_help
                    "Exit" -> android.R.drawable.ic_menu_close_clear_cancel
                    "Waiting Hall" -> android.R.drawable.ic_menu_recent_history
                    else -> android.R.drawable.ic_dialog_info
                }
                FacilityModel(
                    name = if (isKannada) {
                        if (!entity.facilityNameKn.isNullOrBlank()) entity.facilityNameKn else entity.facilityName
                    } else entity.facilityName,
                    iconRes = iconRes,
                    location = if (isKannada) {
                        if (!entity.locationKn.isNullOrBlank()) entity.locationKn else entity.location
                    } else entity.location,
                    distance = if (isKannada) {
                        if (!entity.distanceKn.isNullOrBlank()) entity.distanceKn else entity.distance
                    } else entity.distance,
                    availability = if (isKannada) {
                        if (!entity.availabilityKn.isNullOrBlank()) entity.availabilityKn else entity.availability
                    } else entity.availability,
                    description = if (isKannada) {
                        if (!entity.descriptionKn.isNullOrBlank()) entity.descriptionKn else entity.description
                    } else entity.description
                )
            }
            
            binding.rvFacilities.adapter = FacilityAdapter(facilities) { _ ->
                // Details are now handled in the adapter via expansion
            }
        }
    }

    private fun setupButtons() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, FeedbackActivity::class.java))
        }
    }
}
