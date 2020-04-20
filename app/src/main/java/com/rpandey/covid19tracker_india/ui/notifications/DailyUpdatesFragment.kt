package com.rpandey.covid19tracker_india.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.databinding.FragmentNotificationsBinding
import com.rpandey.covid19tracker_india.databinding.ItemUpdatesBinding
import com.rpandey.covid19tracker_india.ui.BaseFragment
import com.rpandey.covid19tracker_india.util.getViewModel
import com.rpandey.covid19tracker_india.util.observe

class DailyUpdatesFragment : BaseFragment() {

    private lateinit var binding: FragmentNotificationsBinding

    private val viewModel by lazy {
        getViewModel { DailyUpdatesViewModel(repository) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setToolbarTitle(): String {
        return getString(R.string.title_daily_updates)
    }

    override fun observeLiveData() {
        viewModel.getDailyUpdates().observe(this) {
            binding.noUpdates.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            binding.container.removeAllViews()
            it.forEach { data ->
                val message = getSentence(
                    data.confirmedCases,
                    data.recoveredCases,
                    data.deceasedCases
                )
                val binding = ItemUpdatesBinding.inflate(LayoutInflater.from(requireContext()), binding.container, true)
                binding.title.text = data.stateName
                binding.message.text = message
            }
        }
    }

    private fun getSentence(confirmedCount: Int, recoveredCount: Int, deathCount: Int): String {

        val confirmedText = getConfirmedText(confirmedCount)
        val recoveryText = getRecoveryText(recoveredCount)
        val deathText = getDeathsText(deathCount)

        return if (confirmedCount > 0 && recoveredCount > 0 && deathCount > 0) {
            String.format("%s new $confirmedText, %s $recoveryText and %s $deathText", confirmedCount, recoveredCount, deathCount)

        } else {

            var message = ""

            if (confirmedCount > 0)  {
                message += "$confirmedCount new $confirmedText"
            }

            if (recoveredCount > 0)  {
                message += if (message.isEmpty()) "$recoveredCount $recoveryText" else " and $recoveredCount $recoveryText"
            }

            if (deathCount > 0)  {
                message += if (message.isEmpty()) "$deathCount $deathText" else " and $deathCount $deathText"
            }

            message
        }
    }

    private fun getRecoveryText(count: Int): String {
        return if (count == 1) "recovery" else "recoveries"
    }

    private fun getConfirmedText(count: Int): String {
        return if (count == 1) "case" else "cases"
    }

    private fun getDeathsText(count: Int): String {
        return if (count == 1) "death" else "deaths"
    }
}