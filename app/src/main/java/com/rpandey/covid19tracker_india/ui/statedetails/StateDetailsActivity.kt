package com.rpandey.covid19tracker_india.ui.statedetails

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity
import com.rpandey.covid19tracker_india.database.model.CountModel
import com.rpandey.covid19tracker_india.databinding.ActivityStateDetailsBinding
import com.rpandey.covid19tracker_india.databinding.ItemDistrictCasesBinding
import com.rpandey.covid19tracker_india.ui.BaseActivity
import com.rpandey.covid19tracker_india.ui.home.ItemCountCaseBindingModel
import com.rpandey.covid19tracker_india.ui.home.UICaseType
import com.rpandey.covid19tracker_india.util.Util
import com.rpandey.covid19tracker_india.util.getViewModel
import com.rpandey.covid19tracker_india.util.observe

class StateDetailsActivity : BaseActivity() {

    companion object {
        const val KEY_STATE = "KEY_STATE"
        const val KEY_STATE_NAME = "KEY_STATE_NAME"

        fun getIntent(sourceActivity: FragmentActivity, state: String, stateName: String): Intent {
            return Intent(sourceActivity, StateDetailsActivity::class.java).apply {
                putExtra(KEY_STATE, state)
                putExtra(KEY_STATE_NAME, stateName)
            }
        }
    }

    lateinit var binding: ActivityStateDetailsBinding

    private val viewModel: StoreDetailsViewModel by lazy {
        getViewModel { StoreDetailsViewModel(repository) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_state_details)
        observeLiveData()
    }

    private fun observeLiveData() {

        val state = intent.getStringExtra(KEY_STATE)!!
        val stateName = intent.getStringExtra(KEY_STATE_NAME)!!

        binding.title.text = stateName
        binding.ivClose.setOnClickListener { finish() }

        viewModel.lastUpdatedTime(state).observe(this) {
            val title = String.format("%s %s", "Last updated: ", it)
            binding.lastUpdate.text = title
        }

        viewModel.getCount(state, stateName).observe(this) {
            it.keys.forEach { uiCase ->
                setUiCaseModel(uiCase, it)
            }
        }

        viewModel.getDistricts(stateName).observe(this) {
            inflateBookmarkedDistricts(it)
        }
    }

    private fun setUiCaseModel(caseType: UICaseType, allCases: Map<UICaseType, CountModel>) {
        with(binding.casesLayout) {
            val itemModel = ItemCountCaseBindingModel(applicationContext)
            itemModel.init(caseType, allCases)
            when (caseType) {
                UICaseType.TYPE_CONFIRMED -> confirmVm = itemModel
                UICaseType.TYPE_ACTIVE -> activeVm = itemModel
                UICaseType.TYPE_RECOVERED -> recoverVm = itemModel
                UICaseType.TYPE_DEATH -> deathVm = itemModel
                UICaseType.TYPE_TESTING -> testingVm = itemModel
            }
        }
    }

    private fun inflateBookmarkedDistricts(data: List<DistrictEntity>) {
        binding.districtContainer.removeAllViews()
        data.forEach { district ->
            ItemDistrictCasesBinding.inflate(LayoutInflater.from(this), binding.districtContainer, true).apply {
                val param = root.layoutParams as GridLayout.LayoutParams
                param.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                root.layoutParams = param

                tvTitle.text = district.district
                tvCount.text = Util.formatNumber(district.totalConfirmed)
                if (district.confirmed > 0)
                    tvConfirmedDelta.text = String.format("+%s", Util.formatNumber(district.confirmed))
            }
        }
    }
}