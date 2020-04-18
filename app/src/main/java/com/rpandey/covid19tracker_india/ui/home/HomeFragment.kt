package com.rpandey.covid19tracker_india.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase
import com.rpandey.covid19tracker_india.databinding.FragmentHomeBinding
import com.rpandey.covid19tracker_india.util.getViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val repository by lazy {
        CovidIndiaRepository(CovidDatabase.getInstance(requireContext()))
    }

    private val homeViewModel: HomeViewModel by lazy {
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
        observeLiveData()
    }

    private fun observeLiveData() {

        with(binding) {
            homeViewModel.getActiveCount().observe(viewLifecycleOwner, Observer {
                activeCount.text = it.toString()
            })

            homeViewModel.getConfirmedCount().observe(viewLifecycleOwner, Observer {
                confirmedCount.text = it.totalCount.toString()
                confirmedCurrent.text = "+${it.currentCount}"
            })

            homeViewModel.getRecoveredCount().observe(viewLifecycleOwner, Observer {
                recoveredCount.text = it.totalCount.toString()
                recoveredCurrent.text = "+${it.currentCount}"
            })

            homeViewModel.getDeceasedCount().observe(viewLifecycleOwner, Observer {
                deceasedCount.text = it.totalCount.toString()
                deceasedCurrent.text = "+${it.currentCount}"
            })
        }
    }
}