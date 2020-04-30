package com.rpandey.covid19tracker_india.util

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.rpandey.covid19tracker_india.CovidApplication

object PreferenceHelper {

    private const val PREFERENCE_NAME = "COVID_PREF"
    private val preference by lazy {
        CovidApplication.INSTANCE.applicationContext.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
    }

    fun getString(key: String, default: String? = null): String? {
        return preference.getString(key, default)
    }

    fun getInt(key: String, default: Int = -1): Int {
        return preference.getInt(key, default)
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return preference.getBoolean(key, default)
    }

    fun putString(key: String, data: String) {
        getEditor().putString(key, data).apply()
    }

    fun putInt(key: String, data: Int) {
        getEditor().putInt(key, data).apply()
    }

    fun putBoolean(key: String, data: Boolean) {
        getEditor().putBoolean(key, data).apply()
    }

    private fun getEditor(): SharedPreferences.Editor {
        return CovidApplication.INSTANCE.applicationContext.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).edit()
    }
}