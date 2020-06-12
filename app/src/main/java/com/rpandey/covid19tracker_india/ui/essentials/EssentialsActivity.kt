package com.rpandey.covid19tracker_india.ui.essentials

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.util.attachFragment
import kotlinx.android.synthetic.main.activity_about_us.*

class EssentialsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_essentials_activiy)
        attachEssentialFragment()
        iv_close.setOnClickListener { finish() }
    }

    private fun attachEssentialFragment() {
        attachFragment(EssentialsFragment.TAG, R.id.container, false) {
            EssentialsFragment()
        }
    }
}