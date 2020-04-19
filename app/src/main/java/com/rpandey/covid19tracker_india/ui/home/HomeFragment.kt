package com.rpandey.covid19tracker_india.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase
import com.rpandey.covid19tracker_india.databinding.FragmentHomeBinding
import com.rpandey.covid19tracker_india.ui.BaseFragment
import com.rpandey.covid19tracker_india.util.Util.formatNumber
import com.rpandey.covid19tracker_india.util.getViewModel

class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by lazy {
        getViewModel { HomeViewModel(repository) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
//        binding.vm = homeViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun observeLiveData() {

        with(binding) {
            viewModel.getActiveCount().observe(viewLifecycleOwner, Observer {
                it?.let { activeCount.text = formatNumber(it) }
            })

            viewModel.getConfirmedCount().observe(viewLifecycleOwner, Observer {
                it?.let {
                    confirmedCount.text = formatNumber(it.totalCount)
                    confirmedCurrent.text = "+${formatNumber(it.currentCount)}"
                }
            })

            viewModel.getRecoveredCount().observe(viewLifecycleOwner, Observer {
                it?.let {
                    recoveredCount.text = formatNumber(it.totalCount)
                    recoveredCurrent.text = "+${formatNumber(it.currentCount)}"
                }
            })

            viewModel.getDeceasedCount().observe(viewLifecycleOwner, Observer {
                it?.let {
                    deceasedCount.text = formatNumber(it.totalCount)
                    deceasedCurrent.text = "+${formatNumber(it.currentCount)}"
                }
            })
        }
    }
}