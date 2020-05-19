package com.rpandey.covid19tracker_india.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase

abstract class BaseFragment : Fragment() {

    val repository by lazy {
        CovidIndiaRepository(CovidDatabase.getInstance())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getScreenName()?.let { logEvent(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
        if (setToolbarTitle().isNotEmpty()) {
            (activity as AppCompatActivity?)?.title = setToolbarTitle()
        }
    }

    open fun getScreenName(): String? = null

    open fun setToolbarTitle(): String = ""

    abstract fun observeLiveData()

    fun logEvent(event: String) {
        FirebaseAnalytics.getInstance(requireContext().applicationContext).logEvent(event, null)
    }
}