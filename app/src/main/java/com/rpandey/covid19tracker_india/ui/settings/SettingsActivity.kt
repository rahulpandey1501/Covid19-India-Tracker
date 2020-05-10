package com.rpandey.covid19tracker_india.ui.settings

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.rpandey.covid19tracker_india.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            temp()
        }

        public fun temp() {
            val p1 = preferenceManager.findPreference<SwitchPreferenceCompat>("district_notification")
            Log.d("Preff", "${p1?.isChecked == true}")

            val p2 = preferenceManager.findPreference<SwitchPreferenceCompat>("state_notification")
            Log.d("Preff", "${p2?.isChecked == true}")

            val p3 = preferenceManager.findPreference<SwitchPreferenceCompat>("today_update_notification")
            Log.d("Preff", "${p3?.isChecked == true}")
        }

    }
}