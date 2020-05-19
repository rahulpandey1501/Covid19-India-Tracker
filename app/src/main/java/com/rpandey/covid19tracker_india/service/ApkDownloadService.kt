package com.rpandey.covid19tracker_india.service

import android.app.IntentService
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.rpandey.covid19tracker_india.CovidApplication
import com.rpandey.covid19tracker_india.data.model.LaunchData
import com.rpandey.covid19tracker_india.network.APIProvider
import com.rpandey.covid19tracker_india.util.Util
import com.rpandey.covid19tracker_india.util.Util.getAppDownloadDirectory
import com.rpandey.covid19tracker_india.util.Util.getAppDownloadFileName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ApkDownloadService: IntentService("Covid19ApkDownloaderHelper") {

    annotation class DownloadStatus {
        companion object {
            const val NOT_STARTED = 0
            const val DOWNLOADING = 1
            const val COMPLETED = 2
        }
    }

    companion object {

        const val ACTION = "DOWNLOAD_ACTION"
        const val KEY_SUCCESS = "KEY_SUCCESS"
        const val KEY_LAUNCH_DATA = "KEY_LAUNCH_DATA"

        private var downloadStatus = DownloadStatus.NOT_STARTED

        fun start(launchData: LaunchData) {
            val intent = Intent(CovidApplication.INSTANCE, ApkDownloadService::class.java)
            intent.putExtra(KEY_LAUNCH_DATA, Gson().toJson(launchData))
            CovidApplication.INSTANCE.startService(intent)
            Util.log("Download", "Job Trigger")
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        val launchData = intent?.getStringExtra(KEY_LAUNCH_DATA)
        if (launchData != null) {
            CoroutineScope(Dispatchers.IO).launch {
                startDownloadProcess(Gson().fromJson(launchData, LaunchData::class.java)) {
                    sendSuccessBroadcast(launchData)
                }
            }
        }
    }

    private suspend fun startDownloadProcess(launchData: LaunchData, successCallback: (File) -> Unit) {

        val versionCode = launchData.latestVersion

        var apkFile = Util.apkExist(versionCode)
        if (apkFile.first) {
            withContext(Dispatchers.Main) {
                successCallback(apkFile.second)
            }
            return
        }

        if (launchData.apkDownloadUrl.isNullOrEmpty() || downloadStatus == DownloadStatus.DOWNLOADING) {
            return
        }

        val downloadUrl = launchData.apkDownloadUrl!!

        downloadStatus = DownloadStatus.DOWNLOADING // downloading started

        deleteExistingAPKs(getAppDownloadDirectory())

        val downloadService = FileDownloadService(APIProvider.getInstance())
        downloadService.startDownloading(downloadUrl, getAppDownloadFileName(versionCode), getAppDownloadDirectory()) { success, file ->

            apkFile = Util.apkExist(versionCode)
            if (success && apkFile.first) {
                withContext(Dispatchers.Main) {
                    successCallback(apkFile.second)
                }
            }

            downloadStatus = DownloadStatus.COMPLETED
        }
    }

    private fun sendSuccessBroadcast(launchData: String) {
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(
            Intent(ACTION).apply {
                putExtra(KEY_SUCCESS, true)
                putExtra(KEY_LAUNCH_DATA, launchData)
            }
        )
    }

    private fun deleteExistingAPKs(apkDirectory: File) {
        apkDirectory.listFiles()?.forEach {
            it.delete()
        }
    }
}