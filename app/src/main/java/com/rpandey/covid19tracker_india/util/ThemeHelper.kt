package com.rpandey.covid19tracker_india.util

import androidx.appcompat.app.AppCompatDelegate

object ThemeHelper {

    const val LIGHT_MODE = 0
    const val DARK_MODE = 1
    const val DEFAULT = 2

    fun applyTheme(theme: Int) {
        when (theme) {
            LIGHT_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            DARK_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            DEFAULT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}