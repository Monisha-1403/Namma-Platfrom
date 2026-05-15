package com.example.nammaplatform.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.nammaplatform.R
import com.example.nammaplatform.databinding.ActivityLoginBinding
import com.example.nammaplatform.utils.FirebaseHelper
import com.example.nammaplatform.utils.LocaleHelper

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartJourney.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()

            if (name.isNotEmpty() && phone.length == 10) {
                saveUserData(name, phone)
                
                // Get settings for full profile sync
                val settings = getSharedPreferences("Settings", MODE_PRIVATE)
                val isDark = settings.getBoolean("dark_mode", false)
                val accessibility = settings.getBoolean("large_text", false)
                val audio = settings.getBoolean("voice_guide", true)
                
                FirebaseHelper.syncUserProfile(
                    name = name,
                    phone = phone,
                    language = LocaleHelper.getLanguage(this),
                    theme = if (isDark) "Dark" else "Light",
                    audioPreference = audio,
                    accessibility = accessibility
                )

                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else if (name.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_enter_name), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.error_invalid_phone), Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, LanguageSelectionActivity::class.java))
            finish()
        }
    }

    private fun saveUserData(name: String, phone: String) {
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        prefs.edit().putString("user_name", name).putString("user_phone", phone).apply()
    }
}
