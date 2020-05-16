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
import com.google.gson.Gson
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.data.Constants
import com.rpandey.covid19tracker_india.data.model.Config
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity
import com.rpandey.covid19tracker_india.database.model.CountModel
import com.rpandey.covid19tracker_india.databinding.ActivityDistrictDetailsBinding
import com.rpandey.covid19tracker_india.ui.BaseActivity
import com.rpandey.covid19tracker_india.ui.home.ItemCountCaseBindingModel
import com.rpandey.covid19tracker_india.ui.home.UICaseType
import com.rpandey.covid19tracker_india.util.PreferenceHelper
import com.rpandey.covid19tracker_india.util.Util
import com.rpandey.covid19tracker_india.util.getViewModel
import com.rpandey.covid19tracker_india.util.observe
import kotlinx.android.synthetic.main.activity_district_details.*
import kotlinx.android.synthetic.main.layout_cases_count.*

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

        testing_layout.visibility = View.GONE
        iv_close.setOnClickListener { finish() }

        more_info.setOnClickListener {
            loadDistrictMoreInfo()
            it.animate()
                .alpha(0f)
                .setDuration(1000)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        it.visibility = View.GONE
                    }
                })
        }

        viewModel.getDistrict(districtId).observe(this) {
            binding.title.text = it.district
            generateUiCaseMode(it)
            setZoneUI(it.zone)
        }

        viewModel.lastUpdatedTime(districtId).observe(this) {
            val title = String.format(getString(R.string.last_updated), it)
            binding.lastUpdate.text = title
        }
    }

    private fun setZoneUI(zone: String?) {
        if (zone.isNullOrEmpty()) {
            indicator_layout.visibility = View.GONE

        } else {
            val color = Util.getZoneColor(this, zone)
            binding.ivZone.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN)
            binding.tvZone.setTextColor(color)
            binding.tvZone.text = "$zone Zone"
        }
    }

    private fun generateUiCaseMode(district: DistrictEntity) {
        val uiCaseMap = mapOf(
            UICaseType.TYPE_CONFIRMED to CountModel(district.confirmed, district.totalConfirmed),
            UICaseType.TYPE_ACTIVE to CountModel(0, district.getActive()),
            UICaseType.TYPE_RECOVERED to CountModel(district.recovered, district.totalRecovered),
            UICaseType.TYPE_DEATH to CountModel(district.deceased, district.totalDeceased)
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

    private fun loadDistrictMoreInfo() {
        val config = PreferenceHelper.getString(Constants.KEY_CONFIG)
        val configModel = config?.let { Gson().fromJson(it, Config::class.java) }
        val urlPlaceholder = configModel?.districtInfoUrlPlaceholder ?: Constants.DEFAULT_DISTRICT_INFO_PLACEHOLDER
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
        val config = PreferenceHelper.getString(Constants.KEY_CONFIG)
        val configModel = config?.let { Gson().fromJson(it, Config::class.java) }
        val urlPlaceholder = configModel?.districtInfoUrlPlaceholder ?: Constants.DEFAULT_DISTRICT_INFO_PLACEHOLDER
        Util.openWebUrl(this, String.format(urlPlaceholder, binding.title.text))
    }
}