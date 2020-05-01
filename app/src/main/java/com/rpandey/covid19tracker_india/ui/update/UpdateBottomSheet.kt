package com.rpandey.covid19tracker_india.ui.update

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.rpandey.covid19tracker_india.BuildConfig
import com.rpandey.covid19tracker_india.data.model.LaunchData
import com.rpandey.covid19tracker_india.databinding.LayoutUpdateAvailableBinding
import com.rpandey.covid19tracker_india.ui.BaseBottomSheetFragment

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
            if (BuildConfig.VERSION_CODE < launchData.forceUpdate.minVersion)
                requireActivity().finish()
        }

        binding.download.setOnClickListener {
            openBrowser(launchData.downloadUrl)
        }
    }

    private fun openBrowser(downloadUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(downloadUrl)
        startActivity(intent)
    }
}

