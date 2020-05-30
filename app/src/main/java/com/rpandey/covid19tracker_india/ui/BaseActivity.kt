package com.rpandey.covid19tracker_india.ui

import android.os.Bundle
import android.transition.Transition
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase

abstract class BaseActivity : AppCompatActivity() {

    val database by lazy { CovidDatabase.getInstance() }
    val repository by lazy { CovidIndiaRepository(database) }

    abstract fun getScreenName(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logEvent(getScreenName())
    }

    fun logEvent(event: String?) {
        event?.let { FirebaseAnalytics.getInstance(this).logEvent(event, null) }
    }

    open fun addTransitionListener(): Boolean {
        val transition = window.sharedElementEnterTransition
        if (transition != null) {
            // There is an entering shared element transition so add a listener to it
            transition.addListener(object : Transition.TransitionListener {
                override fun onTransitionEnd(transition: Transition) {
                    // As the transition has ended, we can now load the full-size image
                    onTransitionEnd()

                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this)
                }

                override fun onTransitionStart(transition: Transition?) {
                    // No-op
                }

                override fun onTransitionCancel(transition: Transition) {
                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this)
                }

                override fun onTransitionPause(transition: Transition?) {
                    // No-op
                }

                override fun onTransitionResume(transition: Transition?) {
                    // No-op
                }
            })
            return true
        }

        // If we reach here then we have not added a listener
        return false
    }

    open fun onTransitionEnd() {

    }
}