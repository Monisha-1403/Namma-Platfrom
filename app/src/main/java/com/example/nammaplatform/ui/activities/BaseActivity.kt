package com.example.nammaplatform.ui.activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.nammaplatform.utils.LocaleHelper

open class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        val localeContext = LocaleHelper.onAttach(newBase)
        val prefs = localeContext.getSharedPreferences("Settings", MODE_PRIVATE)
        val isLargeText = prefs.getBoolean("large_text", false)
        
        val configuration = localeContext.resources.configuration
        configuration.fontScale = if (isLargeText) 1.3f else 1.0f
        
        val context = localeContext.createConfigurationContext(configuration)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        applyTheme(this)
        super.onCreate(savedInstanceState)
    }

    companion object {
        fun applyTheme(context: Context) {
            try {
                val prefs = context.getSharedPreferences("Settings", MODE_PRIVATE)
                val isDarkMode = prefs.getBoolean("dark_mode", false)
                val targetMode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO

                if (AppCompatDelegate.getDefaultNightMode() != targetMode) {
                    android.util.Log.d("BASE_ACTIVITY", "THEME_CHANGE: Setting night mode to $targetMode")
                    AppCompatDelegate.setDefaultNightMode(targetMode)
                }
            } catch (e: Exception) {
                android.util.Log.e("BASE_ACTIVITY", "THEME_ERROR: Failed to apply theme", e)
            }
        }
    }
}
