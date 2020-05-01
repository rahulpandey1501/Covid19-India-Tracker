package com.rpandey.covid19tracker_india.ui.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity
import com.rpandey.covid19tracker_india.databinding.ItemDistrictCasesMinimalBinding
import com.rpandey.covid19tracker_india.databinding.LayoutSearchActivityBinding
import com.rpandey.covid19tracker_india.ui.BaseActivity
import com.rpandey.covid19tracker_india.util.GridViewInflater
import com.rpandey.covid19tracker_india.util.Util
import com.rpandey.covid19tracker_india.util.getViewModel
import com.rpandey.covid19tracker_india.util.observe


class SearchDistrictActivity : BaseActivity() {

    companion object {
        const val KEY_DISTRICT_ID = "KEY_DISTRICT_ID"
    }

    private lateinit var binding: LayoutSearchActivityBinding

    private val viewModel: SearchDistrictViewModel by lazy {
        getViewModel { SearchDistrictViewModel(repository) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.layout_search_activity)
        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        observeLiveData()
        openKeyboard()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun observeLiveData() {
        binding.etDistrict.doAfterTextChanged {
            viewModel.getDistrict(it?.toString()?.trim())
        }

        viewModel.districtSearch.observe(this) {
            inflateDistricts(it)
        }
    }

    private fun inflateDistricts(districts: List<DistrictEntity>) {
        binding.districtContainer.removeAllViews()
        val gridViewInflater = GridViewInflater(3, binding.districtContainer)
        districts.forEach { district ->
            val binding: ItemDistrictCasesMinimalBinding = gridViewInflater.addView(R.layout.item_district_cases_minimal)
            binding.tvTitle.text = district.district
            binding.tvCount.text = Util.formatNumber(district.totalConfirmed)
            binding.root.setOnClickListener {
                onDistrictSelected(district)
            }
        }
    }

    private fun onDistrictSelected(districtEntity: DistrictEntity) {
        val data = Intent().putExtra(
            KEY_DISTRICT_ID, districtEntity.districtId
        )
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun openKeyboard() {
        binding.etDistrict.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etDistrict, InputMethodManager.SHOW_IMPLICIT)
    }
}