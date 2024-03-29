package com.rpandey.covid19tracker_india.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.data.Constants
import com.rpandey.covid19tracker_india.database.model.CountModel
import com.rpandey.covid19tracker_india.databinding.FragmentHomeBinding
import com.rpandey.covid19tracker_india.ui.BaseFragment
import com.rpandey.covid19tracker_india.ui.bookmark.BookmarkedFragment
import com.rpandey.covid19tracker_india.ui.caseshistory.HistoryCasesActivity
import com.rpandey.covid19tracker_india.ui.topcases.TopCasesFragment
import com.rpandey.covid19tracker_india.util.attachChildFragment
import com.rpandey.covid19tracker_india.util.getViewModel
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by lazy {
        getViewModel { HomeViewModel(repository) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        attachChildFragment(BookmarkedFragment.TAG, binding.bookmarkContainer.id, false) {
            BookmarkedFragment()
        }
        attachChildFragment(TopCasesFragment.TAG, binding.topCasesContainer.id, false) {
            TopCasesFragment()
        }
        return binding.root
    }

    override fun setToolbarTitle(): String {
        return getString(R.string.title_india)
    }

    override fun observeLiveData() {
        viewModel.lastUpdatedTime().observe(viewLifecycleOwner, Observer {
            val title = String.format(getString(R.string.last_updated), it)
            setToolbarSubTitle(title)
        })

        viewModel.getCount().observe(viewLifecycleOwner, Observer {
            it.keys.forEach { uiCase ->
                setUiCaseModel(uiCase, it)
            }
        })

        iv_history.setOnClickListener {
            HistoryCasesActivity.fireIntent(requireActivity(), Constants.STATE_TOTAL_CASE)
        }
    }

    private fun setUiCaseModel(caseType: UICaseType, allCases: Map<UICaseType, CountModel>) {
        with(binding.casesLayout) {
            val itemModel = ItemCountCaseBindingModel(requireContext().applicationContext)
            itemModel.init(caseType, allCases)
            when (caseType) {
                UICaseType.TYPE_CONFIRMED -> confirmVm = itemModel
                UICaseType.TYPE_ACTIVE -> activeVm = itemModel
                UICaseType.TYPE_RECOVERED -> recoverVm = itemModel
                UICaseType.TYPE_DEATH -> deathVm = itemModel
                UICaseType.TYPE_TESTING -> testingVm = itemModel
                UICaseType.TYPE_VACCINATION -> vaccinationVm = itemModel
            }
        }
    }
}