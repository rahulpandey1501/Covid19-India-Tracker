package com.rpandey.covid19tracker_india

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.rpandey.covid19tracker_india.data.Status
import com.rpandey.covid19tracker_india.data.StatusId
import com.rpandey.covid19tracker_india.data.model.LaunchData
import com.rpandey.covid19tracker_india.ui.BaseActivity
import com.rpandey.covid19tracker_india.ui.update.UpdateBottomSheet
import com.rpandey.covid19tracker_india.util.ThemeHelper
import com.rpandey.covid19tracker_india.util.showDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        setSupportActionBar(toolbar)
        setupToolbarIcon()

        navView.setupWithNavController(navController)
        navView.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED

        startSync()
    }

    private fun setupToolbarIcon() {
        iv_ui_mode.setOnClickListener {
            ThemeHelper.toggle(this)
        }
        iv_refresh.setOnClickListener {
            startSync()
        }
    }

    private fun startSync() {
        showRefreshAnimation()
        CoroutineScope(Dispatchers.IO).launch {
            dataProcessor.startSync {
                onSyncComplete(it)
            }
        }
    }

    private fun showRefreshAnimation() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.rotate_image)
        iv_refresh.startAnimation(animation)
    }

    private fun <T: Any?> onSyncComplete(status: Status<T>) {
        CoroutineScope(Dispatchers.Main).launch {
            when(status.statusId) {
                StatusId.LAUNCH_DATA -> {
                    if (status is Status.Success) {
                        processAppLaunchData(status.data as LaunchData)
                    }
                }
                StatusId.OVERALL_DATA -> {
                    if (status is Status.Error) {
                        Toast.makeText(this@MainActivity, "Oops! something went wrong, unable to update", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun processAppLaunchData(data: LaunchData) {
        if (BuildConfig.VERSION_CODE < data.latestVersion) {
            showDialog(UpdateBottomSheet.TAG) {
                UpdateBottomSheet.newInstance(data)
            }
        }
    }
}