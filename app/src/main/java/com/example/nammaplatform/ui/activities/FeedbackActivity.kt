package com.example.nammaplatform.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.nammaplatform.R
import com.example.nammaplatform.databinding.ActivityFeedbackBinding
import com.example.nammaplatform.ui.viewmodels.FeedbackViewModel

class FeedbackActivity : BaseActivity() {

    private lateinit var binding: ActivityFeedbackBinding
    private val viewModel: FeedbackViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
    }

    private fun setupButtons() {
        binding.btnSubmitFeedback.setOnClickListener {
            val rating = binding.rbRating.rating
            val comment = binding.etFeedback.text.toString().trim()
            
            if (rating > 0) {
                viewModel.submitFeedback(rating, comment)
                Toast.makeText(this, getString(R.string.thank_you_feedback), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HappyJourneyActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, getString(R.string.error_rating_required), Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
