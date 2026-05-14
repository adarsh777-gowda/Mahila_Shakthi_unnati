package com.adarsh.mahilashaktiunnati.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import java.util.*

object LanguageManager {
    private const val PREF_NAME = "language_prefs"
    private const val KEY_LANGUAGE = "selected_language"
    private const val DEFAULT_LANGUAGE = "en"
    
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    fun setLanguage(context: Context, languageCode: String) {
        val preferences = getPreferences(context)
        preferences.edit().putString(KEY_LANGUAGE, languageCode).apply()
        applyLanguage(context, languageCode)
    }
    
    fun getLanguage(context: Context): String {
        val preferences = getPreferences(context)
        return preferences.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }
    
    fun applyLanguage(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(configuration)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
        }
    }
    
    fun getCurrentLanguageName(context: Context): String {
        return when (getLanguage(context)) {
            "kn" -> "ಕನ್ನಡ"
            else -> "English"
        }
    }
    
    fun initializeLanguage(context: Context) {
        val currentLanguage = getLanguage(context)
        applyLanguage(context, currentLanguage)
    }
    
    fun isKannadaSelected(context: Context): Boolean {
        return getLanguage(context) == "kn"
    }
    
    fun toggleLanguage(context: Context) {
        val newLanguage = if (isKannadaSelected(context)) "en" else "kn"
        setLanguage(context, newLanguage)
    }
}
