package com.rpandey.covid19tracker_india.ui.statedetails

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity
import com.rpandey.covid19tracker_india.database.model.CountModel
import com.rpandey.covid19tracker_india.databinding.ActivityStateDetailsBinding
import com.rpandey.covid19tracker_india.databinding.ItemDistrictCasesMinimalBinding
import com.rpandey.covid19tracker_india.ui.BaseActivity
import com.rpandey.covid19tracker_india.ui.home.ItemCountCaseBindingModel
import com.rpandey.covid19tracker_india.ui.home.UICaseType
import com.rpandey.covid19tracker_india.util.GridViewInflater
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
            inflateDistricts(it)
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

    private fun inflateDistricts(data: List<DistrictEntity>) {
        binding.districtTitle.visibility = if(data.isEmpty()) View.GONE else View.VISIBLE
        val gridViewInflater = GridViewInflater(3, binding.districtContainer)
        data.forEach { district ->
             gridViewInflater.addView<ItemDistrictCasesMinimalBinding>(R.layout.item_district_cases_minimal).apply {
                tvTitle.text = district.district
                tvCount.text = Util.formatNumber(district.totalConfirmed)
                if (district.confirmed > 0)
                    tvConfirmedDelta.text = String.format("+%s", Util.formatNumber(district.confirmed))
            }
        }
    }
}