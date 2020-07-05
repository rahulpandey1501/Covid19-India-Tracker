package com.rpandey.covid19tracker_india.ui.districtdetails

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.data.Constants
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity
import com.rpandey.covid19tracker_india.database.model.CountModel
import com.rpandey.covid19tracker_india.databinding.ActivityDistrictDetailsBinding
import com.rpandey.covid19tracker_india.ui.BaseActivity
import com.rpandey.covid19tracker_india.ui.essentials.EssentialsFragment
import com.rpandey.covid19tracker_india.ui.home.ItemCountCaseBindingModel
import com.rpandey.covid19tracker_india.ui.home.UICaseType
import com.rpandey.covid19tracker_india.util.Util
import com.rpandey.covid19tracker_india.util.attachFragment
import com.rpandey.covid19tracker_india.util.getViewModel
import com.rpandey.covid19tracker_india.util.observe
import kotlinx.android.synthetic.main.activity_district_details.*
import kotlinx.android.synthetic.main.layout_cases_count.*
import kotlinx.android.synthetic.main.layout_district_rank_meta.*

class DistrictDetailsActivity : BaseActivity() {

    override fun getScreenName(): String = "DistrictDetails"

    companion object {
        const val KEY_DISTRICT_ID = "DISTRICT_ID"

        fun getIntent(sourceActivity: FragmentActivity, districtId: Int): Intent {
            return Intent(sourceActivity, DistrictDetailsActivity::class.java).apply {
                putExtra(KEY_DISTRICT_ID, districtId)
            }
        }
    }

    private var districtEntity: DistrictEntity? = null
    lateinit var binding: ActivityDistrictDetailsBinding

    private val viewModel: DistrictDetailsViewModel by lazy {
        getViewModel { DistrictDetailsViewModel(repository) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_district_details)
        observeLiveData()
    }

    private fun observeLiveData() {

        val districtId = intent.getIntExtra(KEY_DISTRICT_ID, 0)
        viewModel.init(districtId)

        iv_close.setOnClickListener { finish() }

        more_info.setOnClickListener {
            loadDistrictMoreInfo()
            if (essential_info.visibility == View.GONE) {
                extra_info_container.animate()
                    .alpha(0f)
                    .setDuration(1000)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            extra_info_container.visibility = View.GONE
                        }
                    })
            }

            essential_container.visibility = View.GONE
            more_info_container.visibility = View.VISIBLE
        }

        essential_info.setOnClickListener {
            districtEntity?.let {
                loadEssentials(it.stateName, it.district)
            }

            more_info_container.visibility = View.GONE
            essential_container.visibility = View.VISIBLE
        }

        iv_share.setOnClickListener {
            logEvent("DISTRICT_STATS_CLICKED")
            Util.shareScreenshot(screen_shot_layout)
        }

        viewModel.getDistrictInfo.observe(this) {
            this.districtEntity = it
            binding.title.text = it.district
            binding.stateName.text = it.stateName
            generateUiCaseMode(it)
            setZoneUI(it.zone)
            checkForEssentialData(it.stateName, it.district)
        }

        viewModel.lastUpdatedTime(districtId).observe(this) {
            val title = String.format(getString(R.string.last_updated), it)
            binding.lastUpdate.text = title
        }

        viewModel.getMetaInfo.observe(this) {
            val spanSize = 16f

            val spanPlaceholder = Util.getPercentage(it.totalCases, it.totalCasesByState)
            val deltaPercentageText = String.format(
                getString(R.string.district_state_delta_percentage), spanPlaceholder, it.stateName
            )
            percentage_meta.text = Util.setTextSpan(deltaPercentageText, Util.dpToPx(spanSize), spanPlaceholder)

            val stateSpanPlaceholder = "${it.positionByState}"
            val overallSpanPlaceholder = "${it.positionByOverall}"
            val districtDeltaPositionText = String.format(
                getString(R.string.district_rank_combined), stateSpanPlaceholder, it.stateName, overallSpanPlaceholder
            )
            state_delta_meta.text = Util.setTextSpan(districtDeltaPositionText, Util.dpToPx(spanSize), stateSpanPlaceholder, overallSpanPlaceholder)

            val testingSpanPlaceholder = Util.getPercentage(it.totalCases, it.totalTesting)
            if (testingSpanPlaceholder.isNotEmpty()) {
                testing_meta.visibility = View.VISIBLE
                val testingMetaText = String.format(getString(R.string.district_state_meta_testing), testingSpanPlaceholder)
                testing_meta.text = Util.setTextSpan(
                    testingMetaText,
                    Util.dpToPx(spanSize),
                    testingSpanPlaceholder
                )
            }
        }
    }

    private fun setZoneUI(zone: String?) {
        if (zone.isNullOrEmpty()) {
            indicator_layout.visibility = View.GONE

        } else {
            val color = Util.getZoneColor(this, zone)
            Util.setTint(binding.ivZone, color)
            binding.tvZone.setTextColor(color)
            binding.tvZone.text = "$zone Zone"
        }
    }

    private fun generateUiCaseMode(district: DistrictEntity) {
        val uiCaseMap = mapOf(
            UICaseType.TYPE_CONFIRMED to CountModel(district.confirmed, district.totalConfirmed),
            UICaseType.TYPE_ACTIVE to CountModel(district.getCurrentActive(), district.getActive()),
            UICaseType.TYPE_RECOVERED to CountModel(district.recovered, district.totalRecovered),
            UICaseType.TYPE_DEATH to CountModel(district.deceased, district.totalDeceased),
            UICaseType.TYPE_TESTING to CountModel(district.tested, district.totalTested)
        )

        uiCaseMap.forEach { (uiCase, _) ->
            setUiCaseModel(uiCase, uiCaseMap)
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

    private fun checkForEssentialData(stateName: String, district: String) {
        viewModel.hasResources(stateName, district).observe(this) {
            if (it.isNotEmpty()) {
                essential_info.visibility = View.VISIBLE
                essential_info.performClick()
            }
        }
    }

    private fun loadEssentials(stateName: String, district: String) {
        logEvent("DISTRICT_ESSENTIALS_CLICKED")
        attachFragment(EssentialsFragment.TAG, R.id.essential_container, false) {
            EssentialsFragment.newInstance(stateName, district, true)
        }
    }

    private fun loadDistrictMoreInfo() {
        if (webview.visibility == View.VISIBLE) return

        logEvent("DISTRICT_INFO_CLICKED")
        val urlPlaceholder = Util.getConfig()?.districtInfoUrlPlaceholder ?: Constants.DEFAULT_DISTRICT_INFO_PLACEHOLDER
        val url = String.format(urlPlaceholder, binding.title.text)

        webview.visibility = View.VISIBLE
        webview.webViewClient = WebViewClient()
        webview.webChromeClient = WebChromeClient()
        webview.loadUrl(url)
    }

    override fun onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack()
            return
        }

        super.onBackPressed()
    }

    private fun districtMoreInfo() {
        logEvent("DISTRICT_INFO_CLICKED")
        val urlPlaceholder = Util.getConfig()?.districtInfoUrlPlaceholder ?: Constants.DEFAULT_DISTRICT_INFO_PLACEHOLDER
        Util.openWebUrl(this, String.format(urlPlaceholder, binding.title.text))
    }
}