package com.rpandey.covid19tracker_india.ui.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.database.dao.CombinedCasesModel
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity
import com.rpandey.covid19tracker_india.databinding.ItemCombinedViewDistrictBinding
import com.rpandey.covid19tracker_india.databinding.ItemDistrictCasesMinimalBinding
import com.rpandey.covid19tracker_india.databinding.LayoutSearchActivityBinding
import com.rpandey.covid19tracker_india.ui.BaseActivity
import com.rpandey.covid19tracker_india.ui.districtdetails.DistrictDetailsActivity
import com.rpandey.covid19tracker_india.ui.statedetails.StateDetailsActivity
import com.rpandey.covid19tracker_india.util.GridViewInflater
import com.rpandey.covid19tracker_india.util.Util
import com.rpandey.covid19tracker_india.util.getViewModel
import com.rpandey.covid19tracker_india.util.observe
import kotlinx.android.synthetic.main.layout_search_activity.*


class SearchActivity : BaseActivity() {

    override fun getScreenName(): String = "SearchPage"

    companion object {
        const val KEY_DISTRICT_ID = "KEY_DISTRICT_ID"
        const val KEY_VIEW_TYPE = "KEY_VIEW_TYPE"
        const val OVERALL_SEARCH_VIEW = "OVERALL_SEARCH_VIEW"
        const val DISTRICT_SEARCH_VIEW = "DISTRICT_SEARCH_VIEW"
    }

    private lateinit var binding: LayoutSearchActivityBinding

    private val viewModel: SearchDistrictViewModel by lazy {
        getViewModel { SearchDistrictViewModel(repository) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.layout_search_activity)
        readBundle()
        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        observeLiveData()
        openKeyboard()
    }

    private fun readBundle() {
        val viewType = intent.getStringExtra(KEY_VIEW_TYPE)

        if (viewType == DISTRICT_SEARCH_VIEW) {
            et_search.hint = getString(R.string.search_city)

        } else if (viewType == OVERALL_SEARCH_VIEW) {
            et_search.hint = getString(R.string.search_city_state)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun observeLiveData() {
        val viewType = intent.getStringExtra(KEY_VIEW_TYPE)

        binding.etSearch.doAfterTextChanged {
            if (viewType == DISTRICT_SEARCH_VIEW) {
                viewModel.getDistrict(it?.toString()?.trim())

            } else if (viewType == OVERALL_SEARCH_VIEW) {
                viewModel.getOverAllSearch(it?.toString()?.trim())
            }
        }

        viewModel.districtSearch.observe(this) {
            inflateDistricts(it)
        }

        viewModel.overAllSearch.observe(this) {
            inflateOverallResult(it.first, it.second)
        }
    }

    private fun inflateDistricts(districts: List<DistrictEntity>) {
        binding.searchContainer.removeAllViews()
        GridViewInflater(3, binding.searchContainer) {
            districts.forEach { district ->
                val binding: ItemDistrictCasesMinimalBinding = addView(R.layout.item_district_cases_minimal)
                binding.tvTitle.text = district.district
                binding.tvCount.text = Util.formatNumber(district.getActive())
                binding.zoneIndicator.setBackgroundColor(Util.getZoneColor(this@SearchActivity, district.zone))
                binding.root.setOnClickListener {
                    onDistrictSelected(district)
                }
            }
        }
    }

    private fun inflateOverallResult(districts: List<DistrictEntity>, states: List<CombinedCasesModel>) {
        binding.searchContainer.removeAllViews()
        GridViewInflater(1, binding.searchContainer) {

            districts.forEach { district ->
                addView<ItemCombinedViewDistrictBinding>(R.layout.item_combined_view_district).apply {
                    fillOverallView(this, district.district, district.getActive(), district.confirmed,
                        district.totalConfirmed, district.recovered, district.totalRecovered, district.deceased, district.totalDeceased)
                    root.setOnClickListener {
                        openDistrictDetails(district)
                    }
                }
            }

            states.forEach { state ->
                addView<ItemCombinedViewDistrictBinding>(R.layout.item_combined_view_district).apply {
                    fillOverallView(this, state.stateName, state.activeCases, state.confirmedCases,
                        state.totalConfirmedCases, state.recoveredCases, state.totalRecoveredCases, state.deceasedCases, state.totalDeceasedCases)
                    root.setOnClickListener {
                        openStateDetails(state)
                    }
                }
            }
        }
    }

    private fun fillOverallView(
        binding: ItemCombinedViewDistrictBinding,
        title: String,
        activeCases: Int,
        confirmedCases: Int,
        totalConfirmedCases: Int,
        recoveredCases: Int,
        totalRecoveredCases: Int,
        deceasedCases: Int,
        totalDeceasedCases: Int) {

        with(binding) {
            tvTitle.text = title
            tvActive.text = Util.formatNumber(activeCases)

            tvConfirmed.text = Util.formatNumber(totalConfirmedCases)
            setDelta(tvConfirmedDelta, confirmedCases)

            tvRecovered.text = Util.formatNumber(totalRecoveredCases)
            setDelta(tvRecoveredDelta, recoveredCases)

            tvDeath.text = Util.formatNumber(totalDeceasedCases)
            setDelta(tvDeathDelta, deceasedCases)
        }
    }

    private fun setDelta(textView: TextView, count: Int) {
        if (count > 0) {
            textView.visibility = View.VISIBLE
            textView.text = String.format("+%s", Util.formatNumber(count))
        } else {
            textView.visibility = View.INVISIBLE
        }
    }

    private fun onDistrictSelected(districtEntity: DistrictEntity) {
        val data = Intent().putExtra(
            KEY_DISTRICT_ID, districtEntity.districtId
        )
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun openStateDetails(state: CombinedCasesModel) {
        startActivity(StateDetailsActivity.getIntent(this, state.state, state.stateName))
    }

    private fun openDistrictDetails(district: DistrictEntity) {
        startActivity(DistrictDetailsActivity.getIntent(this, district.districtId))
    }

    private fun openKeyboard() {
        binding.etSearch.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
    }
}