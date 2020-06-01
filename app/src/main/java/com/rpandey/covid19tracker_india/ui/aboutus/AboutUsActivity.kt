package com.rpandey.covid19tracker_india.ui.aboutus

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.rpandey.covid19tracker_india.BuildConfig
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.ui.BaseActivity
import com.rpandey.covid19tracker_india.util.Util
import kotlinx.android.synthetic.main.activity_about_us.*


class AboutUsActivity : BaseActivity() {

    override fun getScreenName(): String {
        return "AboutUs"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.bottomsheet_background)
        setupData()
        iv_close.setOnClickListener { finish() }
    }

    private fun setupData() {
        val version = BuildConfig.VERSION_NAME
        tv_version.text = String.format("Version: %s", version)
        tv_share.setOnClickListener {
            logEvent("ABOUT_SHARE_CLICKED")
            val shareIntent = Util.shareAppIntent()
            startActivity(Intent.createChooser(shareIntent, "Share using..."))
        }
    }
}