package com.rpandey.covid19tracker_india.ui.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.database.dao.CombinedCasesModel
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity
import com.rpandey.covid19tracker_india.databinding.LayoutSearchActivityBinding
import com.rpandey.covid19tracker_india.ui.BaseActivity
import com.rpandey.covid19tracker_india.ui.districtdetails.DistrictDetailsActivity
import com.rpandey.covid19tracker_india.ui.statedetails.StateDetailsActivity
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

    private lateinit var adapter: SearchListAdapter
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
        val viewType = getViewType()!!

        if (viewType == DISTRICT_SEARCH_VIEW) {
            recycler_view.layoutManager = GridLayoutManager(this, 3)
            et_search.hint = getString(R.string.search_city)

        } else if (viewType == OVERALL_SEARCH_VIEW) {
            recycler_view.layoutManager = LinearLayoutManager(this)
            et_search.hint = getString(R.string.search_city_state)
        }

        adapter = SearchListAdapter(
            viewType, emptyList(), emptyList(), { onDistrictSelected(it) }, { onStateSelected(it) }
        )

        val controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down)
        recycler_view.layoutAnimation = controller
        recycler_view.adapter = adapter
    }

    private fun getViewType() = intent.getStringExtra(KEY_VIEW_TYPE)

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun observeLiveData() {
        val viewType = intent.getStringExtra(KEY_VIEW_TYPE)

        binding.ivClose.setOnClickListener { finish() }

        binding.etSearch.doAfterTextChanged {
            if (viewType == DISTRICT_SEARCH_VIEW) {
                viewModel.getDistrict(it?.toString()?.trim())

            } else if (viewType == OVERALL_SEARCH_VIEW) {
                viewModel.getOverAllSearch(it?.toString()?.trim())
            }
        }

        viewModel.districtSearch.observe(this) {
            adapter.update(it, emptyList())
            runLayoutAnimation()
        }

        viewModel.overAllSearch.observe(this) {
            adapter.update(it.first, it.second)
            runLayoutAnimation()
        }
    }
    private fun runLayoutAnimation() {
        recycler_view.scheduleLayoutAnimation()
    }

    private fun onStateSelected(state: CombinedCasesModel) {
        startActivity(StateDetailsActivity.getIntent(this, state.state, state.stateName))
    }

    private fun onDistrictSelected(district: DistrictEntity) {
        if (getViewType() == DISTRICT_SEARCH_VIEW) {
            val data = Intent().putExtra(
                KEY_DISTRICT_ID, district.districtId
            )
            setResult(Activity.RESULT_OK, data)
            finish()

        } else {
            startActivity(DistrictDetailsActivity.getIntent(this, district.districtId))
        }
    }

    private fun openKeyboard() {
        binding.etSearch.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
    }
}