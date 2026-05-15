package com.example.nammaplatform.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.nammaplatform.databinding.ActivityLanguageSelectionBinding
import com.example.nammaplatform.utils.LocaleHelper

class LanguageSelectionActivity : BaseActivity() {

    private lateinit var binding: ActivityLanguageSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ensure buttons are clickable
        binding.btnKannada.isEnabled = true
        binding.btnEnglish.isEnabled = true

        binding.btnKannada.setOnClickListener {
            binding.btnKannada.alpha = 0.5f // Visual feedback
            updateLanguage("kn")
        }

        binding.btnEnglish.setOnClickListener {
            binding.btnEnglish.alpha = 0.5f // Visual feedback
            updateLanguage("en")
        }
    }

    private fun updateLanguage(lang: String) {
        LocaleHelper.setLocale(this, lang)
        
        // Mark first time as false
        val prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("is_first_time", false).commit()
        
        navigateToLogin()
    }

    private fun navigateToLogin() {
        if (!isFinishing) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
