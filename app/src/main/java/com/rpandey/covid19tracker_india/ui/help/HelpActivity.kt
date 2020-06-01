package com.rpandey.covid19tracker_india.ui.help

import android.os.Bundle
import android.view.WindowManager
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_help.*

class HelpActivity : BaseActivity() {

    override fun getScreenName(): String {
        return "AddWidgetPage"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.bottomsheet_background)
        iv_close.setOnClickListener { finish() }
    }
}