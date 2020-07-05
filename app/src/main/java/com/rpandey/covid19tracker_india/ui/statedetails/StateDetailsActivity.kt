package com.rpandey.covid19tracker_india.ui.statedetails

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.data.Constants
import com.rpandey.covid19tracker_india.data.model.covidIndia.Zone
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity
import com.rpandey.covid19tracker_india.database.entity.StateEntity
import com.rpandey.covid19tracker_india.database.model.CountModel
import com.rpandey.covid19tracker_india.databinding.ActivityStateDetailsBinding
import com.rpandey.covid19tracker_india.ui.BaseActivity
import com.rpandey.covid19tracker_india.ui.common.HeaderViewHelper
import com.rpandey.covid19tracker_india.ui.common.SortOn
import com.rpandey.covid19tracker_india.ui.common.ViewSortModel
import com.rpandey.covid19tracker_india.ui.dashboard.SelectStateBottomSheet
import com.rpandey.covid19tracker_india.ui.districtdetails.DistrictDetailsActivity
import com.rpandey.covid19tracker_india.ui.home.ItemCountCaseBindingModel
import com.rpandey.covid19tracker_india.ui.home.UICaseType
import com.rpandey.covid19tracker_india.util.Util
import com.rpandey.covid19tracker_india.util.getViewModel
import com.rpandey.covid19tracker_india.util.observe
import com.rpandey.covid19tracker_india.util.showDialog
import kotlinx.android.synthetic.main.activity_state_details.*
import kotlinx.android.synthetic.main.item_zone_distribution.view.*

class StateDetailsActivity : BaseActivity(), SelectStateBottomSheet.Callback {

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

    private val viewModel: StateDetailsViewModel by lazy {
        getViewModel { StateDetailsViewModel(repository) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_state_details)
        adapter = DistrictListAdapter(mutableListOf()) {
            openDistrictDetailsView(it)
        }
        binding.rvDistrictContainer.adapter = adapter
        val state = intent.getStringExtra(KEY_STATE)!!
        val stateName = intent.getStringExtra(KEY_STATE_NAME)!!
        observeLiveData(state, stateName)
    }

    override fun onSateSelected(stateEntity: StateEntity) {
        observeLiveData(stateEntity.code, stateEntity.name)
    }

    private fun observeLiveData(stateCode: String, stateName: String) {

        with(binding) {
            title.text = "$stateName ⇌"
            title.setOnClickListener { stateChangeClicked() }
            ivClose.setOnClickListener { finish() }
            ivShare.setOnClickListener {
                val rootView = window.decorView.findViewById<View>(android.R.id.content)
                logEvent("STATE_STATS_CLICKED")
                Util.shareScreenshot(rootView)
            }
        }

        viewModel.lastUpdatedTime(stateCode).observe(this) {
            val title = String.format(getString(R.string.last_updated), it)
            binding.lastUpdate.text = title
        }

        viewModel.getCount(stateCode, stateName).observe(this) {
            it.keys.forEach { uiCase ->
                setUiCaseModel(uiCase, it)
            }
        }

        viewModel.getDistricts(stateName).observe(this) {
            inflateDistricts(it)
            inflateZoneDistribution(it)
        }

        setupSortClickListeners()
    }

    private fun setupSortClickListeners() {
        with(binding.districtHeader) {
            val headerArrowViews = mutableListOf(
                ViewSortModel(headerConfirmed, confirmSortArrow, SortOn.CONFIRMED),
                ViewSortModel(headerActive, activeSortArrow, SortOn.ACTIVE),
                ViewSortModel(headerRecovered, recoverSortArrow, SortOn.RECOVERED),
                ViewSortModel(headerDeaths, deathSortArrow, SortOn.DECEASED),
                ViewSortModel(binding.containerDistrictTitle, binding.districtSortArrow, SortOn.NAME)
            )
            HeaderViewHelper(headerArrowViews, SortOn.NAME to true) { sortOn, ascending ->
                sortData(sortOn, ascending)
            }.init()
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
        district_header.visibility = if(data.isEmpty()) View.GONE else View.VISIBLE
        container_district_title.visibility = district_header.visibility

        adapter.update(data as MutableList<DistrictEntity>)
    }

    private fun inflateZoneDistribution(data: List<DistrictEntity>) {
        zone_distribution_container.removeAllViews()

        var orangeCount = 0
        var redCount = 0
        var greenCount = 0

        data.forEach {
            when(it.zone) {
                Zone.Green.name -> { ++greenCount }
                Zone.Orange.name -> { ++orangeCount }
                Zone.Red.name -> { ++redCount }
            }
        }
        val totalZoneCount = orangeCount + greenCount + redCount
        inflateZoneItem(Zone.Red, redCount, totalZoneCount)
        inflateZoneItem(Zone.Orange, orangeCount, totalZoneCount)
        inflateZoneItem(Zone.Green, greenCount, totalZoneCount)
    }

    private fun inflateZoneItem(zone: Zone, currentZoneCount: Int, totalZoneCount: Int) {
        val inflater = LayoutInflater.from(this)
        val zoneItem = inflater.inflate(R.layout.item_zone_distribution, zone_distribution_container, false)
        zoneItem.indicator.setBackgroundColor(Util.getZoneColor(this, zone.name))
        zoneItem.distribution.text = Util.getPercentage(currentZoneCount, totalZoneCount)
        val param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            1.0f
        )
        param.marginStart = Util.dpToPx(10f)
        zoneItem.layoutParams = param
        zoneItem.tv_zone.text = "${zone.name} zone"
        zone_distribution_container.addView(zoneItem)
    }

    private fun openDistrictDetailsView(district: DistrictEntity) {
        startActivity(DistrictDetailsActivity.getIntent(this, district.districtId))
    }

    private fun sortData(sortOn: SortOn, ascending: Boolean = true) {
        adapter.data.sortWith(object: Comparator<DistrictEntity> {
            override fun compare(d1: DistrictEntity?, d2: DistrictEntity?): Int {
                if (d1 == null || d2 == null)
                    return 0

                return when(sortOn) {
                    SortOn.NAME -> getCompare(d1.district, d2.district)
                    SortOn.CONFIRMED -> getCompare(d1.totalConfirmed, d2.totalConfirmed)
                    SortOn.ACTIVE -> getCompare(d1.getActive(), d2.getActive())
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

    private fun stateChangeClicked() {
        logEvent("STATE_CHANGE_CLICKED")
        showDialog(SelectStateBottomSheet.TAG) {
            SelectStateBottomSheet.newInstance()
        }
    }

    private fun stateMoreInfo(stateCode: String) {
        logEvent("STATE_INFO_CLICKED")
        val urlPlaceholder = Util.getConfig()?.stateInfoUrlPlaceholder ?: Constants.DEFAULT_STATE_INFO_PLACEHOLDER
        Util.openWebUrl(this, String.format(urlPlaceholder, stateCode))
    }
}