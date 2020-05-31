package com.rpandey.covid19tracker_india.ui.topcases

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity
import com.rpandey.covid19tracker_india.databinding.ItemDistrictCasesMinimalBinding
import com.rpandey.covid19tracker_india.ui.BaseFragment
import com.rpandey.covid19tracker_india.ui.districtdetails.DistrictDetailsActivity
import com.rpandey.covid19tracker_india.ui.statedetails.StateDetailsActivity
import com.rpandey.covid19tracker_india.util.GridViewInflater
import com.rpandey.covid19tracker_india.util.Util
import com.rpandey.covid19tracker_india.util.getViewModel
import com.rpandey.covid19tracker_india.util.observe
import kotlinx.android.synthetic.main.fragment_top_cases.*

class TopCasesFragment : BaseFragment() {

    companion object {
        const val TAG = "TopCasesFragment"
    }

    private val viewModel: TopCasesViewModel by lazy {
        getViewModel { TopCasesViewModel(repository) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_top_cases, container, false)
    }

    override fun observeLiveData() {
        viewModel.topCases.observe(this) { data ->
            GridViewInflater(3, top_district_container) {
                data.forEach { data ->
                    val binding: ItemDistrictCasesMinimalBinding = addView(R.layout.item_district_cases_minimal)
                    binding.apply {
                        tvTitle.text = data.name
                        tvCount.text = Util.formatNumber(data.totalConfirmed)
                        root.setOnClickListener {
                            if (data.type == TopCasesViewModel.Type.DISTRICT)
                                openDistrictDetailsView(data)
                            else
                                openStateDetailsView(data)

                        }
                    }
                }
            }
        }
    }

    private fun openDistrictDetailsView(dataItem: TopCasesViewModel.DataItem) {
        startActivity(DistrictDetailsActivity.getIntent(requireActivity(),
            dataItem.typeIdentifier as Int
        ))
    }

    private fun openStateDetailsView(dataItem: TopCasesViewModel.DataItem) {
        startActivity(StateDetailsActivity.getIntent(requireActivity(),
            dataItem.typeIdentifier as String, dataItem.name))
    }
}