package com.rpandey.covid19tracker_india.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.database.dao.CombinedCasesModel
import com.rpandey.covid19tracker_india.databinding.FragmentStatesDataBinding
import com.rpandey.covid19tracker_india.ui.BaseFragment
import com.rpandey.covid19tracker_india.ui.common.HeaderViewHelper
import com.rpandey.covid19tracker_india.ui.common.SortOn
import com.rpandey.covid19tracker_india.ui.statedetails.StateDetailsActivity
import com.rpandey.covid19tracker_india.util.getViewModel
import kotlinx.android.synthetic.main.fragment_states_data.*

class StateListFragment : BaseFragment() {

    override fun getScreenName(): String? = "StateListTracker"

    private val viewModel: StateListViewModel by lazy {
        getViewModel { StateListViewModel(repository) }
    }

    private lateinit var adapter: StatesAdapter
    private lateinit var binding: FragmentStatesDataBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatesDataBinding.inflate(inflater, container, false)
        adapter = StatesAdapter(mutableListOf()) {
            openStateDetailsActivity(it)
        }
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    private fun openStateDetailsActivity(model: CombinedCasesModel) {
        startActivity(StateDetailsActivity.getIntent(requireActivity(), model.state, model.stateName))
    }

    override fun setToolbarTitle(): String {
        return getString(R.string.state_tracker)
    }

    override fun observeLiveData() {
        viewModel.getStatesData().observe(viewLifecycleOwner, Observer {
            header.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
            adapter.update(it as MutableList<CombinedCasesModel>)
        })

        setupSortClickListeners()
    }

    private fun setupSortClickListeners() {
        HeaderViewHelper(binding.header, SortOn.CONFIRMED to false) { sortOn, ascending ->
            sortData(sortOn, ascending)
        }.init()
    }

    private fun sortData(sortOn: SortOn, ascending: Boolean = true) {
        adapter.data.sortWith(object: Comparator<CombinedCasesModel> {
            override fun compare(d1: CombinedCasesModel?, d2: CombinedCasesModel?): Int {
                if (d1 == null || d2 == null)
                    return 0

                return when(sortOn) {
                    SortOn.NAME -> getCompare(d1.stateName, d2.stateName)
                    SortOn.CONFIRMED -> getCompare(d1.totalConfirmedCases, d2.totalConfirmedCases)
                    SortOn.ACTIVE -> getCompare(d1.activeCases, d2.activeCases)
                    SortOn.RECOVERED -> getCompare(d1.totalRecoveredCases, d2.totalRecoveredCases)
                    SortOn.DECEASED -> getCompare(d1.totalDeceasedCases, d2.totalDeceasedCases)
                }
            }

            private fun <T: Comparable<T>> getCompare(data1: T, data2: T): Int {
                return if (ascending) data1.compareTo(data2) else data2.compareTo(data1)
            }
        })
        adapter.notifyDataSetChanged()
    }
}