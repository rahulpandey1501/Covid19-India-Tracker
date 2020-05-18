package com.rpandey.covid19tracker_india

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.rpandey.covid19tracker_india.data.Status
import com.rpandey.covid19tracker_india.data.StatusId
import com.rpandey.covid19tracker_india.data.model.LaunchData
import com.rpandey.covid19tracker_india.ui.BaseActivity
import com.rpandey.covid19tracker_india.ui.aboutus.AboutUsActivity
import com.rpandey.covid19tracker_india.ui.search.SearchActivity
import com.rpandey.covid19tracker_india.ui.update.UpdateBottomSheet
import com.rpandey.covid19tracker_india.util.ThemeHelper
import com.rpandey.covid19tracker_india.util.Util
import com.rpandey.covid19tracker_india.util.showDialog
import com.rpandey.covid19tracker_india.util.showToast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : BaseActivity() {

    override fun getScreenName(): String = "Home"

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        setSupportActionBar(toolbar)
        setupPullToRefresh()
        initFCM()
    }

    private fun setupPullToRefresh() {
        onSynStart()
        pull_refresh.setOnRefreshListener {
            onSynStart()
        }
    }

    private fun onSynStart() {
        if (!pull_refresh.isRefreshing)
            pull_refresh.isRefreshing = true

        startSync()
    }

    private fun initFCM() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result?.token
            })

//        FirebaseMessaging.getInstance().subscribeToTopic("DEV_TESTING").addOnCompleteListener {
//            Log.d("Covid19 topic: ", it.isSuccessful.toString())
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
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
                onShareClicked()
            }

            R.id.ui_mode -> {
                ThemeHelper.toggle(this)
            }

            R.id.about_us -> {
                startActivity(Intent(this, AboutUsActivity::class.java))
            }

            R.id.exit -> {
                finish()
            }
        }
        return true
    }

    private fun onShareClicked() {
        logEvent("SHARE_CLICKED")
        val shareIntent = Util.shareAppIntent()
        startActivity(Intent.createChooser(shareIntent, "Share using..."))
    }

    private fun startSync(callback: (Status<*>) -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            dataProcessor.startSync {
                withContext(Dispatchers.Main) {
                    onStatusResult(it)
                    callback(it)
                }
            }
        }
    }

    private fun <T: Any?> onStatusResult(status: Status<T>) {
        when(status.statusId) {
            /* StatusId.LAUNCH_DATA -> { // commenting specially for huawei distribution
                if (status is Status.Success) {
                    processAppLaunchData(status.data as LaunchData)
                    showToast("Data successfully updated!")
                }
                pull_refresh.isRefreshing = false
            } */

            StatusId.OVERALL_DATA -> {
                if (status is Status.Error) {
                    showToast("Oops! something went wrong, unable to update")
                }
                if (status is Status.Success) {
                    showToast("Data successfully updated!")
                }
                pull_refresh.isRefreshing = false
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