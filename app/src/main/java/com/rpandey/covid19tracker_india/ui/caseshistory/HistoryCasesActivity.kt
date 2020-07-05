package com.rpandey.covid19tracker_india.ui.caseshistory

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.fragment.app.FragmentActivity
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.data.Constants
import com.rpandey.covid19tracker_india.data.model.IndianStates
import com.rpandey.covid19tracker_india.ui.BaseActivity
import com.rpandey.covid19tracker_india.util.getViewModel
import com.rpandey.covid19tracker_india.util.observe
import kotlinx.android.synthetic.main.activity_history_cases.*

class HistoryCasesActivity : BaseActivity() {

    companion object {
        private const val KEY_STATE_CODE = "KEY_STATE_CODE"
        fun fireIntent(sourceActivity: FragmentActivity, stateCode: String) {
            sourceActivity.startActivity(
                Intent(sourceActivity, HistoryCasesActivity::class.java).apply {
                    putExtra(KEY_STATE_CODE, stateCode)
                }
            )
        }
    }

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
        val stateCode = intent.extras?.getString(KEY_STATE_CODE)!!
        state_name.text = getStateName(stateCode)
        recycler_view.adapter = adapter
//        recycler_view.layoutAnimation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down)
        observeData(stateCode)
    }

    private fun getStateName(stateCode: String): String {
        return if (stateCode == Constants.STATE_TOTAL_CASE) {
            getString(R.string.title_india)
        } else {
            IndianStates.from(stateCode).stateName
        }
    }

    private fun observeData(stateCode: String) {
        viewModel.getHistory(stateCode).observe(this) {
            adapter.update(it)
            runLayoutAnimation()
        }
    }

    private fun runLayoutAnimation() {
        recycler_view.scheduleLayoutAnimation()
    }
}