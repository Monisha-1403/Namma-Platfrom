package com.example.nammaplatform.ui.activities

import android.os.Bundle
import android.widget.Toast
import com.example.nammaplatform.R
import com.example.nammaplatform.databinding.ActivityEmergencyHelpBinding

class EmergencyHelpActivity : BaseActivity() {

    private lateinit var binding: ActivityEmergencyHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergencyHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
    }

    private fun setupButtons() {
        binding.cvEmergencyMain.setOnClickListener {
            Toast.makeText(this, getString(R.string.call_139), Toast.LENGTH_SHORT).show()
        }

        binding.btnPolice.setOnClickListener {
            Toast.makeText(this, getString(R.string.call_police), Toast.LENGTH_LONG).show()
        }

        binding.btnMedical.setOnClickListener {
            Toast.makeText(this, getString(R.string.call_medical), Toast.LENGTH_LONG).show()
        }

        binding.btnWomenSafety.setOnClickListener {
            Toast.makeText(this, getString(R.string.call_women), Toast.LENGTH_LONG).show()
        }

        binding.btnLostFound.setOnClickListener {
            Toast.makeText(this, getString(R.string.directing_lost_found), Toast.LENGTH_LONG).show()
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        
        binding.btnNext.visibility = android.view.View.GONE
    }
}
