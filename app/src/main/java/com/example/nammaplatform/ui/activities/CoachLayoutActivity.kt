package com.example.nammaplatform.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.nammaplatform.R
import com.example.nammaplatform.databinding.ActivityCoachLayoutBinding
import com.example.nammaplatform.models.CoachModel
import com.example.nammaplatform.models.CrowdLevel
import com.example.nammaplatform.models.SeatModel
import com.example.nammaplatform.ui.adapters.CoachAdapter
import com.example.nammaplatform.ui.adapters.SeatAdapter
import com.example.nammaplatform.ui.viewmodels.CoachViewModel

class CoachLayoutActivity : BaseActivity() {

    private lateinit var binding: ActivityCoachLayoutBinding
    private val viewModel: CoachViewModel by viewModels()
    private var selectedCoach: CoachModel? = null
    private lateinit var seatAdapter: SeatAdapter
    private var currentTrainId: String? = null
    private var trainNumber: String? = null
    private var arrivalTime: String? = null
    private var junctionName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoachLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentTrainId = intent.getStringExtra("TRAIN_ID")
        trainNumber = intent.getStringExtra("TRAIN_NUMBER")
        arrivalTime = intent.getStringExtra("ARRIVAL_TIME")
        junctionName = intent.getStringExtra("JUNCTION_NAME")
        val trainName = intent.getStringExtra("TRAIN_NAME") ?: "Train Details"
        val platform = intent.getStringExtra("PLATFORM") ?: "1"
        
        binding.tvCoachTitle.text = trainName
        binding.tvCoachSubtitle.text = getString(R.string.platform_format, platform)

        viewModel.setTrainId(currentTrainId)

