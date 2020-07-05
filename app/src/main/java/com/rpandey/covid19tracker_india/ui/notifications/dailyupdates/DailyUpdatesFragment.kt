package com.rpandey.covid19tracker_india.ui.notifications.dailyupdates

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Lifecycle
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity
import com.rpandey.covid19tracker_india.databinding.FragmentNotificationsBinding
import com.rpandey.covid19tracker_india.databinding.ItemUpdatesBinding
import com.rpandey.covid19tracker_india.ui.BaseFragment
import com.rpandey.covid19tracker_india.ui.districtdetails.DistrictDetailsActivity
import com.rpandey.covid19tracker_india.ui.statedetails.StateDetailsActivity
import com.rpandey.covid19tracker_india.util.getViewModel
import com.rpandey.covid19tracker_india.util.observe
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.coroutines.*

class DailyUpdatesFragment : BaseFragment() {

    companion object {
        const val TAG = "DailyUpdatesFragment"
    }

    override fun getScreenName(): String? = "DailyUpdates"

    private lateinit var districtAdapter: NewDistrictCasesAdapter
    private lateinit var binding: FragmentNotificationsBinding

    private val viewModel by lazy {
        getViewModel {
            DailyUpdatesViewModel(
                repository
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        districtAdapter = NewDistrictCasesAdapter(mutableListOf()) {
            openDistrictDetailsView(it)
        }
        binding.districtRv.adapter = districtAdapter
        return binding.root
    }

    override fun setToolbarTitle(): String {
        return getString(R.string.title_daily_updates)
    }

    override fun observeLiveData() {
        viewModel.getDailyStateUpdates().observe(this) {
            binding.noUpdates.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            binding.stateContainer.removeAllViews()
            it.forEach { data ->
                val message = HtmlCompat.fromHtml(getSentence(
                    data.confirmedCases,
                    data.recoveredCases,
                    data.deceasedCases
                ), HtmlCompat.FROM_HTML_MODE_COMPACT)
                val binding = ItemUpdatesBinding.inflate(LayoutInflater.from(requireContext()), binding.stateContainer, true)
                binding.title.text = data.stateName
                binding.message.text = message
                binding.root.setOnClickListener {
                    startActivity(StateDetailsActivity.getIntent(requireActivity(), data.state, data.stateName))
                }
            }
        }

        CoroutineScope(Dispatchers.Default).launch {
            delay(500)
            withContext(Dispatchers.Main) {
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
                    viewModel.getDailyDistrictUpdates().observe(this@DailyUpdatesFragment) {
                        districtAdapter.update(it as MutableList<DistrictEntity>)
                    }
            }
        }

        toggle.setOnClickListener {
            if (district_container.visibility == View.VISIBLE) {
                tv_action_button.text = getString(R.string.districts)

                district_container.animate()
                    .alpha(0f)
                    .setDuration(150)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            district_container.visibility = View.INVISIBLE
                            state_container.visibility = View.VISIBLE
                            state_container.alpha = 1f
                        }
                    })


            } else {
                tv_action_button.text = getString(R.string.states)

                state_container.animate()
                    .alpha(0f)
                    .setDuration(150)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            state_container.visibility = View.INVISIBLE
                            district_container.visibility = View.VISIBLE
                            district_container.alpha = 1f
                        }
                    })
            }
        }
    }

    private fun openDistrictDetailsView(district: DistrictEntity) {
        startActivity(DistrictDetailsActivity.getIntent(requireActivity(), district.districtId))
    }

    private fun getSentence(confirmedCount: Int, recoveredCount: Int, deathCount: Int): String {

        val confirmedText = getConfirmedText(confirmedCount)
        val recoveryText = getRecoveryText(recoveredCount)
        val deathText = getDeathsText(deathCount)

        return if (confirmedCount > 0 && recoveredCount > 0 && deathCount > 0) {
            String.format("<b>%s</b> new $confirmedText, <b>%s</b> $recoveryText and <b>%s</b> $deathText", confirmedCount, recoveredCount, deathCount)

        } else {

            var message = ""

            if (confirmedCount > 0)  {
                message += "<b>$confirmedCount</b> new $confirmedText"
            }

            if (recoveredCount > 0)  {
                message += if (message.isEmpty()) "<b>$recoveredCount</b> $recoveryText" else " and <b>$recoveredCount</b> $recoveryText"
            }

            if (deathCount > 0)  {
                message += if (message.isEmpty()) "<b>$deathCount</b> $deathText" else " and <b>$deathCount<b/> $deathText"
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