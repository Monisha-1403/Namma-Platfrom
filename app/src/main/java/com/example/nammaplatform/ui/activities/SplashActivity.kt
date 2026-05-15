package com.example.nammaplatform.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.nammaplatform.databinding.ActivitySplashBinding
import com.example.nammaplatform.ui.viewmodels.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: MainViewModel by viewModels()
    private var isNavigating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply theme early
        applyTheme(this)
        
        super.onCreate(savedInstanceState)
        android.util.Log.d("SPLASH_ACTIVITY", "APP_START: Splash screen created")
        
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAnimations()
        
        android.util.Log.d("SPLASH_ACTIVITY", "DATA_LOAD: Initializing data...")
        viewModel.initializeData()

        lifecycleScope.launch {
            // Requirement: 10 Seconds duration
            android.util.Log.d("SPLASH_ACTIVITY", "TIMER: Starting 10s wait...")
            delay(10000)
            if (!isFinishing && !isNavigating) {
                isNavigating = true
                android.util.Log.d("SPLASH_ACTIVITY", "NAVIGATE: Moving to next screen")
                navigateNext()
            }
        }
    }

    private fun setupAnimations() {
        // Fade in for text
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 2000
        fadeIn.fillAfter = true

        binding.tvAppName.startAnimation(fadeIn)
        binding.tvTagline.startAnimation(fadeIn)
        binding.tvTaglineExtended.startAnimation(fadeIn)
        binding.pbLoading.startAnimation(fadeIn)

        // Train movement + Scale (Premium feel)
        val trainAnimSet = AnimationSet(true)
        
        val moveTrain = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, -0.5f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        )
        moveTrain.duration = 3000
        
        val scaleTrain = ScaleAnimation(
            0.5f, 1.0f, 0.5f, 1.0f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleTrain.duration = 2000
        
        trainAnimSet.addAnimation(moveTrain)
        trainAnimSet.addAnimation(scaleTrain)
        trainAnimSet.addAnimation(fadeIn)
        
        binding.ivTrainLogo.startAnimation(trainAnimSet)

        // Glow pulse animation
        val glowPulse = ScaleAnimation(
            0.9f, 1.1f, 0.9f, 1.1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        glowPulse.duration = 1500
        glowPulse.repeatMode = Animation.REVERSE
        glowPulse.repeatCount = Animation.INFINITE
        
        binding.vGlow.startAnimation(glowPulse)
    }

    private fun navigateNext() {
        // As per requirement: Splash -> Language -> Login -> Welcome -> Dashboard
        // Do NOT bypass screens.
        val intent = Intent(this, LanguageSelectionActivity::class.java)
        startActivity(intent)
        finish()
    }
}
