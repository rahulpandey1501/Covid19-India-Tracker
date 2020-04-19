package com.rpandey.covid19tracker_india.ui.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity
import com.rpandey.covid19tracker_india.databinding.FragmentSearchDistrictBinding
import com.rpandey.covid19tracker_india.databinding.ItemDistrictCasesBinding
import com.rpandey.covid19tracker_india.ui.BaseFragment
import com.rpandey.covid19tracker_india.ui.dashboard.SelectStateBottomSheet
import com.rpandey.covid19tracker_india.util.Util
import com.rpandey.covid19tracker_india.util.getViewModel
import com.rpandey.covid19tracker_india.util.observe

class SearchDistrictFragment : BaseFragment() {

    companion object {
        const val TAG = "SearchDistrictFragment"
    }

    interface Callback {
        fun onDistrictSelected(districtEntity: DistrictEntity)
    }

    private lateinit var callback: Callback
    private lateinit var binding: FragmentSearchDistrictBinding

    private val viewModel: SearchDistrictViewModel by lazy {
        getViewModel { SearchDistrictViewModel(repository) }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Callback)
            callback = context
        else if (parentFragment is Callback)
            callback = parentFragment as Callback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchDistrictBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun observeLiveData() {
        binding.etDistrict.doAfterTextChanged {
            viewModel.getDistrict(it?.toString()?.trim())
        }

        viewModel.districtSearch.observe(this) {
            inflateDistricts(it)
        }
    }

    private fun inflateDistricts(districts: List<DistrictEntity>) {
        binding.districtContainer.removeAllViews()
        districts.forEach { district ->
            val binding = ItemDistrictCasesBinding.inflate(LayoutInflater.from(requireContext()), binding.districtContainer, true)
            binding.tvTitle.text = district.district
            binding.tvCount.text = Util.formatNumber(district.totalConfirmed)
            binding.root.setOnClickListener {
                callback.onDistrictSelected(district)
                parentFragmentManager.popBackStackImmediate()
            }
        }
    }
}