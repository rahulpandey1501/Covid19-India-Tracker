package com.rpandey.covid19tracker_india.ui.bookmark

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import com.rpandey.covid19tracker_india.database.dao.CombinedCasesModel
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity
import com.rpandey.covid19tracker_india.database.entity.StateEntity
import com.rpandey.covid19tracker_india.databinding.FragmentBookmaredBinding
import com.rpandey.covid19tracker_india.databinding.ItemCombinedViewBinding
import com.rpandey.covid19tracker_india.databinding.ItemDistrictCasesBinding
import com.rpandey.covid19tracker_india.ui.BaseFragment
import com.rpandey.covid19tracker_india.ui.dashboard.SelectStateBottomSheet
import com.rpandey.covid19tracker_india.ui.search.SearchDistrictActivity
import com.rpandey.covid19tracker_india.util.Util
import com.rpandey.covid19tracker_india.util.getViewModel
import com.rpandey.covid19tracker_india.util.observe
import com.rpandey.covid19tracker_india.util.showDialog

class BookmarkedFragment : BaseFragment(),
    SelectStateBottomSheet.Callback {

    companion object {
        const val TAG = "BookmarkedFragment"
    }

    private val searchRequestCode = 100
    private lateinit var binding: FragmentBookmaredBinding
    private val districtCancelViews = mutableListOf<View>()
    private val statesCancelVies = mutableListOf<View>()

    private val viewModel: BookmarkViewModel by lazy {
        getViewModel {
            BookmarkViewModel(
                repository
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBookmaredBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun observeLiveData() {

        binding.addState.setOnClickListener {
            showStateBottomsheet()
        }

        binding.addDistrict.setOnClickListener {
            openDistrictSearchFragment()
        }

        binding.removeState.setOnClickListener {
            statesCancelVies.forEach { it.visibility = if(it.visibility == View.VISIBLE) View.GONE else View.VISIBLE }
        }

        binding.removeDistrict.setOnClickListener {
            districtCancelViews.forEach { it.visibility = if(it.visibility == View.VISIBLE) View.GONE else View.VISIBLE }
        }

        viewModel.getBookmarkedDistricts().observe(this) {
            inflateBookmarkedDistricts(it)
        }

        viewModel.getBookmarkedCombinedCases().observe(this) {
            inflateBookmarkedCombined(it)
        }
    }

    private fun showStateBottomsheet() {
        showDialog(SelectStateBottomSheet.TAG) {
            SelectStateBottomSheet()
        }
    }

    override fun onSateSelected(stateEntity: StateEntity) {
        viewModel.onSateSelected(stateEntity)
    }

    private fun openDistrictSearchFragment() {
        startActivityForResult(
            Intent(activity, SearchDistrictActivity::class.java), searchRequestCode
        )
    }

    private fun inflateBookmarkedDistricts(data: List<DistrictEntity>) {
        districtCancelViews.clear()
        binding.districtContainer.removeAllViews()
        binding.removeDistrict.visibility = if (data.isEmpty()) View.GONE else View.VISIBLE

        data.forEach { district ->
            ItemDistrictCasesBinding.inflate(LayoutInflater.from(requireContext()), binding.districtContainer, true).apply {
                val param = root.layoutParams as GridLayout.LayoutParams
                param.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                root.layoutParams = param

                tvTitle.text = district.district
                tvCount.text = Util.formatNumber(district.totalConfirmed)

                ivCancel.setOnClickListener {
                    viewModel.onDistrictRemoved(district.districtId)
                }
                districtCancelViews.add(ivCancel)
            }
        }
    }

    private fun inflateBookmarkedCombined(data: List<CombinedCasesModel>) {
        binding.statesContainer.removeAllViews()
        binding.removeState.visibility = if (data.isEmpty()) View.GONE else View.VISIBLE
        binding.header.visibility = if (data.isEmpty()) View.GONE else View.VISIBLE

        data.forEach { model ->
            ItemCombinedViewBinding.inflate(LayoutInflater.from(requireContext()), binding.statesContainer, true).apply {

                tvActive.text = Util.formatNumber(model.activeCases)

                tvConfirmed.text = Util.formatNumber(model.totalConfirmedCases)
                setDelta(tvConfirmedDelta, model.confirmedCases)

                tvRecovered.text = Util.formatNumber(model.totalRecoveredCases)
                setDelta(tvRecoveredDelta, model.recoveredCases)

                tvDeath.text = Util.formatNumber(model.totalDeceasedCases)
                setDelta(tvDeathDelta, model.deceasedCases)

                tvTitle.text = model.stateName

                ivCancel.setOnClickListener {
                    viewModel.onCombinedCasesBookmarkRemoved(model)
                }

                statesCancelVies.add(ivCancel)
            }
        }
    }

    private fun setDelta(textView: TextView, count: Int) {
        if (count > 0) {
            textView.text = Util.formatNumber(count)
        } else {
            textView.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == searchRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getIntExtra(SearchDistrictActivity.KEY_DISTRICT_ID, -1)?.let {
                viewModel.onDistrictSelected(it)
            }
        }
    }
}