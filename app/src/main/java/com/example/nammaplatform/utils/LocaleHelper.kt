package com.example.nammaplatform.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"

    fun setLocale(context: Context, language: String): Context {
        android.util.Log.d("LANGUAGE_DEBUG", "setLocale: Requesting change to [$language]")
        persist(context, language)
        return updateResources(context, language)
    }

    fun onAttach(context: Context): Context {
        val lang = getPersistedData(context, "en")
        android.util.Log.d("LANGUAGE_DEBUG", "onAttach: Applying language [$lang]")
        return updateResources(context, lang)
    }

    fun getLanguage(context: Context): String {
        val lang = getPersistedData(context, "en")
        return lang
    }

    private fun persist(context: Context, language: String) {
        val preferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(SELECTED_LANGUAGE, language)
        editor.commit() // Force immediate write
        android.util.Log.d("LANGUAGE_DEBUG", "persist: Saved [$language] to SharedPreferences")
    }

    private fun getPersistedData(context: Context, defaultLanguage: String): String {
        val preferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage) ?: defaultLanguage
    }

    private fun updateResources(context: Context, language: String): Context {
        return try {
            val locale = if (language == "kn") Locale("kn", "IN") else Locale("en", "US")
            Locale.setDefault(locale)

            val res = context.resources
            val configuration = Configuration(res.configuration)
            configuration.setLocale(locale)
            configuration.setLayoutDirection(locale)

            android.util.Log.d("LANGUAGE_DEBUG", "updateResources: Configuration set to locale [${locale.language}]")

            // Update legacy resources for wider compatibility
            res.updateConfiguration(configuration, res.displayMetrics)

            context.createConfigurationContext(configuration)
        } catch (e: Exception) {
            android.util.Log.e("LANGUAGE_DEBUG", "updateResources: ERROR - ${e.message}")
            context
        }
    }
}
