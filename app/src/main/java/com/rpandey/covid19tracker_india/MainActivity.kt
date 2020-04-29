package com.rpandey.covid19tracker_india

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.google.firebase.analytics.FirebaseAnalytics
import com.rpandey.covid19tracker_india.data.Status
import com.rpandey.covid19tracker_india.data.processor.CovidIndiaDataProcessor
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase
import com.rpandey.covid19tracker_india.network.APIProvider
import com.rpandey.covid19tracker_india.network.NetworkBuilder
import com.rpandey.covid19tracker_india.util.ThemeHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var firebaseAnalytics: FirebaseAnalytics

    private val covidIndia by lazy {
        CovidIndiaDataProcessor(
            APIProvider(NetworkBuilder.apiService), CovidDatabase.getInstance(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        setSupportActionBar(toolbar)
        setupToolbarIcon()

        navView.setupWithNavController(navController)
        navView.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED

        startSync()

        appOpenEvent()
    }

    private fun appOpenEvent() {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Homepage")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle)
    }

    private fun setupToolbarIcon() {
        val currentTheme = ThemeHelper.getTheme(this)

        if (currentTheme == ThemeHelper.LIGHT_MODE) {
            iv_ui_mode.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_outline_nights_stay_24
                )
            )
            iv_ui_mode.setOnClickListener {
                ThemeHelper.applyTheme(ThemeHelper.DARK_MODE)
            }
        } else {
            iv_ui_mode.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_twotone_wb_sunny_24
                )
            )
            iv_ui_mode.setOnClickListener {
                ThemeHelper.applyTheme(ThemeHelper.LIGHT_MODE)
            }
        }

        iv_refresh.setOnClickListener {
            startSync()
        }

    }

    private fun startSync(callback: (Status<*>) -> Unit = {}) {
        showRefreshAnimation()
        CoroutineScope(Dispatchers.IO).launch {
            covidIndia.startSync {
                callback(it)
                if (it is Status.Error) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(
                            this@MainActivity,
                            "Oops! something went wrong, unable to update", Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun showRefreshAnimation() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.rotate_image)
        iv_refresh.startAnimation(animation)
    }
}