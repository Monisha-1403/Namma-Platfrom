package com.example.nammaplatform.ui.activities

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.nammaplatform.R
import com.example.nammaplatform.databinding.ActivityDashboardBinding
import com.example.nammaplatform.ui.adapters.JunctionAdapter
import com.example.nammaplatform.ui.adapters.TrainAdapter
import com.example.nammaplatform.ui.viewmodels.DashboardViewModel
import com.example.nammaplatform.utils.LocaleHelper
import com.google.android.material.snackbar.Snackbar

class DashboardActivity : BaseActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()
    private var junctionAdapter: JunctionAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRadarAnimation()
        setupJunctions()
        setupTrainList()
        setupHelpFab()
        setupFeatureClicks()
        setupBottomNav()
        setupProfile()
        setupAnnouncement()
        observeAnnouncements()
        setupFeedbackFab()
    }

    private fun setupFeedbackFab() {
        binding.fabFeedback.setOnClickListener {
            startActivity(Intent(this, FeedbackActivity::class.java))
        }
    }

    private fun setupRadarAnimation() {
        val pulse = binding.vPulse
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 4f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 4f)
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f)

        val animator = ObjectAnimator.ofPropertyValuesHolder(pulse, scaleX, scaleY, alpha)
        animator.duration = 2000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.start()
    }

    private fun setupJunctions() {
        junctionAdapter = JunctionAdapter(emptyList()) { junction ->
            val isKannada = LocaleHelper.getLanguage(this) == "kn"
            viewModel.selectJunction(junction.junctionId)
            binding.cvStation.visibility = View.VISIBLE
            binding.llFeatures.visibility = View.VISIBLE
            binding.tvNextTrainsLabel.visibility = View.VISIBLE
            binding.rvTrains.visibility = View.VISIBLE
            val jName = if (isKannada) {
                if (!junction.nameKn.isNullOrBlank()) junction.nameKn else junction.name
            } else {
                junction.name
            }
            binding.tvStationName.text = jName
            
            // Update announcement
            binding.tvAnnouncement.text = getString(R.string.announcement_welcome)
            binding.tvAnnouncement.isSelected = true 
        }
        binding.rvJunctions.adapter = junctionAdapter

        viewModel.allJunctions.observe(this) { junctions ->
            junctionAdapter?.updateJunctions(junctions)
        }
    }

    private fun setupTrainList() {
        viewModel.nextThreeTrains.observe(this) { trains ->
            val isKannada = LocaleHelper.getLanguage(this) == "kn"
            val junction = viewModel.selectedJunction.value
            binding.rvTrains.adapter = TrainAdapter(trains, { train ->
                val intent = Intent(this, CoachLayoutActivity::class.java)
                intent.putExtra("TRAIN_ID", train.trainId)
                intent.putExtra("TRAIN_NUMBER", train.trainNumber)
                val tName = if (isKannada) {
                    if (!train.trainNameKannada.isNullOrBlank()) train.trainNameKannada else train.trainNameEnglish
                } else {
                    train.trainNameEnglish
                }
                intent.putExtra("TRAIN_NAME", tName)
                intent.putExtra("PLATFORM", train.platformNumber)
                intent.putExtra("ARRIVAL_TIME", train.arrivalTime)
                val jName = if (isKannada) {
                    if (!junction?.nameKn.isNullOrBlank()) junction?.nameKn else junction?.name
                } else {
                    junction?.name
                }
                intent.putExtra("JUNCTION_NAME", jName)
                startActivity(intent)
            }, { train ->
                speakTrainAnnouncement(train)
            })
            
            if (trains.isNotEmpty()) {
                val firstTrain = trains[0]
                val tName = if (isKannada) {
                    if (!firstTrain.trainNameKannada.isNullOrBlank()) firstTrain.trainNameKannada else firstTrain.trainNameEnglish
                } else {
                    firstTrain.trainNameEnglish
                }
                binding.tvAnnouncement.text = getString(R.string.announcement_format, tName, firstTrain.platformNumber)
                binding.tvAnnouncement.isSelected = true
            }
        }
    }

    private fun observeAnnouncements() {
        val lang = LocaleHelper.getLanguage(this)
        viewModel.announcements.observe(this) { announcements ->
            if (announcements.isNotEmpty()) {
                val latest = announcements[0]
                val msg = if (lang == "kn") {
                    if (!latest.messageKn.isNullOrBlank()) latest.messageKn else latest.messageEn
                } else {
                    latest.messageEn
                }
                binding.tvAnnouncement.text = msg
                binding.tvAnnouncement.isSelected = true
            }
        }
    }

    private var tts: android.speech.tts.TextToSpeech? = null

    private fun setupHelpFab() {
        tts = android.speech.tts.TextToSpeech(this) { status ->
            if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                updateTtsLanguage()
            } else {
                android.util.Log.e("TTS", "Initialization failed")
            }
        }

        binding.fabHelp.setOnClickListener {
            triggerFullAnnouncement()
        }
    }

    private fun updateTtsLanguage() {
        val lang = LocaleHelper.getLanguage(this)
        val locale = if (lang == "kn") java.util.Locale("kn", "IN") else java.util.Locale.US
        val result = tts?.setLanguage(locale)
        if (result == android.speech.tts.TextToSpeech.LANG_MISSING_DATA || result == android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED) {
            android.util.Log.e("TTS", "Language not supported: $lang")
            if (lang == "kn") {
                Toast.makeText(this, getString(R.string.error_tts_kn_not_supported), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun triggerFullAnnouncement() {
        val isKannada = LocaleHelper.getLanguage(this) == "kn"
        val trains = viewModel.nextThreeTrains.value ?: emptyList()
        val junction = viewModel.selectedJunction.value

        if (junction == null || trains.isEmpty()) {
            val announcement = binding.tvAnnouncement.text.toString()
            speakAnnouncement(announcement)
            Snackbar.make(binding.root, announcement, Snackbar.LENGTH_SHORT).show()
            return
        }

        val firstTrain = trains[0]
        val junctionName = if (isKannada) {
            if (!junction.nameKn.isNullOrBlank()) junction.nameKn else junction.name
        } else {
            junction.name
        }
        val trainName = if (isKannada) {
            if (!firstTrain.trainNameKannada.isNullOrBlank()) firstTrain.trainNameKannada else firstTrain.trainNameEnglish
        } else {
            firstTrain.trainNameEnglish
        }

        val mainAnnouncement = getString(
            R.string.announcement_main_template,
            trainName,
            firstTrain.trainNumber,
            firstTrain.platformNumber
        )

        val nextTrainsInfo = if (trains.size > 1) {
            val nextTrain = trains[1]
            val nextTrainName = if (isKannada) {
                if (!nextTrain.trainNameKannada.isNullOrBlank()) nextTrain.trainNameKannada else nextTrain.trainNameEnglish
            } else {
                nextTrain.trainNameEnglish
            }
            " " + getString(R.string.announcement_next_trains, nextTrainName, nextTrain.trainNumber, nextTrain.platformNumber)
        } else ""

        val fullMessage = mainAnnouncement + nextTrainsInfo
        speakAnnouncement(fullMessage)
        Snackbar.make(binding.root, fullMessage, Snackbar.LENGTH_LONG).show()
    }

    private fun setupAnnouncement() {
        binding.tvAnnouncement.isSelected = true 
        binding.ivSpeaker.setOnClickListener {
            triggerFullAnnouncement()
        }
    }

    private fun setupProfile() {
        binding.ivProfile.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun speakAnnouncement(message: String) {
        if (tts != null) {
            updateTtsLanguage()
            tts?.speak(message, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, "AnnouncementID")
        } else {
            Toast.makeText(this, getString(R.string.error_tts_not_ready), Toast.LENGTH_SHORT).show()
        }
    }

    private fun speakTrainAnnouncement(train: com.example.nammaplatform.models.TrainModel) {
        val isKannada = LocaleHelper.getLanguage(this) == "kn"
        val trainName = if (isKannada) {
            if (!train.trainNameKannada.isNullOrBlank()) train.trainNameKannada else train.trainNameEnglish
        } else {
            train.trainNameEnglish
        }

        val message = getString(
            R.string.announcement_main_template,
            trainName ?: "",
            train.trainNumber,
            train.platformNumber
        )
        speakAnnouncement(message)
    }

    private fun setupFeatureClicks() {
        binding.cvFacilities.setOnClickListener {
            val junctionId = viewModel.getSelectedJunctionId().value
            if (junctionId != null) {
                val intent = Intent(this, FacilitiesActivity::class.java)
                intent.putExtra("JUNCTION_ID", junctionId)
                startActivity(intent)
            } else {
                Snackbar.make(binding.root, getString(R.string.error_select_junction), Snackbar.LENGTH_SHORT).show()
            }
        }
        binding.cvEmergency.setOnClickListener {
            startActivity(Intent(this, EmergencyHelpActivity::class.java))
        }
    }

    private fun setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_trains -> true
                R.id.nav_facilities -> {
                    val junctionId = viewModel.getSelectedJunctionId().value
                    if (junctionId != null) {
                        val intent = Intent(this, FacilitiesActivity::class.java)
                        intent.putExtra("JUNCTION_ID", junctionId)
                        startActivity(intent)
                    } else {
                        Snackbar.make(binding.root, getString(R.string.error_select_junction), Snackbar.LENGTH_SHORT).show()
                    }
                    true
                }
                R.id.nav_emergency -> {
                    startActivity(Intent(this, EmergencyHelpActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
