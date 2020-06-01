package com.rpandey.covid19tracker_india.ui.caseshistory

import android.os.Bundle
import android.view.animation.AnimationUtils
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.ui.BaseActivity
import com.rpandey.covid19tracker_india.util.getViewModel
import com.rpandey.covid19tracker_india.util.observe
import kotlinx.android.synthetic.main.activity_history_cases.*

class HistoryCasesActivity : BaseActivity() {

    override fun getScreenName(): String {
        return "HistoryCasesActivity"
    }

    private val viewModel by lazy {
        getViewModel { HistoryCasesViewModel(repository) }
    }

    private val adapter = HistoryCasesAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_cases)
        iv_close.setOnClickListener { finish() }
        recycler_view.adapter = adapter
        recycler_view.layoutAnimation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down)
        observeData()
    }

    private fun observeData() {
        viewModel.getHistory().observe(this) {
            adapter.update(it)
            runLayoutAnimation()
        }
    }

    private fun runLayoutAnimation() {
        recycler_view.scheduleLayoutAnimation()
    }
}