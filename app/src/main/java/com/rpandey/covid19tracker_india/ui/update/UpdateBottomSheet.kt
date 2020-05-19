package com.rpandey.covid19tracker_india.ui.update

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.rpandey.covid19tracker_india.BuildConfig
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.data.model.LaunchData
import com.rpandey.covid19tracker_india.databinding.LayoutUpdateAvailableBinding
import com.rpandey.covid19tracker_india.ui.BaseBottomSheetFragment
import com.rpandey.covid19tracker_india.util.Util

class UpdateBottomSheet : BaseBottomSheetFragment() {

    companion object {
        const val TAG = "UpdateBottomSheet"
        private const val KEY_DATA = "KEY_DATA"

        fun newInstance(launchData: LaunchData): UpdateBottomSheet {
            return UpdateBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(KEY_DATA, Gson().toJson(launchData))
                }
            }
        }
    }

    private lateinit var binding: LayoutUpdateAvailableBinding

    override fun setUpDialogBehaviour(dialog: BottomSheetDialog, behaviour: BottomSheetBehavior<FrameLayout>) {
        behaviour.skipCollapsed = true
        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LayoutUpdateAvailableBinding.inflate(inflater, container, false)
        val launchData = Gson().fromJson(requireArguments().getString(KEY_DATA), LaunchData::class.java)
        initUi(launchData)
        binding.vm = launchData
        logEvent("AppUpdateUI")
        return binding.root
    }

    private fun initUi(launchData: LaunchData) {
        val currentVersion = BuildConfig.VERSION_CODE
        val forceUpdate = launchData.forceUpdate
        if (currentVersion < forceUpdate.minVersion) {
            isCancelable = false
        }

        binding.later.setOnClickListener {
            dismissAllowingStateLoss()
            logEvent("UPDATE_LATER_CLICKED")
            if (BuildConfig.VERSION_CODE < launchData.forceUpdate.minVersion) {
                requireActivity().finish()
            }
        }

        val downloadedAPK = Util.apkExist(launchData.latestVersion)
        val isApkExist = downloadedAPK.first && launchData.config?.autoDownloadEnabled == true
        binding.tvActionButton.text = if (isApkExist) getString(R.string.install) else getString(R.string.download)

        binding.download.setOnClickListener {
            if (isApkExist) {
                logEvent("UPDATE_INSTALL_CLICKED")
                Util.startInstallerIntent(requireContext(), downloadedAPK.second)
            } else {
                logEvent("UPDATE_DOWNLOAD_CLICKED")
                Util.openBrowser(requireContext(), launchData.downloadUrl)
            }
        }
    }
}

