package com.example.nammaplatform.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import com.example.nammaplatform.R
import com.example.nammaplatform.databinding.ActivityWelcomeBinding

class WelcomeActivity : BaseActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userName = getUserName()
        binding.tvGreeting.text = getString(R.string.greeting_format, userName)
        binding.tvFindingPlatform.text = getString(R.string.finding_platform)

        setupAnimations()

        binding.btnContinue.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setupAnimations() {
        // Station Master / Train Entry Style Animation
        val moveIn = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, -1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f
        )
        moveIn.duration = 1500
        
        val bounce = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, -0.05f
        )
        bounce.duration = 800
        bounce.repeatMode = Animation.REVERSE
        bounce.repeatCount = Animation.INFINITE
        bounce.startOffset = 1500

        val animSet = AnimationSet(true)
        animSet.addAnimation(moveIn)
        animSet.addAnimation(bounce)
        
        binding.ivWelcomeAnim.startAnimation(animSet)
    }

    private fun getUserName(): String {
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        return prefs.getString("user_name", "User") ?: "User"
    }
}
