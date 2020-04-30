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
import com.rpandey.covid19tracker_india.ui.statedetails.StateDetailsActivity
import com.rpandey.covid19tracker_india.util.getViewModel

class ListStatesFragment : BaseFragment() {

    private val viewModel: ListStatesViewModel by lazy {
        getViewModel { ListStatesViewModel(repository) }
    }

    private lateinit var adapter: StatesAdapter
    private lateinit var binding: FragmentStatesDataBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatesDataBinding.inflate(inflater, container, false)
        adapter = StatesAdapter(emptyList()) {
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
            binding.header.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
            adapter.update(it)
        })
    }
}