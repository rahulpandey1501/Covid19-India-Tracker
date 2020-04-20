package com.rpandey.covid19tracker_india.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.databinding.FragmentHomeBinding
import com.rpandey.covid19tracker_india.ui.BaseFragment
import com.rpandey.covid19tracker_india.ui.bookmark.BookmarkedFragment
import com.rpandey.covid19tracker_india.util.Util.formatNumber
import com.rpandey.covid19tracker_india.util.attachChildFragment
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
        attachChildFragment(BookmarkedFragment.TAG, R.id.bookmark_container, false) {
            BookmarkedFragment()
        }
    }

    override fun setToolbarTitle(): String {
        return getString(R.string.title_home)
    }

    override fun observeLiveData() {

        with(binding) {
            viewModel.getActiveCount().observe(viewLifecycleOwner, Observer {
                it?.let { activeCount.text = formatNumber(it) }
            })

            viewModel.getConfirmedCount().observe(viewLifecycleOwner, Observer {
                it?.let {
                    confirmedCount.text = formatNumber(it.totalCount)
                    setDelta(confirmedCurrent, it.currentCount)
                }
            })

            viewModel.getRecoveredCount().observe(viewLifecycleOwner, Observer {
                it?.let {
                    recoveredCount.text = formatNumber(it.totalCount)
                    setDelta(recoveredCurrent, it.currentCount)
                }
            })

            viewModel.getDeceasedCount().observe(viewLifecycleOwner, Observer {
                it?.let {
                    deceasedCount.text = formatNumber(it.totalCount)
                    setDelta(deceasedCurrent, it.currentCount)
                }
            })

            viewModel.lastUpdatedTime().observe(viewLifecycleOwner, Observer {
                val title = String.format("%s %s", "Last updated: ", it)
                (activity as AppCompatActivity?)?.supportActionBar?.subtitle = title
            })
        }
    }

    private fun setDelta(textView: TextView, count: Int) {
        if (count > 0) {
            textView.visibility = View.VISIBLE
            textView.text = formatNumber(count)
        } else {
            textView.visibility = View.GONE
        }
    }
}