package com.example.nammaplatform.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import com.example.nammaplatform.databinding.ActivityHappyJourneyBinding

class HappyJourneyActivity : BaseActivity() {

    private lateinit var binding: ActivityHappyJourneyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHappyJourneyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAnimations()

        binding.btnFinish.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupAnimations() {
        val moveTrain = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, -1.0f,
            Animation.RELATIVE_TO_PARENT, 1.2f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        )
        moveTrain.duration = 4000
        moveTrain.repeatCount = Animation.INFINITE
        
        binding.ivTrainMoving.startAnimation(moveTrain)
    }
}
