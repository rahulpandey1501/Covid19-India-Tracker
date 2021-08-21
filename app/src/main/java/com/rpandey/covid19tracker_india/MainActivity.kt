package com.rpandey.covid19tracker_india

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.lifecycle.Lifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.rpandey.covid19tracker_india.data.Constants
import com.rpandey.covid19tracker_india.data.RequestId
import com.rpandey.covid19tracker_india.data.Status
import com.rpandey.covid19tracker_india.data.model.LaunchData
import com.rpandey.covid19tracker_india.data.processor.CovidIndiaSyncManager
import com.rpandey.covid19tracker_india.service.ApkDownloadService
import com.rpandey.covid19tracker_india.ui.BaseActivity
import com.rpandey.covid19tracker_india.ui.aboutus.AboutUsActivity
import com.rpandey.covid19tracker_india.ui.essentials.EssentialsActivity
import com.rpandey.covid19tracker_india.ui.help.HelpActivity
import com.rpandey.covid19tracker_india.ui.search.SearchActivity
import com.rpandey.covid19tracker_india.ui.update.UpdateBottomSheet
import com.rpandey.covid19tracker_india.util.ThemeHelper
import com.rpandey.covid19tracker_india.util.Util
import com.rpandey.covid19tracker_india.util.showDialog
import com.rpandey.covid19tracker_india.util.showToast
import com.rpandey.covid19tracker_india.widgets.updateAllAppWidget
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : BaseActivity() {

    override fun getScreenName(): String = "Home"

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appLaunchSync()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        setSupportActionBar(toolbar)
        setupPullToRefresh()
        initFCM()
    }

    private fun setupPullToRefresh() {
        pull_refresh.setOnRefreshListener {
            appLaunchSync()
        }
    }

    private fun appLaunchSync() {
        if (!pull_refresh.isRefreshing)
            pull_refresh.isRefreshing = true

        startSync()
    }

    private fun initFCM() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
            })

//        FirebaseMessaging.getInstance().subscribeToTopic("DEV_TESTING").addOnCompleteListener {
//            Log.d("Covid19 topic: ", it.isSuccessful.toString())
//        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        try {
            if (menu is MenuBuilder) {
                menu.setOptionalIconsVisible(true)
            }
        } catch (e: Exception) {}
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.search -> {
                startActivity(Intent(this, SearchActivity::class.java).apply {
                    putExtra(SearchActivity.KEY_VIEW_TYPE, SearchActivity.OVERALL_SEARCH_VIEW)
                })
            }

            R.id.share -> {
                logEvent("MAIN_STATS_CLICKED")
                val rootView = findViewById<View>(android.R.id.content)
                Util.shareScreenshot(rootView)
            }

            R.id.ui_mode -> {
                ThemeHelper.toggle(this)
            }

            R.id.essentials -> {
                startActivity(Intent(this, EssentialsActivity::class.java))
            }

            R.id.about_us -> {
                startActivity(Intent(this, AboutUsActivity::class.java))
            }

            R.id.worldwide -> {
                val url =  Util.getConfig()?.analysisUrl ?: Constants.DEFAULT_ANALYSIS_URL
                Util.openWebUrl(this, url, getString(R.string.worldwide))
            }

            R.id.add_widget -> {
                startActivity(Intent(this, HelpActivity::class.java))
            }
        }
        return true
    }

    private fun startSync(callback: (Status<*>) -> Unit = {}) {
        CovidIndiaSyncManager.getInstance().syncAllData {
            withContext(Dispatchers.Main) {
                onStatusResult(it)
                callback(it)
            }
        }
    }

    private fun <T : Any?> onStatusResult(status: Status<T>) {
        when (status.requestId) {

            RequestId.LAUNCH_DATA -> {
                if (status is Status.Success) {
                    processAppLaunchData(status.data as LaunchData)
                }
            }

            RequestId.OVERALL_DATA -> {
                if (status is Status.Success) {
                    updateAppWidget()
                }
                if (status is Status.Error) {
                    showToast("Oops! something went wrong \nUnable to update the data")
                }
                pull_refresh.isRefreshing = false
            }
        }
    }

    private fun updateAppWidget() = CoroutineScope(Dispatchers.IO).launch {
        delay(2000)
        updateAllAppWidget(CovidApplication.INSTANCE)
    }

    private fun processAppLaunchData(data: LaunchData) {
//        data.apkDownloadUrl = "https://docs.google.com/uc?export=download&id=1o-IGH-T000z3aByg-A1BgwQSrjSpSG3Z"
//        data.config?.autoDownloadEnabled = true

        if (BuildConfig.VERSION_CODE < data.latestVersion) {
            if (data.config?.autoDownloadEnabled == true) {
                ApkDownloadService.start(data)

            } else {
                showUpdateUI(data)
            }
        }
    }

    private fun showUpdateUI(data: LaunchData) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            showDialog(UpdateBottomSheet.TAG) {
                UpdateBottomSheet.newInstance(data)
            }
        }
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(receiver)
        super.onPause()
    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(receiver, IntentFilter(ApkDownloadService.ACTION))
        super.onResume()
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.getBooleanExtra(ApkDownloadService.KEY_SUCCESS, false) == true) {
                val launchData = intent.getStringExtra(ApkDownloadService.KEY_LAUNCH_DATA)
                showUpdateUI(Gson().fromJson(launchData, LaunchData::class.java))
            }
        }
    }
}