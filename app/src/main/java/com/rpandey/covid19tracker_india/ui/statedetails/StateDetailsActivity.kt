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
import com.rpandey.covid19tracker_india.ui.BaseActivity
import com.rpandey.covid19tracker_india.ui.common.HeaderViewHelper
import com.rpandey.covid19tracker_india.ui.common.SortOn
import com.rpandey.covid19tracker_india.ui.home.ItemCountCaseBindingModel
import com.rpandey.covid19tracker_india.ui.home.UICaseType
import com.rpandey.covid19tracker_india.util.getViewModel
import com.rpandey.covid19tracker_india.util.observe
import kotlinx.android.synthetic.main.activity_state_details.*

class StateDetailsActivity : BaseActivity() {

    override fun getScreenName(): String = "StateDetails"

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

    private lateinit var adapter: DistrictListAdapter
    lateinit var binding: ActivityStateDetailsBinding

    private val viewModel: StoreDetailsViewModel by lazy {
        getViewModel { StoreDetailsViewModel(repository) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_state_details)
        adapter = DistrictListAdapter(mutableListOf())
        binding.rvDistrictContainer.adapter = adapter
        observeLiveData()
    }

    private fun observeLiveData() {

        val state = intent.getStringExtra(KEY_STATE)!!
        val stateName = intent.getStringExtra(KEY_STATE_NAME)!!

        with(binding) {
            title.text = stateName
            ivClose.setOnClickListener { finish() }
        }

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

        setupSortClickListeners()
    }

    private fun setupSortClickListeners() {
        val headerViewHelper = HeaderViewHelper(binding.districtHeader, SortOn.NAME to true) { sortOn, ascending ->
            sortData(sortOn, ascending)
        }
        headerViewHelper.addMoreViews(binding.containerDistrictTitle, binding.districtSortArrow, SortOn.NAME)
        headerViewHelper.init()
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
        district_header.visibility = if(data.isEmpty()) View.GONE else View.VISIBLE
        adapter.update(data as MutableList<DistrictEntity>)
    }

    private fun sortData(sortOn: SortOn, ascending: Boolean = true) {
        adapter.data.sortWith(object: Comparator<DistrictEntity> {
            override fun compare(d1: DistrictEntity?, d2: DistrictEntity?): Int {
                if (d1 == null || d2 == null)
                    return 0

                return when(sortOn) {
                    SortOn.NAME -> getCompare(d1.district, d2.district)
                    SortOn.CONFIRMED -> getCompare(d1.totalConfirmed, d2.totalConfirmed)
                    SortOn.ACTIVE -> getCompare(d1.active, d2.active)
                    SortOn.RECOVERED -> getCompare(d1.totalRecovered, d2.totalRecovered)
                    SortOn.DECEASED -> getCompare(d1.totalDeceased, d2.totalDeceased)
                }
            }

            private fun <T: Comparable<T>> getCompare(data1: T, data2: T): Int {
                return if (ascending) data1.compareTo(data2) else data2.compareTo(data1)
            }
        })
        adapter.notifyDataSetChanged()
    }
}