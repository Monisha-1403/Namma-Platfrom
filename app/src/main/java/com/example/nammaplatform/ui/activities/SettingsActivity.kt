package com.example.nammaplatform.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.nammaplatform.R
import com.example.nammaplatform.databinding.ActivitySettingsBinding
import com.example.nammaplatform.utils.LocaleHelper

class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSettings()
        setupButtons()
    }

    private fun setupSettings() {
        val prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val userPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        
        binding.tvUserName.text = userPrefs.getString("user_name", getString(R.string.default_user))
        binding.tvUserPhone.text = userPrefs.getString("user_phone", getString(R.string.default_phone))

        // Theme
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        binding.switchDarkMode.isChecked = isDarkMode
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // Language
        val savedLang = LocaleHelper.getLanguage(this)
        android.util.Log.d("LANGUAGE_DEBUG", "Settings: Loaded saved language [$savedLang]")

        // Set state without listener to avoid trigger loop
        if (savedLang == "kn") {
            binding.toggleLanguage.check(R.id.btn_lang_kn)
        } else {
            binding.toggleLanguage.check(R.id.btn_lang_en)
        }

        binding.toggleLanguage.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val newLang = if (checkedId == R.id.btn_lang_kn) "kn" else "en"
                val activeLang = LocaleHelper.getLanguage(this)
                
                android.util.Log.d("LANGUAGE_DEBUG", "Settings: Toggle clicked. New=[$newLang], Active=[$activeLang]")

                if (newLang != activeLang) {
                    android.util.Log.d("LANGUAGE_DEBUG", "Settings: Switching language to [$newLang]")
                    LocaleHelper.setLocale(this, newLang)
                    
                    // Restart app stack to apply changes globally
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        }

        // Other toggles
        binding.switchVoiceGuide.isChecked = prefs.getBoolean("voice_guide", true)
        binding.switchVoiceGuide.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("voice_guide", isChecked).apply()
        }
        
        binding.switchLargeText.isChecked = prefs.getBoolean("large_text", false)
        binding.switchLargeText.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("large_text", isChecked).commit()
            recreate() // Make it instant
        }

        binding.btnReset.setOnClickListener {
            getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit().clear().apply()
            prefs.edit().clear().apply()
            Toast.makeText(this, getString(R.string.msg_app_reset), Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupButtons() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnFeedback.setOnClickListener {
            startActivity(Intent(this, FeedbackActivity::class.java))
        }
    }
}
