package com.rpandey.covid19tracker_india.util

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.rpandey.covid19tracker_india.CovidApplication

object ThemeHelper {

    private const val KEY_USER_SELECTED = "KEY_USER_SELECTED"

    const val LIGHT_MODE = 0
    const val DARK_MODE = 1
    const val DEFAULT = 2

    fun applyTheme(theme: Int) {
        PreferenceHelper.putInt(KEY_USER_SELECTED, theme)
        if (theme !=  getTheme(CovidApplication.INSTANCE)) {
            when (theme) {
                LIGHT_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                DARK_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                DEFAULT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    fun getTheme(context: Context): Int {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> DARK_MODE
            Configuration.UI_MODE_NIGHT_NO -> LIGHT_MODE
            Configuration.UI_MODE_NIGHT_UNDEFINED -> LIGHT_MODE
            else -> LIGHT_MODE
        }
    }

    fun applyUserPrefTheme() {
        applyTheme(PreferenceHelper.getInt(KEY_USER_SELECTED, DEFAULT))
    }

    fun toggle(context: Context) {
        applyTheme(if (getTheme(context) == LIGHT_MODE) DARK_MODE else LIGHT_MODE)
    }
}