        setupCoaches()
        setupSeats()
        setupButtons()
    }

    private fun setupCoaches() {
        viewModel.filteredCoaches.observe(this) { entities ->
            val coaches = entities.map { entity ->
                val colorRes = when(entity.coachName) {
                    "ENGINE" -> R.color.coach_engine
                    "GEN" -> R.color.coach_gen
                    "AC", "AC1", "AC2" -> R.color.coach_ac
                    "LADIES" -> R.color.coach_ladies
                    else -> R.color.coach_sleeper
                }
                CoachModel(
                    name = entity.coachName,
                    type = entity.coachType,
                    typeKn = entity.coachTypeKn,
                    colorRes = colorRes,
                    seatsAvailable = entity.seatAvailability,
                    crowdIntensity = try { CrowdLevel.valueOf(entity.crowdLevel) } catch(e: Exception) { CrowdLevel.LOW },
                    entryPosition = entity.platformPosition,
                    entryPositionKn = entity.platformPositionKn,
                    boardingTip = entity.boardingAdvice,
                    boardingTipKn = entity.boardingAdviceKn,
                    isLadies = entity.isLadies,
                    securityLevel = entity.securityLevel.orEmpty(),
                    securityLevelKn = entity.securityLevelKn.orEmpty(),
                    nearestPolice = entity.nearestPolice.orEmpty(),
                    nearestPoliceKn = entity.nearestPoliceKn.orEmpty(),
                    nearestWashroom = entity.nearestWashroom.orEmpty(),
                    nearestWashroomKn = entity.nearestWashroomKn.orEmpty(),
                    safeZone = entity.safeZone.orEmpty(),
                    safeZoneKn = entity.safeZoneKn.orEmpty()
                )
            }

            val adapter = CoachAdapter(coaches) { coach ->
                displayCoachDetails(coach)
            }
            binding.rvCoaches.adapter = adapter
        }
        
        LinearSnapHelper().attachToRecyclerView(binding.rvCoaches)
    }

    private fun setupSeats() {
        seatAdapter = SeatAdapter(emptyList()) { seat ->
            seat.isSelected = !seat.isSelected
            seatAdapter.notifyDataSetChanged()
        }
        binding.rvSeats.adapter = seatAdapter
    }

    private fun displayCoachDetails(coach: CoachModel) {
        selectedCoach = coach
        val isKannada = com.example.nammaplatform.utils.LocaleHelper.getLanguage(this) == "kn"

        binding.tvPrompt.visibility = View.GONE
        binding.cvCoachDetails.visibility = View.VISIBLE
        binding.tvSeatLayoutLabel.visibility = View.VISIBLE
        binding.rvSeats.visibility = View.VISIBLE
        binding.btnNext.visibility = View.VISIBLE

        binding.tvDetailName.text = coach.name
        binding.tvDetailType.text = if (isKannada) {
            if (!coach.typeKn.isNullOrBlank()) coach.typeKn else coach.type
        } else {
            coach.type
        }
        binding.tvDetailSeats.text = getString(R.string.seats_format, coach.seatsAvailable)
        
        val crowdLabel = when(coach.crowdIntensity) {
            CrowdLevel.LOW -> getString(R.string.low_crowd)
            CrowdLevel.MEDIUM -> getString(R.string.medium_crowd)
            CrowdLevel.HEAVY -> getString(R.string.heavy_crowd)
        }
        binding.tvCrowdLabel.text = crowdLabel
        
        val crowdColor = when(coach.crowdIntensity) {
            CrowdLevel.LOW -> R.color.green
            CrowdLevel.MEDIUM -> R.color.railway_yellow
            CrowdLevel.HEAVY -> R.color.red
        }
        binding.vCrowdIndicator.setBackgroundColor(androidx.core.content.ContextCompat.getColor(this, crowdColor))

        binding.tvDetailPosition.text = if (isKannada) {
            if (!coach.entryPositionKn.isNullOrBlank()) coach.entryPositionKn else coach.entryPosition
        } else {
            coach.entryPosition
        }
        binding.tvDetailAdvice.text = if (isKannada) {
            if (!coach.boardingTipKn.isNullOrBlank()) coach.boardingTipKn else coach.boardingTip
        } else {
            coach.boardingTip
        }

        if (coach.isLadies) {
            binding.llLadiesExtra.visibility = View.VISIBLE
            val security = if (isKannada) {
                if (!coach.securityLevelKn.isNullOrBlank()) coach.securityLevelKn else coach.securityLevel
            } else {
                coach.securityLevel
            }
            val police = if (isKannada) {
                if (!coach.nearestPoliceKn.isNullOrBlank()) coach.nearestPoliceKn else coach.nearestPolice
            } else {
                coach.nearestPolice
            }
            val washroom = if (isKannada) {
                if (!coach.nearestWashroomKn.isNullOrBlank()) coach.nearestWashroomKn else coach.nearestWashroom
            } else {
                coach.nearestWashroom
            }
            
            binding.tvLadiesInfo.text = getString(R.string.ladies_extra_info, 
                security, police, washroom)
        } else {
            binding.llLadiesExtra.visibility = View.GONE
        }

        generateSeats(coach)
    }

    private fun generateSeats(coach: CoachModel) {
        val seats = mutableListOf<SeatModel>()
        for (i in 1..40) {
            seats.add(SeatModel(i.toString(), isAvailable = i % 5 != 0))
        }
        seatAdapter.updateSeats(seats)
    }

    private var tts: android.speech.tts.TextToSpeech? = null

    private fun setupButtons() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnNext.setOnClickListener {
            val intent = Intent(this, RouteTimelineActivity::class.java)
            intent.putExtra("TRAIN_ID", currentTrainId)
            intent.putExtra("TRAIN_NAME", binding.tvCoachTitle.text.toString())
            startActivity(intent)
        }

        tts = android.speech.tts.TextToSpeech(this) { status ->
            if (status != android.speech.tts.TextToSpeech.ERROR) {
                val currentLang = com.example.nammaplatform.utils.LocaleHelper.getLanguage(this)
                tts?.language = if (currentLang == "kn") java.util.Locale("kn", "IN") else java.util.Locale.US
            }
        }

        binding.fabAudio.setOnClickListener {
            val isKannada = com.example.nammaplatform.utils.LocaleHelper.getLanguage(this) == "kn"
            val trainName = binding.tvCoachTitle.text.toString()
            val platform = binding.tvCoachSubtitle.text.toString().filter { it.isDigit() }
            
            val baseAnnouncement = getString(R.string.announcement_main_template, 
                trainName, 
                trainNumber ?: "", 
                platform
            )

            val coachMessage = selectedCoach?.let { coach ->
                val pos = if (isKannada) {
                    if (!coach.entryPositionKn.isNullOrBlank()) coach.entryPositionKn else coach.entryPosition
                } else {
                    coach.entryPosition
                }
                " " + getString(R.string.tts_coach_guide, coach.name, pos)
            } ?: ""
            
            val fullMessage = baseAnnouncement + coachMessage
            
            binding.fabAudio.isEnabled = false
            Toast.makeText(this, getString(R.string.loading_audio), Toast.LENGTH_SHORT).show()
            
            val lang = com.example.nammaplatform.utils.LocaleHelper.getLanguage(this)
            val locale = if (lang == "kn") java.util.Locale("kn", "IN") else java.util.Locale.US
            val result = tts?.setLanguage(locale)
            
            if (result == android.speech.tts.TextToSpeech.LANG_MISSING_DATA || result == android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED) {
                if (lang == "kn") {
                    Toast.makeText(this, "Kannada voice not supported, falling back to English", Toast.LENGTH_SHORT).show()
                    tts?.setLanguage(java.util.Locale.US)
                }
            }

            tts?.speak(fullMessage, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, null)

            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                binding.fabAudio.isEnabled = true
            }, 3000)
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
