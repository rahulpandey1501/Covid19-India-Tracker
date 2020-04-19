package com.rpandey.covid19tracker_india.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity
import com.rpandey.covid19tracker_india.database.entity.StateEntity
import com.rpandey.covid19tracker_india.databinding.FragmentDashboardBinding
import com.rpandey.covid19tracker_india.databinding.ItemDistrictCasesBinding
import com.rpandey.covid19tracker_india.ui.BaseFragment
import com.rpandey.covid19tracker_india.ui.search.SearchDistrictActivity
import com.rpandey.covid19tracker_india.util.Util
import com.rpandey.covid19tracker_india.util.getViewModel
import com.rpandey.covid19tracker_india.util.observe
import com.rpandey.covid19tracker_india.util.showDialog

class DashboardFragment : BaseFragment(), SelectStateBottomSheet.Callback {

    private val searchRequestCode = 100
    private lateinit var binding: FragmentDashboardBinding

    private val viewModel: DashboardViewModel by lazy {
        getViewModel { DashboardViewModel(repository) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun observeLiveData() {

        binding.addState.setOnClickListener {
            showStateBottomsheet()
        }

        binding.addDistrict.setOnClickListener {
            openDistrictSearchFragment()
        }

        viewModel.getBookmarkedDistricts().observe(this) {
            inflateBookmarkedDistricts(it)
        }

        viewModel.getBookmarkedCombinedCases().observe(this) {

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

    private fun inflateBookmarkedDistricts(districts: List<DistrictEntity>) {
        binding.districtContainer.removeAllViews()

        districts.forEach { district ->
            val binding = ItemDistrictCasesBinding.inflate(LayoutInflater.from(requireContext()), binding.districtContainer, true)

            val param = binding.root.layoutParams as GridLayout.LayoutParams
            param.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            param.width = GridLayout.LayoutParams.WRAP_CONTENT
            binding.root.layoutParams = param

            binding.tvTitle.text = district.district
            binding.tvCount.text = Util.formatNumber(district.totalConfirmed)
            binding.root.setOnClickListener {

            }
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